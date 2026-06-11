package rs.ac.singidunum.rpg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.singidunum.rpg.entity.CharacterItem;

import java.util.List;
import java.util.Optional;

public interface CharacterItemRepository extends JpaRepository<CharacterItem, Long> {
    List<CharacterItem> findByCharacterId(Long characterId);
    Optional<CharacterItem> findByCharacterIdAndItemId(Long characterId, Long itemId);
}
