package rs.ac.singidunum.rpg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.singidunum.rpg.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
