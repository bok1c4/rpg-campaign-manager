package rs.ac.singidunum.rpg.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.singidunum.rpg.dto.ItemDtos;
import rs.ac.singidunum.rpg.entity.Item;
import rs.ac.singidunum.rpg.exception.ResourceNotFoundException;
import rs.ac.singidunum.rpg.repository.ItemRepository;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional(readOnly = true)
    public List<ItemDtos.Response> list() {
        return itemRepository.findAll().stream().map(Mapper::toItem).toList();
    }

    @Transactional(readOnly = true)
    public ItemDtos.Response get(Long id) {
        return Mapper.toItem(find(id));
    }

    @Transactional
    public ItemDtos.Response create(ItemDtos.CreateRequest req) {
        Item i = new Item();
        apply(i, req.name(), req);
        return Mapper.toItem(itemRepository.save(i));
    }

    @Transactional
    public ItemDtos.Response update(Long id, ItemDtos.UpdateRequest req) {
        Item i = find(id);
        i.setName(req.name());
        i.setItemType(req.itemType());
        i.setRarity(req.rarity());
        i.setGoldValue(req.goldValue());
        i.setDescription(req.description());
        return Mapper.toItem(itemRepository.save(i));
    }

    @Transactional
    public void delete(Long id) {
        itemRepository.delete(find(id));
    }

    private void apply(Item i, String name, ItemDtos.CreateRequest req) {
        i.setName(name);
        i.setItemType(req.itemType());
        i.setRarity(req.rarity());
        i.setGoldValue(req.goldValue());
        i.setDescription(req.description());
    }

    private Item find(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Predmet", id));
    }
}
