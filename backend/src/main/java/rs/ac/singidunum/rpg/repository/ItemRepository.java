package rs.ac.singidunum.rpg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.singidunum.rpg.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
