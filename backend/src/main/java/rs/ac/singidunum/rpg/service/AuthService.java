package rs.ac.singidunum.rpg.service;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.singidunum.rpg.dto.AuthDtos;
import rs.ac.singidunum.rpg.entity.Role;
import rs.ac.singidunum.rpg.entity.User;
import rs.ac.singidunum.rpg.exception.ConflictException;
import rs.ac.singidunum.rpg.repository.UserRepository;
import rs.ac.singidunum.rpg.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new ConflictException("Korisničko ime je već zauzeto.");
        }
        if (userRepository.existsByEmail(req.email())) {
            throw new ConflictException("Email je već registrovan.");
        }
        User user = new User();
        user.setUsername(req.username());
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setRole(Role.PLAYER);
        userRepository.save(user);
        return tokensFor(user);
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest req) {
        // baca BadCredentialsException ako su podaci pogrešni -> 401 (GlobalExceptionHandler)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        User user = userRepository.findByUsername(req.username()).orElseThrow();
        return tokensFor(user);
    }

    public AuthDtos.AuthResponse refresh(AuthDtos.RefreshRequest req) {
        try {
            if (!jwtService.isTokenType(req.refreshToken(), JwtService.REFRESH)) {
                throw new JwtException("Token nije refresh tipa.");
            }
            String username = jwtService.extractUsername(req.refreshToken());
            User user = userRepository.findByUsername(username).orElseThrow();
            return tokensFor(user);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Nevažeći ili istekao refresh token.");
        }
    }

    private AuthDtos.AuthResponse tokensFor(User user) {
        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);
        return new AuthDtos.AuthResponse(access, refresh, user.getUsername(), user.getRole().name());
    }
}
