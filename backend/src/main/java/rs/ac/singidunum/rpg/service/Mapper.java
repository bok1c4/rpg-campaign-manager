package rs.ac.singidunum.rpg.service;

import rs.ac.singidunum.rpg.dto.*;
import rs.ac.singidunum.rpg.entity.*;

/** Konverzija entiteta u DTO-ove. Kontroleri nikad ne vraćaju entitete direktno. */
public final class Mapper {

    private Mapper() {
    }

    public static UserDtos.Response toUser(User u) {
        return new UserDtos.Response(u.getId(), u.getUsername(), u.getEmail(), u.getRole().name());
    }

    public static ItemDtos.Response toItem(Item i) {
        return new ItemDtos.Response(i.getId(), i.getName(), i.getItemType(), i.getRarity(),
                i.getGoldValue(), i.getDescription());
    }

    public static InventoryDtos.Response toInventory(CharacterItem ci) {
        return new InventoryDtos.Response(ci.getId(), toItem(ci.getItem()), ci.getQuantity(), ci.isEquipped());
    }

    public static SessionDtos.Response toSession(GameSession s) {
        return new SessionDtos.Response(s.getId(), s.getCampaign().getId(), s.getSessionNumber(),
                s.getTitle(), s.getSummary(), s.getPlayedOn());
    }

    public static CharacterDtos.Response toCharacter(GameCharacter c) {
        return new CharacterDtos.Response(c.getId(), c.getName(), c.getRace(), c.getCharacterClass(),
                c.getLevel(), c.getHitPoints(), c.getBackstory(),
                c.getCampaign().getId(), c.getCampaign().getTitle(),
                toUser(c.getPlayer()), c.getInventory().size());
    }

    public static CampaignDtos.Response toCampaign(Campaign c) {
        return new CampaignDtos.Response(c.getId(), c.getTitle(), c.getDescription(), c.getSetting(),
                toUser(c.getGameMaster()), c.getCharacters().size(), c.getSessions().size(),
                c.getPlayers().stream().map(Mapper::toUser).toList());
    }
}
