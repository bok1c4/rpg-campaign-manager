package rs.ac.singidunum.rpg.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.singidunum.rpg.dto.SessionDtos;
import rs.ac.singidunum.rpg.service.SessionService;

import java.util.List;

@RestController
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/api/campaigns/{campaignId}/sessions")
    public List<SessionDtos.Response> listByCampaign(@PathVariable Long campaignId) {
        return sessionService.listByCampaign(campaignId);
    }

    @PostMapping("/api/campaigns/{campaignId}/sessions")
    @PreAuthorize("hasRole('GAME_MASTER')")
    public ResponseEntity<SessionDtos.Response> create(@PathVariable Long campaignId,
                                                       @Valid @RequestBody SessionDtos.CreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.create(campaignId, req));
    }

    @GetMapping("/api/sessions/{id}")
    public SessionDtos.Response get(@PathVariable Long id) {
        return sessionService.get(id);
    }

    @PutMapping("/api/sessions/{id}")
    @PreAuthorize("hasRole('GAME_MASTER')")
    public SessionDtos.Response update(@PathVariable Long id, @Valid @RequestBody SessionDtos.UpdateRequest req) {
        return sessionService.update(id, req);
    }

    @DeleteMapping("/api/sessions/{id}")
    @PreAuthorize("hasRole('GAME_MASTER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sessionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
