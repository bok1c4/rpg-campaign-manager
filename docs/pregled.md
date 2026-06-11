# Pregled projekta — šta trenutno imamo

Brza mapa aplikacije za korisnika i developera. (Ovo NIJE uputstvo za nadogradnju — samo snimak
trenutnog stanja.)

## Šta je ovo

Web aplikacija za vođenje **tabletop RPG kampanja** (D&D-stil). Dve vrste korisnika:

- **Game Master (GM)** — kreira i upravlja kampanjama, sesijama igre i katalogom predmeta; vodi
  „party roster“ (ko igra u kojoj kampanji).
- **Player (igrač)** — kreira svoje likove, dodaje ih u kampanje i upravlja njihovim inventarom.

Tehnički: **REST API (Spring Boot)** + **React SPA**, **JWT** prijava, **PostgreSQL** baza, sve se
pokreće sa `docker compose up --build` na **http://localhost:8080**.

## Da li ima admin panel?

**Nema poseban „admin panel“ kao zasebnu stranicu.** Umesto toga:

- **Uloga GAME_MASTER je de-facto admin** — isti UI, ali su mu vidljive i dozvoljene dodatne akcije
  (kreiranje/izmena/brisanje kampanja, sesija, predmeta; dodavanje/uklanjanje igrača iz partyja).
  Igraču su te akcije sakrivene u UI-ju i odbijene na backendu (`403`).
- Za **developera/administraciju API-ja** postoji **Swagger UI** na
  **http://localhost:8080/swagger-ui.html** — tu se vide i isprobavaju svi endpoint-i (klikni
  „Authorize“ i nalepi access token). To je najbliže „admin/dev alatu“ što imamo.
- Direktan pristup bazi: PostgreSQL je izložen na **localhost:5433** (user/pass/db: `rpg`/`rpg`/`rpgdb`).

## Nalozi (svi: lozinka `password123`)

| Username | Uloga | Napomena |
|---|---|---|
| `gamemaster` | GAME_MASTER | glavni demo GM (2 kampanje) + 2 lika u „Moji likovi“ |
| `dungeonkeeper` | GAME_MASTER | drugi GM (1 kampanja) + 1 lik |
| `aragorn` | PLAYER | ima 2 lika u 2 kampanje |
| `legolas`, `gimli` | PLAYER | u kampanji „Lost Mines“ |
| `gandalf`, `frodo` | PLAYER | u kampanji „Tomb…“ |
| `boromir` | PLAYER | u kampanji „Shadows…“ |

Možeš i da se **registruješ** (`/register`) — novi nalog uvek dobija ulogu PLAYER.

## Stranice (šta korisnik vidi)

| Stranica | Ruta | Šta radi |
|---|---|---|
| Prijava / Registracija | `/login`, `/register` | JWT prijava; demo kredencijali su pred-popunjeni |
| **Kampanje** | `/campaigns` | lista svih kampanja; GM ima „+ Nova“, „Izmeni“, „Obriši“ |
| **Detalji kampanje** | `/campaigns/:id` | party (igrači), likovi, sesije; GM upravlja svime |
| **Moji likovi** | `/characters` | likovi trenutnog korisnika; kreiranje/izmena/brisanje |
| **Lik / Inventar** | `/characters/:id` | podaci o liku + inventar (dodaj/skini/opremi/izbaci predmet) |
| **Predmeti** | `/items` | katalog predmeta; GM ima CRUD, igrač samo gleda |

Navbar prikazuje tvoje ime i ulogu; GM-only dugmad se ne prikazuju igraču.

## Tipičan tok korišćenja

**Kao GM (`gamemaster`):**
1. Prijava → „Kampanje“ → „+ Nova kampanja“.
2. Otvori kampanju → dodaj igrače u party, dodaj sesije.
3. „Predmeti“ → dodaj nove predmete u katalog.

**Kao Player (`aragorn`):**
1. Prijava → „Moji likovi“ → „+ Novi lik“ (izaberi kampanju).
2. Otvori lika → „Inventar“ → dodaj predmete iz kataloga, opremi ih, menjaj količinu.
3. „Kampanje“ → pogledaj sesije i ostatak partyja.

## Šta je trenutno u bazi (seed podaci za testiranje)

- **8 korisnika** (2 GM + 6 igrača), **13 predmeta** (svi tipovi: WEAPON/ARMOR/POTION/SCROLL/TREASURE/MISC
  i sve retkosti: COMMON→LEGENDARY).
- **3 kampanje**: „The Lost Mines of Singidunum“, „Tomb of the Forgotten Compiler“, „Shadows over Belgrade“.
- **11 likova** sa popunjenim inventarima, **6 sesija**. Svaki demo nalog (i GM-ovi i igrači) ima
  bar jednog lika u „Moji likovi“ — `gamemaster` ima „Sir Roland“ i „Vesna the Seer“,
  `dungeonkeeper` ima „Inspektor Kovač“.
- Seed se ubacuje samo ako je baza prazna. Reset na čisto: `docker compose down -v && docker compose up`.

## Dev mapa — backend

Slojevi: `controller → service → repository → entity (+ dto, security, config, exception)`.
Paket: `rs.ac.singidunum.rpg`.

**Tabele (7):** `users`, `campaigns`, `characters`, `game_sessions`, `items`,
`character_items` (inventar, M:N sa atributima), `campaign_players` (party, čista M:N).

**Glavni endpoint-i** (svi pod `/api`, JSON, JWT u `Authorization: Bearer`):

| Resurs | Endpoint-i | Ko sme da menja |
|---|---|---|
| Auth | `POST /auth/register`, `/auth/login`, `/auth/refresh` | javno |
| Campaigns | `GET/POST/PUT/DELETE /campaigns[/{id}]`, `…/players/{userId}` | GM za izmene |
| Characters | `GET/POST/PUT/DELETE /characters[/{id}]`, `/characters/mine` | vlasnik ili GM |
| Inventory | `GET/POST/PUT/DELETE /characters/{id}/items[/{entryId}]` | vlasnik ili GM |
| Sessions | `GET /campaigns/{id}/sessions`, `…/sessions`, `PUT/DELETE /sessions/{id}` | GM za izmene |
| Items | `GET/POST/PUT/DELETE /items[/{id}]` | GM za izmene |
| Users | `GET /users` | GM |

Puna interaktivna lista: **Swagger UI** ili Postman kolekcija u `docs/postman/`.

## Granice trenutnog stanja (čega NEMA)

Namerno van obima (da ne komplikujemo, a da pokrijemo bodove):

- Nema posebne „admin“ stranice za upravljanje korisnicima kroz UI (samo `GET /users` za GM-a;
  promena uloge/brisanje naloga se ne radi kroz aplikaciju).
- Nema upload-a slika/avatara, nema paginacije/pretrage, nema „zaboravljena lozinka“.
- Refresh token je stateless JWT (nema server-side revoke liste).
- Lik se dodaje u kampanju ali ne postoji provera članstva u partyju pri kreiranju lika
  (bilo koji igrač može napraviti lika u bilo kojoj postojećoj kampanji).
