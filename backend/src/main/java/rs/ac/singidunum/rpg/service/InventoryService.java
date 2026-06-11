package rs.ac.singidunum.rpg.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.singidunum.rpg.dto.InventoryDtos;
import rs.ac.singidunum.rpg.entity.CharacterItem;
import rs.ac.singidunum.rpg.entity.GameCharacter;
import rs.ac.singidunum.rpg.entity.Item;
import rs.ac.singidunum.rpg.exception.ForbiddenException;
import rs.ac.singidunum.rpg.exception.ResourceNotFoundException;
import rs.ac.singidunum.rpg.repository.CharacterItemRepository;
import rs.ac.singidunum.rpg.repository.GameCharacterRepository;
import rs.ac.singidunum.rpg.repository.ItemRepository;

import java.util.List;

@Service
public class InventoryService {

    private final CharacterItemRepository characterItemRepository;
    private final GameCharacterRepository characterRepository;
    private final ItemRepository itemRepository;
    private final CurrentUserService currentUser;

    public InventoryService(CharacterItemRepository characterItemRepository,
                            GameCharacterRepository characterRepository, ItemRepository itemRepository,
                            CurrentUserService currentUser) {
        this.characterItemRepository = characterItemRepository;
        this.characterRepository = characterRepository;
        this.itemRepository = itemRepository;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public List<InventoryDtos.Response> list(Long characterId) {
        findCharacter(characterId);
        return characterItemRepository.findByCharacterId(characterId)
                .stream().map(Mapper::toInventory).toList();
    }

    @Transactional
    public InventoryDtos.Response add(Long characterId, InventoryDtos.AddItemRequest req) {
        GameCharacter character = findCharacter(characterId);
        currentUser.requireOwnerOrGameMaster(character.getPlayer().getId());
        Item item = itemRepository.findById(req.itemId())
                .orElseThrow(() -> ResourceNotFoundException.of("Predmet", req.itemId()));

        int qty = req.quantity() == null ? 1 : req.quantity();
        // ako lik već ima taj predmet, samo uvećaj količinu (unique constraint character+item)
        CharacterItem entry = characterItemRepository
                .findByCharacterIdAndItemId(characterId, item.getId())
                .orElseGet(() -> {
                    CharacterItem ci = new CharacterItem();
                    ci.setCharacter(character);
                    ci.setItem(item);
                    ci.setQuantity(0);
                    return ci;
                });
        entry.setQuantity(entry.getQuantity() + qty);
        if (req.equipped() != null) {
            entry.setEquipped(req.equipped());
        }
        return Mapper.toInventory(characterItemRepository.save(entry));
    }

    @Transactional
    public InventoryDtos.Response update(Long characterId, Long entryId, InventoryDtos.UpdateEntryRequest req) {
        CharacterItem entry = findEntry(characterId, entryId);
        currentUser.requireOwnerOrGameMaster(entry.getCharacter().getPlayer().getId());
        if (req.quantity() != null) entry.setQuantity(req.quantity());
        if (req.equipped() != null) entry.setEquipped(req.equipped());
        return Mapper.toInventory(characterItemRepository.save(entry));
    }

    @Transactional
    public void remove(Long characterId, Long entryId) {
        CharacterItem entry = findEntry(characterId, entryId);
        currentUser.requireOwnerOrGameMaster(entry.getCharacter().getPlayer().getId());
        characterItemRepository.delete(entry);
    }

    private GameCharacter findCharacter(Long characterId) {
        return characterRepository.findById(characterId)
                .orElseThrow(() -> ResourceNotFoundException.of("Lik", characterId));
    }

    private CharacterItem findEntry(Long characterId, Long entryId) {
        CharacterItem entry = characterItemRepository.findById(entryId)
                .orElseThrow(() -> ResourceNotFoundException.of("Stavka inventara", entryId));
        if (!entry.getCharacter().getId().equals(characterId)) {
            throw new ForbiddenException("Stavka ne pripada zadatom liku.");
        }
        return entry;
    }
}
