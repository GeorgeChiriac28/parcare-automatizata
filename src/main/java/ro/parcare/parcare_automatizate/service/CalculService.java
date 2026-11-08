package ro.parcare.parcare_automatizate.service;

import ro.parcare.parcare_automatizate.enums.TipPerioada;
import ro.parcare.parcare_automatizate.enums.TipZi;
import ro.parcare.parcare_automatizate.model.Notificare;
import ro.parcare.parcare_automatizate.model.Parcare;
import ro.parcare.parcare_automatizate.model.SesiuneParcare;
import ro.parcare.parcare_automatizate.model.Tarif;
import ro.parcare.parcare_automatizate.model.ZiSpeciala;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Regulile de calcul din documentul Sprint 1 (C1 - C7).
public interface CalculService {

    // C3: tipul zilei (lucratoare, weekend sau eveniment)
    TipZi tipulZilei(LocalDate data, List<ZiSpeciala> zileSpeciale);

    // tariful parcarii pentru o anumita zi
    Tarif gasesteTarif(Parcare parcare, LocalDate data);

    // C1: cate ore se platesc
    long oreFacturate(LocalDateTime inceput, LocalDateTime sfarsit);

    // C2: suma pentru plata pe ore, cu plafonul pe 24 de ore
    BigDecimal sumaPentruOre(Tarif tarif, LocalDateTime inceput, LocalDateTime sfarsit);

    // suma pentru perioada aleasa de sofer (ora, zi sau saptamana)
    BigDecimal sumaPentruPerioada(Tarif tarif, TipPerioada tipPerioada, LocalDateTime oraIntrare, LocalDateTime acum);

    // pana cand este platita parcarea dupa o plata
    LocalDateTime valabilPana(TipPerioada tipPerioada, LocalDateTime oraIntrare, LocalDateTime acum);

    // C4: taxa de intarziere
    BigDecimal taxaIntarziere(Tarif tarif, TipPerioada tipPerioada);

    // C7: incasarile dintr-o zi (doar platile reusite)
    BigDecimal incasariPeZi(List<SesiuneParcare> sesiuni, LocalDate zi);

    // C7: cate notificari nu au fost rezolvate
    int notificariNerezolvate(List<Notificare> notificari);
}
