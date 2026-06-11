package rs.ac.singidunum.rpg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.singidunum.rpg.entity.GameCharacter;

import java.util.List;

public interface GameCharacterRepository extends JpaRepository<GameCharacter, Long> {
    List<GameCharacter> findByPlayerId(Long playerId);
    List<GameCharacter> findByCampaignId(Long campaignId);
}
