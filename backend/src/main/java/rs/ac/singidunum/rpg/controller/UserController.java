package rs.ac.singidunum.rpg.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.singidunum.rpg.dto.UserDtos;
import rs.ac.singidunum.rpg.repository.UserRepository;
import rs.ac.singidunum.rpg.service.Mapper;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Lista svih korisnika — koristi GM da bira igrače za party roster kampanje. */
    @GetMapping
    @PreAuthorize("hasRole('GAME_MASTER')")
    public List<UserDtos.Response> list() {
        return userRepository.findAll().stream().map(Mapper::toUser).toList();
    }
}
