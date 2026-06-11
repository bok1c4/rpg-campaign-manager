package rs.ac.singidunum.rpg.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import rs.ac.singidunum.rpg.entity.Role;
import rs.ac.singidunum.rpg.entity.User;
import rs.ac.singidunum.rpg.exception.ForbiddenException;
import rs.ac.singidunum.rpg.exception.ResourceNotFoundException;
import rs.ac.singidunum.rpg.repository.UserRepository;

/** Pomoćni servis za dohvatanje trenutno ulogovanog korisnika i proveru vlasništva. */
@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ResourceNotFoundException("Nema ulogovanog korisnika.");
        }
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Trenutni korisnik nije pronađen."));
    }

    public boolean isGameMaster() {
        return get().getRole() == Role.GAME_MASTER;
    }

    /** Dozvoljava akciju samo vlasniku resursa ili Game Master-u. */
    public void requireOwnerOrGameMaster(Long ownerUserId) {
        User current = get();
        if (current.getRole() != Role.GAME_MASTER && !current.getId().equals(ownerUserId)) {
            throw new ForbiddenException("Možeš upravljati samo svojim resursima.");
        }
    }
}
