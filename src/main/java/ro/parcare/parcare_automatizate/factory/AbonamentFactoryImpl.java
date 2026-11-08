package ro.parcare.parcare_automatizate.factory;

import org.springframework.stereotype.Component;
import ro.parcare.parcare_automatizate.enums.StarePlata;
import ro.parcare.parcare_automatizate.model.Abonament;
import ro.parcare.parcare_automatizate.model.NumarInmatriculare;
import ro.parcare.parcare_automatizate.model.PerioadaAbonament;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class AbonamentFactoryImpl implements AbonamentFactory {

    private long urmatorulId = 1;

    @Override
    public Abonament creeazaAbonament(NumarInmatriculare nrAuto, Long idLoc, boolean reinnoireAutomata,
                                      LocalDate dataInceput, BigDecimal pret) {
        Abonament abonament = new Abonament(nrAuto, idLoc, reinnoireAutomata);
        abonament.setId(urmatorulId);
        urmatorulId++;

        abonament.adaugaPerioada(creeazaPerioada(dataInceput, pret));
        abonament.setDataCreare(LocalDateTime.now());
        return abonament;
    }

    @Override
    public PerioadaAbonament creeazaPerioadaUrmatoare(Abonament abonament, BigDecimal pret) {
        List<PerioadaAbonament> perioade = abonament.getPerioade();
        PerioadaAbonament ultima = perioade.get(perioade.size() - 1);
        LocalDate inceput = ultima.getDataSfarsit().plusDays(1);
        return creeazaPerioada(inceput, pret);
    }

    // o luna: de exemplu 1 noiembrie - 30 noiembrie
    private PerioadaAbonament creeazaPerioada(LocalDate inceput, BigDecimal pret) {
        LocalDate sfarsit = inceput.plusMonths(1).minusDays(1);
        return new PerioadaAbonament(inceput, sfarsit, pret, StarePlata.REUSITA);
    }
}
