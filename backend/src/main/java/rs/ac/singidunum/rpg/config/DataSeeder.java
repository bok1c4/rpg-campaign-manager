package rs.ac.singidunum.rpg.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.singidunum.rpg.entity.*;
import rs.ac.singidunum.rpg.repository.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Ubacuje bogate demo podatke pri prvom pokretanju (ako je baza prazna):
 * 2 Game Master-a, 6 igrača, 13 predmeta (svi tipovi i retkosti),
 * 3 kampanje, 11 likova sa inventarima (i GM nalozi imaju svoje likove) i 6 sesija.
 * Sve lozinke su "password123".
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CampaignRepository campaignRepository;
    private final GameCharacterRepository characterRepository;
    private final GameSessionRepository sessionRepository;
    private final ItemRepository itemRepository;
    private final CharacterItemRepository characterItemRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, CampaignRepository campaignRepository,
                      GameCharacterRepository characterRepository, GameSessionRepository sessionRepository,
                      ItemRepository itemRepository, CharacterItemRepository characterItemRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.campaignRepository = campaignRepository;
        this.characterRepository = characterRepository;
        this.sessionRepository = sessionRepository;
        this.itemRepository = itemRepository;
        this.characterItemRepository = characterItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // već seed-ovano
        }

        // ---------- Korisnici ----------
        User gm = user("gamemaster", "gm@singidunum.rs", Role.GAME_MASTER);
        User dungeonKeeper = user("dungeonkeeper", "dk@singidunum.rs", Role.GAME_MASTER);
        User aragorn = user("aragorn", "aragorn@singidunum.rs", Role.PLAYER);
        User legolas = user("legolas", "legolas@singidunum.rs", Role.PLAYER);
        User gimli = user("gimli", "gimli@singidunum.rs", Role.PLAYER);
        User gandalf = user("gandalf", "gandalf@singidunum.rs", Role.PLAYER);
        User frodo = user("frodo", "frodo@singidunum.rs", Role.PLAYER);
        User boromir = user("boromir", "boromir@singidunum.rs", Role.PLAYER);
        userRepository.saveAll(List.of(gm, dungeonKeeper, aragorn, legolas, gimli, gandalf, frodo, boromir));

        // ---------- Katalog predmeta (svi tipovi i retkosti) ----------
        Item anduril = item("Andúril", ItemType.WEAPON, Rarity.LEGENDARY, 1500, "Reforged blade of the West.");
        Item longbow = item("Elven Longbow", ItemType.WEAPON, Rarity.RARE, 400, "Carved from Lórien wood.");
        Item warhammer = item("Dwarven Warhammer", ItemType.WEAPON, Rarity.RARE, 350, "Heavy two-handed hammer.");
        Item sting = item("Sting", ItemType.WEAPON, Rarity.UNCOMMON, 200, "Glows blue when orcs are near.");
        Item staff = item("Staff of the Magi", ItemType.WEAPON, Rarity.LEGENDARY, 5000, "Absorbs and unleashes spells.");
        Item potion = item("Healing Potion", ItemType.POTION, Rarity.COMMON, 50, "Restores 2d4+2 hit points.");
        Item greaterPotion = item("Greater Healing Potion", ItemType.POTION, Rarity.UNCOMMON, 150, "Restores 4d4+4 hit points.");
        Item mithril = item("Mithril Shirt", ItemType.ARMOR, Rarity.EPIC, 2000, "Light as a feather, hard as dragon scales.");
        Item shield = item("Shield of Faith", ItemType.ARMOR, Rarity.UNCOMMON, 120, "A shimmering field of protection.");
        Item fireball = item("Scroll of Fireball", ItemType.SCROLL, Rarity.UNCOMMON, 150, "A 3rd-level fire spell, single use.");
        Item cloak = item("Cloak of Elvenkind", ItemType.TREASURE, Rarity.RARE, 800, "Grants advantage on stealth checks.");
        Item bag = item("Bag of Holding", ItemType.TREASURE, Rarity.RARE, 1000, "Holds far more than it should.");
        Item tools = item("Thieves' Tools", ItemType.MISC, Rarity.COMMON, 25, "Picks and files for locks and traps.");
        itemRepository.saveAll(List.of(anduril, longbow, warhammer, sting, staff, potion, greaterPotion,
                mithril, shield, fireball, cloak, bag, tools));

        // ---------- Kampanja 1 ----------
        Campaign lostMines = campaign("The Lost Mines of Singidunum",
                "A classic dungeon crawl beneath the old university halls.", "Forgotten Realms", gm,
                List.of(aragorn, legolas, gimli));

        // ---------- Kampanja 2 ----------
        Campaign tomb = campaign("Tomb of the Forgotten Compiler",
                "An ancient crypt where a legendary artifact of pure logic was sealed away.", "Homebrew", gm,
                List.of(gandalf, frodo));

        // ---------- Kampanja 3 (drugi GM) ----------
        Campaign shadows = campaign("Shadows over Belgrade",
                "Modern-day occult investigators uncover a conspiracy beneath the city.", "Urban Fantasy", dungeonKeeper,
                List.of(boromir, aragorn));

        campaignRepository.saveAll(List.of(lostMines, tomb, shadows));

        // ---------- Likovi ----------
        GameCharacter cAragorn = character("Aragorn", "Human", "Ranger", 5, 44, aragorn, lostMines);
        GameCharacter cLegolas = character("Legolas", "Elf", "Archer", 5, 38, legolas, lostMines);
        GameCharacter cGimli = character("Gimli", "Dwarf", "Fighter", 5, 52, gimli, lostMines);
        GameCharacter cGandalf = character("Gandalf the Grey", "Maia", "Wizard", 8, 60, gandalf, tomb);
        GameCharacter cFrodo = character("Frodo", "Hobbit", "Rogue", 4, 28, frodo, tomb);
        GameCharacter cBoromir = character("Boromir", "Human", "Paladin", 6, 58, boromir, shadows);
        GameCharacter cStrider = character("Strider", "Human", "Ranger", 3, 30, aragorn, shadows);
        GameCharacter cMira = character("Mira Nightshade", "Tiefling", "Warlock", 4, 32, boromir, shadows);
        // Likovi koje vode sami Game Master-i — da i GM nalozi imaju podatke u "Moji likovi"
        GameCharacter cRoland = character("Sir Roland", "Human", "Paladin", 7, 64, gm, lostMines);
        GameCharacter cVesna = character("Vesna the Seer", "Human", "Cleric", 6, 48, gm, tomb);
        GameCharacter cKovac = character("Inspektor Kovač", "Human", "Rogue", 5, 40, dungeonKeeper, shadows);
        characterRepository.saveAll(List.of(cAragorn, cLegolas, cGimli, cGandalf, cFrodo, cBoromir, cStrider, cMira,
                cRoland, cVesna, cKovac));

        // ---------- Inventari (ManyToMany lik <-> predmet) ----------
        characterItemRepository.saveAll(List.of(
                inventory(cAragorn, anduril, 1, true),
                inventory(cAragorn, potion, 3, false),
                inventory(cAragorn, cloak, 1, true),
                inventory(cLegolas, longbow, 1, true),
                inventory(cLegolas, fireball, 2, false),
                inventory(cGimli, warhammer, 1, true),
                inventory(cGimli, mithril, 1, true),
                inventory(cGimli, potion, 2, false),
                inventory(cGandalf, staff, 1, true),
                inventory(cGandalf, fireball, 3, false),
                inventory(cGandalf, greaterPotion, 1, false),
                inventory(cFrodo, sting, 1, true),
                inventory(cFrodo, bag, 1, false),
                inventory(cFrodo, tools, 1, false),
                inventory(cBoromir, shield, 1, true),
                inventory(cBoromir, greaterPotion, 2, false),
                inventory(cStrider, longbow, 1, true),
                inventory(cMira, tools, 1, true),
                inventory(cRoland, shield, 1, true),
                inventory(cRoland, greaterPotion, 2, false),
                inventory(cVesna, fireball, 2, false),
                inventory(cVesna, potion, 3, false),
                inventory(cKovac, sting, 1, true),
                inventory(cKovac, cloak, 1, true),
                inventory(cKovac, tools, 1, false)));

        // ---------- Sesije ----------
        sessionRepository.saveAll(List.of(
                session(lostMines, 1, "Goblin Ambush",
                        "The party is ambushed on the Triboar Trail and tracks the goblins to their lair.",
                        LocalDate.of(2026, 3, 1)),
                session(lostMines, 2, "Cragmaw Hideout",
                        "Exploring the goblin cave and rescuing the captured guide.",
                        LocalDate.of(2026, 3, 8)),
                session(lostMines, 3, "The Town of Phandalin",
                        "The heroes reach town and clash with the Redbrand ruffians.",
                        LocalDate.of(2026, 3, 15)),
                session(tomb, 1, "The Sealed Door",
                        "Deciphering the runes guarding the entrance to the tomb.",
                        LocalDate.of(2026, 3, 5)),
                session(tomb, 2, "Hall of Mirrors",
                        "Illusory guardians test the party's resolve.",
                        LocalDate.of(2026, 3, 12)),
                session(shadows, 1, "The Disappearance",
                        "A missing professor leads the investigators into the city's underground.",
                        LocalDate.of(2026, 4, 2))));
    }

    // ---------- helper-i ----------

    private User user(String username, String email, Role role) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode("password123"));
        u.setRole(role);
        return u;
    }

    private Item item(String name, ItemType type, Rarity rarity, int gold, String description) {
        Item i = new Item();
        i.setName(name);
        i.setItemType(type);
        i.setRarity(rarity);
        i.setGoldValue(gold);
        i.setDescription(description);
        return i;
    }

    private Campaign campaign(String title, String description, String setting, User gameMaster, List<User> players) {
        Campaign c = new Campaign();
        c.setTitle(title);
        c.setDescription(description);
        c.setSetting(setting);
        c.setGameMaster(gameMaster);
        c.getPlayers().addAll(players);
        return c;
    }

    private GameCharacter character(String name, String race, String clazz, int level, int hp,
                                   User player, Campaign campaign) {
        GameCharacter c = new GameCharacter();
        c.setName(name);
        c.setRace(race);
        c.setCharacterClass(clazz);
        c.setLevel(level);
        c.setHitPoints(hp);
        c.setPlayer(player);
        c.setCampaign(campaign);
        return c;
    }

    private CharacterItem inventory(GameCharacter character, Item item, int quantity, boolean equipped) {
        CharacterItem ci = new CharacterItem();
        ci.setCharacter(character);
        ci.setItem(item);
        ci.setQuantity(quantity);
        ci.setEquipped(equipped);
        return ci;
    }

    private GameSession session(Campaign campaign, int number, String title, String summary, LocalDate date) {
        GameSession s = new GameSession();
        s.setCampaign(campaign);
        s.setSessionNumber(number);
        s.setTitle(title);
        s.setSummary(summary);
        s.setPlayedOn(date);
        return s;
    }
}
