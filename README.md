# RPG Campaign Manager

Završni projekat iz kursa **Internet softverske arhitekture** (Univerzitet Singidunum).

Aplikacija za vođenje **tabletop RPG kampanja** (npr. Dungeons & Dragons): Game Master kreira
kampanje, sesije igre i katalog predmeta; igrači kreiraju svoje likove, pridružuju se kampanjama i
upravljaju inventarom svojih likova.

Realizovano kao **REST API (Spring Boot)** + **SPA frontend (React)**, sa **JWT** autentifikacijom
(access + refresh token), autorizacijom preko rola i automatskim osvežavanjem tokena.

---

## Pokretanje (jedna komanda — ne treba ti lokalno instalirana Java/Maven/Node)

Potreban je samo **Docker** (Desktop) sa pokrenutim demonom.

```bash
docker compose up --build
```

Kad se podigne, otvori: **http://localhost:8080**

- Frontend (React) i backend (REST API) se serviraju sa istog porta `8080`.
- PostgreSQL baza se podiže automatski kao zaseban kontejner.
- Build (Node + Maven) se odvija **unutar Docker-a** — ništa ne instaliraš na svom računaru.

Zaustavljanje: `Ctrl+C`, pa `docker compose down` (dodaj `-v` da obrišeš i bazu).

### Demo nalozi (seed-uju se automatski pri prvom pokretanju)

| Uloga | Username | Password |
|---|---|---|
| Game Master (admin) | `gamemaster` | `password123` |
| Player (korisnik) | `aragorn` | `password123` |

## Korisni linkovi (kad app radi)

- Aplikacija: http://localhost:8080
- Swagger UI (interaktivna API dokumentacija): http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Mapiranje na bodovanje (30/30)

| Bodovi | Stavka | Gde se vidi |
|---|---|---|
| 5 | [BE] Arhitektura | slojeviti Spring Boot: `controller → service → repository → entity` |
| 5 | [BE]+[FE] CRUD za 1 tabelu | pun CRUD nad `campaigns`, `characters`, `sessions`, `items` |
| 5 | [BE] OneToMany + ManyToMany | `campaign→characters/sessions`; `characters↔items`, `campaigns↔players` |
| 4 | [BE] JWT generisanje + validacija | `security/JwtService`, `JwtAuthenticationFilter` |
| 4 | [FE] Autorizacija preko rola | role-zaštićene rute, GM-only akcije skrivene za igrače |
| 4 | [FE] Automatsko osvežavanje tokena | axios interceptor (`/api/auth/refresh`) |
| 3 | [DOC] Postman dokumentacija | `docs/postman/` kolekcija sa sačuvanim primerima odgovora |

## Struktura

```
rpg-campaign-manager/
├── docker-compose.yml        # db (Postgres) + app (Spring Boot servira React build)
├── Dockerfile                # multi-stage: Node build → Maven build → JRE runtime
├── backend/                  # Spring Boot REST API  ([BE] repo)
├── frontend/                 # React + Vite SPA       ([FE] repo)
└── docs/
    ├── postman/              # Postman kolekcija + environment
    ├── pregled.md            # ⭐ brza mapa: šta imamo, kako se koristi, nalozi, granice
    ├── arhitektura.md        # opis arhitekture i modela podataka
    └── rest-vs-soap.md       # priprema za usmeni (glave IX i X)
```

> Za brzi pregled „šta sve imamo i kako se koristi“ pogledaj **`docs/pregled.md`** — tamo je i pun
> spisak demo naloga (8 korisnika) i odgovor na pitanje da li postoji admin panel.

## Tehnologije

- **Backend:** Java 21, Spring Boot 3.4, Spring Web, Spring Data JPA, Spring Security, JJWT, springdoc-openapi
- **Baza:** PostgreSQL 16
- **Frontend:** React 18, Vite, React Router, Axios
- **Build/Run:** Docker (multi-stage), Docker Compose

## Predaja (GitHub + Postman)

Instrukcije traže linkove za `[BE]` repo, `[FE]` repo i Postman dokumentaciju.

- Ovaj projekat je **monorepo** radi lakšeg razvoja. Za predaju možeš ili dati isti link za `[BE]`
  i `[FE]` (uz napomenu da su u `/backend` i `/frontend`), ili razdvojiti u dva GitHub repozitorijuma.
- Postman kolekcija je u `docs/postman/` — uvezi je u Postman i objavi (Publish) da dobiješ javni link.
- Ako je repo privatan, dodaj `bpapaz@singimail.rs` kao kolaboratora.
