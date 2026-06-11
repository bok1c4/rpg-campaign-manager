package rs.ac.singidunum.rpg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.singidunum.rpg.entity.Campaign;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
}
