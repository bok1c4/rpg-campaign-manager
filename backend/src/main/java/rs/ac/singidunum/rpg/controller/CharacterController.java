package rs.ac.singidunum.rpg.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.singidunum.rpg.dto.CharacterDtos;
import rs.ac.singidunum.rpg.service.CharacterService;

import java.util.List;

@RestController
@RequestMapping("/api/characters")
public class CharacterController {

    private final CharacterService characterService;

    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @GetMapping
    public List<CharacterDtos.Response> list(@RequestParam(required = false) Long campaignId) {
        return characterService.list(campaignId);
    }

    @GetMapping("/mine")
    public List<CharacterDtos.Response> mine() {
        return characterService.listMine();
    }

    @GetMapping("/{id}")
    public CharacterDtos.Response get(@PathVariable Long id) {
        return characterService.get(id);
    }

    @PostMapping
    public ResponseEntity<CharacterDtos.Response> create(@Valid @RequestBody CharacterDtos.CreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(characterService.create(req));
    }

    @PutMapping("/{id}")
    public CharacterDtos.Response update(@PathVariable Long id, @Valid @RequestBody CharacterDtos.UpdateRequest req) {
        return characterService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        characterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
