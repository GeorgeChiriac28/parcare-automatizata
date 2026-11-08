package ro.parcare.parcare_automatizate.factory;

import ro.parcare.parcare_automatizate.model.Abonament;
import ro.parcare.parcare_automatizate.model.NumarInmatriculare;
import ro.parcare.parcare_automatizate.model.PerioadaAbonament;

import java.math.BigDecimal;
import java.time.LocalDate;

// Construieste agregatul Abonament, impreuna cu prima luna platita.
public interface AbonamentFactory {

    Abonament creeazaAbonament(NumarInmatriculare nrAuto, Long idLoc, boolean reinnoireAutomata,
                               LocalDate dataInceput, BigDecimal pret);

    // pentru reinnoire: perioada care incepe a doua zi dupa ultima perioada
    PerioadaAbonament creeazaPerioadaUrmatoare(Abonament abonament, BigDecimal pret);
}
