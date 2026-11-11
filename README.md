# Sistem automatizat de gestiune a parcării

Proiect pentru disciplina MSD – aplicație de business pentru o parcare auto complet automatizată, fără personal și fără tichete de hârtie.

Vehiculele sunt identificate după numărul de înmatriculare, citit de camere la intrare și la ieșire. Parcarea are o zonă pentru abonați (cu locuri rezervate), o zonă pentru șoferii ocazionali și locuri pentru persoane cu dizabilități. Șoferii plătesc la tonomat sau în aplicația mobilă, iar administratorul primește notificări și rapoarte.

## Stadiul proiectului

| Sprint | Tema | Stadiu |
| --- | --- | --- |
| Sprint 1 | Business Domain Design – use case-uri, model de domeniu, reguli de business | Finalizat |
| Sprint 2 | Business Model Implementation – entități Java, servicii, teste JUnit | În lucru |
| Sprint 3 | Persistence Model Implementation – JPA, repository-uri Spring | Planificat |

## Structura repository-ului

```
parcare-automatizata/
├── README.md                     ← această pagină
├── pom.xml                       ← configurarea proiectului Maven
├── docs/
│   └── sprint-1/                 ← documentația Sprint 1 (PDF + diagrama tabelelor)
└── src/
    ├── main/java/ro/parcare/parcare_automatizate/
    │   ├── enums/                ← valorile fixe (stări, tipuri)
    │   ├── model/                ← entitățile și value object-ul NumarInmatriculare
    │   ├── factory/              ← construirea agregatelor
    │   └── service/              ← regulile de business (calcul, validare, workflow)
    └── test/java/...             ← testele JUnit
```

## Cum rulezi testele

Deschide proiectul în IntelliJ IDEA (Java 21), apoi clic dreapta pe proiect → **Run 'All Tests'**.

## Tehnologii

- Java, Spring Boot (Sprint 2–3)
- JPA / Hibernate, Spring Data JPA (Sprint 3)
- JUnit (teste)
- UML pentru design, DBML ([dbdiagram.io](https://dbdiagram.io)) pentru diagrama tabelelor

## Autor

George – Sisteme Informaționale pentru Afaceri (SIA)
