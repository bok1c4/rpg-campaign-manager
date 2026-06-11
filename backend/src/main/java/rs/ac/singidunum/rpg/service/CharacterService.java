package rs.ac.singidunum.rpg.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.singidunum.rpg.dto.CharacterDtos;
import rs.ac.singidunum.rpg.entity.Campaign;
import rs.ac.singidunum.rpg.entity.GameCharacter;
import rs.ac.singidunum.rpg.exception.ResourceNotFoundException;
import rs.ac.singidunum.rpg.repository.CampaignRepository;
import rs.ac.singidunum.rpg.repository.GameCharacterRepository;

import java.util.List;

@Service
public class CharacterService {

    private final GameCharacterRepository characterRepository;
    private final CampaignRepository campaignRepository;
    private final CurrentUserService currentUser;

    public CharacterService(GameCharacterRepository characterRepository,
                            CampaignRepository campaignRepository, CurrentUserService currentUser) {
        this.characterRepository = characterRepository;
        this.campaignRepository = campaignRepository;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public List<CharacterDtos.Response> list(Long campaignId) {
        List<GameCharacter> characters = (campaignId == null)
                ? characterRepository.findAll()
                : characterRepository.findByCampaignId(campaignId);
        return characters.stream().map(Mapper::toCharacter).toList();
    }

    @Transactional(readOnly = true)
    public List<CharacterDtos.Response> listMine() {
        return characterRepository.findByPlayerId(currentUser.get().getId())
                .stream().map(Mapper::toCharacter).toList();
    }

    @Transactional(readOnly = true)
    public CharacterDtos.Response get(Long id) {
        return Mapper.toCharacter(find(id));
    }

    @Transactional
    public CharacterDtos.Response create(CharacterDtos.CreateRequest req) {
        Campaign campaign = campaignRepository.findById(req.campaignId())
                .orElseThrow(() -> ResourceNotFoundException.of("Kampanja", req.campaignId()));

        GameCharacter c = new GameCharacter();
        c.setName(req.name());
        c.setRace(req.race());
        c.setCharacterClass(req.characterClass());
        c.setLevel(req.level() == null ? 1 : req.level());
        c.setHitPoints(req.hitPoints() == null ? 10 : req.hitPoints());
        c.setBackstory(req.backstory());
        c.setCampaign(campaign);
        c.setPlayer(currentUser.get()); // vlasnik je trenutni korisnik
        return Mapper.toCharacter(characterRepository.save(c));
    }

    @Transactional
    public CharacterDtos.Response update(Long id, CharacterDtos.UpdateRequest req) {
        GameCharacter c = find(id);
        currentUser.requireOwnerOrGameMaster(c.getPlayer().getId());
        c.setName(req.name());
        c.setRace(req.race());
        c.setCharacterClass(req.characterClass());
        if (req.level() != null) c.setLevel(req.level());
        if (req.hitPoints() != null) c.setHitPoints(req.hitPoints());
        c.setBackstory(req.backstory());
        return Mapper.toCharacter(characterRepository.save(c));
    }

    @Transactional
    public void delete(Long id) {
        GameCharacter c = find(id);
        currentUser.requireOwnerOrGameMaster(c.getPlayer().getId());
        characterRepository.delete(c);
    }

    private GameCharacter find(Long id) {
        return characterRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Lik", id));
    }
}
