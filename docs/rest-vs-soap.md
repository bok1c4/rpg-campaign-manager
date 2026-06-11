# Priprema za usmeni — Web servisi (REST i SOAP), glave IX i X

Kratak podsetnik za odbranu. Predmetni nastavnik/asistent postavlja par pitanja iz ovih oblasti.

## Šta je web servis

Softverska komponenta dostupna preko mreže, koja omogućava komunikaciju i razmenu podataka
između aplikacija nezavisno od platforme i programskog jezika, najčešće preko HTTP-a.
Dve dominantne paradigme: **REST** i **SOAP**.

## REST (Representational State Transfer)

- **Arhitektonski stil** (nije protokol). Resursi se identifikuju **URI-jem**, a nad njima se rade
  operacije preko **HTTP metoda**: `GET` (čitanje), `POST` (kreiranje), `PUT/PATCH` (izmena),
  `DELETE` (brisanje).
- **Reprezentacije:** najčešće **JSON** (može i XML). Klijent i server razmenjuju reprezentaciju stanja resursa.
- **Principi (ograničenja):**
  - **Client–Server** — razdvojene odgovornosti.
  - **Stateless** — svaki zahtev sadrži sve potrebne informacije; server ne pamti sesiju.
    (Zato koristimo JWT — token nosi identitet u svakom zahtevu.)
  - **Cacheable** — odgovori mogu biti keširani.
  - **Uniform Interface** — jednoobrazan pristup resursima.
  - **Layered System** — slojevi (proksiji, gateway-i) transparentni za klijenta.
  - **Code on Demand** (opciono).
- **Statusni kodovi:** 200 OK, 201 Created, 204 No Content, 400 Bad Request, 401 Unauthorized,
  403 Forbidden, 404 Not Found, 409 Conflict, 500 Internal Server Error.
- **Prednosti:** jednostavnost, lakoća, JSON, dobra podrška u browseru/mobilnim aplikacijama, skalabilnost.

## SOAP (Simple Object Access Protocol)

- **Protokol** za razmenu poruka, zasnovan na **XML**-u. Poruka ima **Envelope** (Header + Body).
- Nezavisan od transporta (najčešće HTTP, može SMTP…).
- **WSDL** (Web Services Description Language) — XML opis servisa: operacije, tipovi, vezivanja, adresa.
- **UDDI** — registar za pronalaženje servisa (u praksi se retko koristi).
- **WS-* standardi:** WS-Security, WS-ReliableMessaging, WS-AtomicTransaction — formalna podrška za
  bezbednost, pouzdanost i transakcije.
- **Prednosti:** strogo definisan ugovor (WSDL), ugrađena bezbednost/pouzdanost, pogodan za
  enterprise i striktne integracije (banke, telekom).
- **Mane:** verboznost (XML), složenost, sporiji.

## REST vs SOAP — sažeto

| | REST | SOAP |
|---|---|---|
| Tip | arhitektonski stil | protokol |
| Format | JSON (najčešće), XML | isključivo XML |
| Ugovor | OpenAPI/Swagger (opciono) | WSDL (obavezan) |
| Stanje | stateless | može stateful (WS-*) |
| Transport | HTTP | HTTP, SMTP… |
| Bezbednost | HTTPS + token (npr. JWT) | WS-Security (+ HTTPS) |
| Kada | web/mobilni API-ji, javni servisi | enterprise, strogi ugovori i transakcije |

## Veza sa ovim projektom

Projekat je **REST**: resursi `campaigns`, `characters`, `sessions`, `items`, `users`; standardne
HTTP metode i statusni kodovi; **stateless** uz **JWT**; JSON reprezentacije; dokumentacija kroz
**Swagger UI** (`/swagger-ui.html`) i **Postman** kolekciju.
