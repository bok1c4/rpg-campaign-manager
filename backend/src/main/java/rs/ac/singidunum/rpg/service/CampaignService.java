package rs.ac.singidunum.rpg.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.singidunum.rpg.dto.CampaignDtos;
import rs.ac.singidunum.rpg.dto.UserDtos;
import rs.ac.singidunum.rpg.entity.Campaign;
import rs.ac.singidunum.rpg.entity.User;
import rs.ac.singidunum.rpg.exception.ResourceNotFoundException;
import rs.ac.singidunum.rpg.repository.CampaignRepository;
import rs.ac.singidunum.rpg.repository.UserRepository;

import java.util.List;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUser;

    public CampaignService(CampaignRepository campaignRepository, UserRepository userRepository,
                           CurrentUserService currentUser) {
        this.campaignRepository = campaignRepository;
        this.userRepository = userRepository;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public List<CampaignDtos.Response> list() {
        return campaignRepository.findAll().stream().map(Mapper::toCampaign).toList();
    }

    @Transactional(readOnly = true)
    public CampaignDtos.Response get(Long id) {
        return Mapper.toCampaign(find(id));
    }

    @Transactional
    public CampaignDtos.Response create(CampaignDtos.CreateRequest req) {
        Campaign c = new Campaign();
        c.setTitle(req.title());
        c.setDescription(req.description());
        c.setSetting(req.setting());
        c.setGameMaster(currentUser.get());
        return Mapper.toCampaign(campaignRepository.save(c));
    }

    @Transactional
    public CampaignDtos.Response update(Long id, CampaignDtos.UpdateRequest req) {
        Campaign c = find(id);
        c.setTitle(req.title());
        c.setDescription(req.description());
        c.setSetting(req.setting());
        return Mapper.toCampaign(campaignRepository.save(c));
    }

    @Transactional
    public void delete(Long id) {
        campaignRepository.delete(find(id));
    }

    @Transactional(readOnly = true)
    public List<UserDtos.Response> listPlayers(Long campaignId) {
        return find(campaignId).getPlayers().stream().map(Mapper::toUser).toList();
    }

    @Transactional
    public List<UserDtos.Response> addPlayer(Long campaignId, Long userId) {
        Campaign c = find(campaignId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Korisnik", userId));
        c.getPlayers().add(user);
        campaignRepository.save(c);
        return c.getPlayers().stream().map(Mapper::toUser).toList();
    }

    @Transactional
    public void removePlayer(Long campaignId, Long userId) {
        Campaign c = find(campaignId);
        c.getPlayers().removeIf(u -> u.getId().equals(userId));
        campaignRepository.save(c);
    }

    private Campaign find(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Kampanja", id));
    }
}
