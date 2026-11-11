# Sprint 2 – Business Model Implementation

Implementarea în Java și Spring Boot a modelului de domeniu și a regulilor de business din [Sprint 1](../sprint-1/README.md).

Tot codul se află în `src/main/java/ro/parcare/parcare_automatizate/`, iar testele în `src/test/java/ro/parcare/parcare_automatizate/`.

## Livrabile cerute și unde se găsesc

| Livrabil | Unde |
| --- | --- |
| Clase de entități | `model/` – 11 entități: `Parcare`, `Zona`, `LocParcare`, `Tarif`, `ZiSpeciala`, `SesiuneParcare`, `Plata`, `Abonament`, `PerioadaAbonament`, `Vehicul`, `Notificare` |
| Value objects | `model/NumarInmatriculare` și cele 11 enumerări din `enums/` |
| Validări JavaBean (adnotări) | În entități: `@NotNull`, `@NotBlank`, `@Positive`, `@Min`, `@Max`, `@AssertTrue` (regulile V1–V8) |
| Factory-uri pentru agregate | `factory/` – `ParcareFactory`, `SesiuneFactory`, `AbonamentFactory` (interfață + implementare) |
| Servicii de workflow | `service/ParcareWorkflowService` (UC1–UC4), `service/AbonamentWorkflowService` (UC5), `service/AdministrareWorkflowService` (UC6–UC8) |
| Serviciu de validare | `service/ValidareService` – regulile pe agregate A1–A8, plus V2 și V3 |
| Serviciu de calcul | `service/CalculService` – regulile C1–C7 (facturare pe ore, plafon pe zi, taxă de întârziere, rapoarte) |
| Serviciu de audit | `service/AuditService` și interfața `model/Auditabil` |
| Clasă de configurare Spring | `config/AppConfig` |
| Teste JUnit | `src/test/java/...` – 68 de teste |

## Agregate

| Agregat | Entitate root | Componente |
| --- | --- | --- |
| Parcare | `Parcare` | `Zona` cu `LocParcare`, `Tarif`, `ZiSpeciala` |
| SesiuneParcare | `SesiuneParcare` | `Plata` |
| Abonament | `Abonament` | `PerioadaAbonament` |
| Vehicul | `Vehicul` | – |
| Notificare | `Notificare` | – |

## Teste

| Clasă de test | Ce verifică |
| --- | --- |
| `NumarInmatriculareTest` | Formatul numărului de înmatriculare (V1) |
| `EntitatiTest` | Validările din entități (V2, V5–V8) și metodele simple din entități |
| `FactoryTest` | Construirea agregatelor |
| `CalculServiceTest` | Regulile de calcul C1–C7 |
| `ValidareServiceTest` | Regulile de validare A1–A8, V2, V3 |
| `ParcareWorkflowServiceTest` | Intrare, plată, ieșire, cele 15 minute de grație, taxa de întârziere |
| `AbonamentWorkflowServiceTest` | Cumpărarea, reînnoirea și expirarea abonamentelor |
| `AdministrareWorkflowServiceTest` | Senzori, notificări, tarife, zile de eveniment, rapoarte |
| `ParcareAutomatizateApplicationTests` | Pornirea aplicației Spring cu toate serviciile |

## Observație

Până în Sprint 3 datele sunt ținute în liste în memorie, iar id-urile sunt date de factory-uri. În Sprint 3 vor fi înlocuite cu persistența JPA și repository-uri Spring.
