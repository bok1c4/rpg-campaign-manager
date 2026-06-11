package rs.ac.singidunum.rpg.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.singidunum.rpg.dto.CampaignDtos;
import rs.ac.singidunum.rpg.dto.UserDtos;
import rs.ac.singidunum.rpg.service.CampaignService;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping
    public List<CampaignDtos.Response> list() {
        return campaignService.list();
    }

    @GetMapping("/{id}")
    public CampaignDtos.Response get(@PathVariable Long id) {
        return campaignService.get(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('GAME_MASTER')")
    public ResponseEntity<CampaignDtos.Response> create(@Valid @RequestBody CampaignDtos.CreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(campaignService.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GAME_MASTER')")
    public CampaignDtos.Response update(@PathVariable Long id, @Valid @RequestBody CampaignDtos.UpdateRequest req) {
        return campaignService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GAME_MASTER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        campaignService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/players")
    public List<UserDtos.Response> listPlayers(@PathVariable Long id) {
        return campaignService.listPlayers(id);
    }

    @PostMapping("/{id}/players/{userId}")
    @PreAuthorize("hasRole('GAME_MASTER')")
    public List<UserDtos.Response> addPlayer(@PathVariable Long id, @PathVariable Long userId) {
        return campaignService.addPlayer(id, userId);
    }

    @DeleteMapping("/{id}/players/{userId}")
    @PreAuthorize("hasRole('GAME_MASTER')")
    public ResponseEntity<Void> removePlayer(@PathVariable Long id, @PathVariable Long userId) {
        campaignService.removePlayer(id, userId);
        return ResponseEntity.noContent().build();
    }
}
