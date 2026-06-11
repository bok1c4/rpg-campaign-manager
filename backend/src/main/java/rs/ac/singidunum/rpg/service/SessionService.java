package rs.ac.singidunum.rpg.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.singidunum.rpg.dto.SessionDtos;
import rs.ac.singidunum.rpg.entity.Campaign;
import rs.ac.singidunum.rpg.entity.GameSession;
import rs.ac.singidunum.rpg.exception.ResourceNotFoundException;
import rs.ac.singidunum.rpg.repository.CampaignRepository;
import rs.ac.singidunum.rpg.repository.GameSessionRepository;

import java.util.List;

@Service
public class SessionService {

    private final GameSessionRepository sessionRepository;
    private final CampaignRepository campaignRepository;

    public SessionService(GameSessionRepository sessionRepository, CampaignRepository campaignRepository) {
        this.sessionRepository = sessionRepository;
        this.campaignRepository = campaignRepository;
    }

    @Transactional(readOnly = true)
    public List<SessionDtos.Response> listByCampaign(Long campaignId) {
        return sessionRepository.findByCampaignIdOrderBySessionNumberAsc(campaignId)
                .stream().map(Mapper::toSession).toList();
    }

    @Transactional(readOnly = true)
    public SessionDtos.Response get(Long id) {
        return Mapper.toSession(find(id));
    }

    @Transactional
    public SessionDtos.Response create(Long campaignId, SessionDtos.CreateRequest req) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> ResourceNotFoundException.of("Kampanja", campaignId));
        GameSession s = new GameSession();
        s.setCampaign(campaign);
        s.setSessionNumber(req.sessionNumber());
        s.setTitle(req.title());
        s.setSummary(req.summary());
        s.setPlayedOn(req.playedOn());
        return Mapper.toSession(sessionRepository.save(s));
    }

    @Transactional
    public SessionDtos.Response update(Long id, SessionDtos.UpdateRequest req) {
        GameSession s = find(id);
        s.setSessionNumber(req.sessionNumber());
        s.setTitle(req.title());
        s.setSummary(req.summary());
        s.setPlayedOn(req.playedOn());
        return Mapper.toSession(sessionRepository.save(s));
    }

    @Transactional
    public void delete(Long id) {
        sessionRepository.delete(find(id));
    }

    private GameSession find(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Sesija", id));
    }
}
