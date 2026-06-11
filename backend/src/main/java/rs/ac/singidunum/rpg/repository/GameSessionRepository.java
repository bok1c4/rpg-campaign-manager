package rs.ac.singidunum.rpg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.singidunum.rpg.entity.GameSession;

import java.util.List;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    List<GameSession> findByCampaignIdOrderBySessionNumberAsc(Long campaignId);
}
