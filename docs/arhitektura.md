# Arhitektura i model podataka

## Pregled

REST API (Spring Boot) + SPA (React). Backend je slojevit:

```
HTTP  →  Controller  →  Service  →  Repository  →  Baza (PostgreSQL)
                 ↑           ↑
               DTO        Entity (JPA)
         (ulaz/izlaz)  (mapiranje na tabele)

Presek: Security (JWT filter), Exception handling (@RestControllerAdvice)
```

- **Controller** — prima HTTP zahteve, validira ulaz (`@Valid`), vraća DTO (nikad entitet).
- **Service** — poslovna logika, transakcije (`@Transactional`), mapiranje entitet ↔ DTO (`Mapper`).
- **Repository** — Spring Data JPA (`JpaRepository`), bez ručnog SQL-a.
- **Entity** — JPA mapiranje na tabele; relacije OneToMany / ManyToMany / ManyToOne.
- **Security** — `JwtAuthenticationFilter` čita `Bearer` token, `JwtService` ga generiše/validira,
  autorizacija po rolama (`@PreAuthorize`).

## Model podataka (7 tabela)

| Tabela | Opis | Ključne relacije |
|---|---|---|
| `users` | korisnici (role: GAME_MASTER / PLAYER) | — |
| `campaigns` | kampanje | ManyToOne → users (gameMaster) |
| `characters` | likovi | ManyToOne → campaigns, ManyToOne → users (player) |
| `game_sessions` | sesije igre | ManyToOne → campaigns |
| `items` | katalog predmeta | — |
| `character_items` | inventar (spojni entitet) | ManyToOne → characters, ManyToOne → items |
| `campaign_players` | party roster (spojna tabela) | ManyToMany campaigns ↔ users |

### Relacije

- **OneToMany / ManyToOne**
  - `Campaign` 1—N `GameCharacter` (`campaign_id`)
  - `Campaign` 1—N `GameSession` (`campaign_id`)
  - `User` (GM) 1—N `Campaign` (`game_master_id`)
  - `User` (player) 1—N `GameCharacter` (`player_id`)
- **ManyToMany**
  - `GameCharacter` N—N `Item` — preko spojnog **entiteta** `CharacterItem` koji nosi
    atribute `quantity` i `equipped`.
  - `Campaign` N—N `User` (igrači) — preko **`@ManyToMany` + `@JoinTable`** (`campaign_players`).

## Autentifikacija i autorizacija

- Login/registracija vraćaju **access token** (15 min) i **refresh token** (7 dana), oba HS256 JWT.
- Access token nosi `sub` (username), `role` i `type=access`.
- `JwtAuthenticationFilter` na svaki zahtev validira potpis i rok i puni `SecurityContext`.
- Autorizacija:
  - Mutacije nad `campaigns`, `sessions`, `items` i party rosterom → **GAME_MASTER**
    (`@PreAuthorize("hasRole('GAME_MASTER')")`).
  - Likove i njihov inventar može da menja **vlasnik (player) ili GAME_MASTER**
    (provera u servisu, `CurrentUserService.requireOwnerOrGameMaster`).
- **Refresh flow:** frontend (`api/client.js`) na `401` automatski poziva `/api/auth/refresh`,
  smeni access token i ponovi originalni zahtev (axios interceptor, single-flight).

## Pakovanje (Docker)

Multi-stage `Dockerfile`:
1. `node` build React-a → `dist/`
2. `maven` build Spring Boot-a; `dist/` se kopira u `src/main/resources/static` (jedan jar servira oba)
3. `eclipse-temurin:21-jre` pokreće jar

`docker-compose.yml` diže `db` (PostgreSQL) i `app`. Pokretanje: `docker compose up --build`.
