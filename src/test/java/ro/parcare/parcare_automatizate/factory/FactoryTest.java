package ro.parcare.parcare_automatizate.factory;

import org.junit.jupiter.api.Test;
import ro.parcare.parcare_automatizate.enums.StareSesiune;
import ro.parcare.parcare_automatizate.enums.TipLoc;
import ro.parcare.parcare_automatizate.enums.TipZona;
import ro.parcare.parcare_automatizate.model.Abonament;
import ro.parcare.parcare_automatizate.model.NumarInmatriculare;
import ro.parcare.parcare_automatizate.model.Parcare;
import ro.parcare.parcare_automatizate.model.PerioadaAbonament;
import ro.parcare.parcare_automatizate.model.SesiuneParcare;
import ro.parcare.parcare_automatizate.model.Zona;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FactoryTest {

    @Test
    void parcareaAreZoneleSiLocurileCerute() {
        ParcareFactory factory = new ParcareFactoryImpl();
        Parcare parcare = factory.creeazaParcare("Parcare Centru", "Str. Lapusneanu 10", 10, 8, 2, 70);

        assertEquals(20, parcare.getCapacitate());
        assertEquals(2, parcare.getZone().size());
        assertEquals(10, parcare.locuriLibere(TipZona.ABONATI));
        assertEquals(10, parcare.locuriLibere(TipZona.OCAZIONALI));
    }

    @Test
    void zonaOcazionalilorAreLocuriPentruDizabilitati() {
        ParcareFactory factory = new ParcareFactoryImpl();
        Parcare parcare = factory.creeazaParcare("Parcare Centru", "Str. Lapusneanu 10", 10, 8, 2, 70);

        Zona zonaOcazionali = parcare.getZone().get(1);
        int locuriDizabilitati = 0;
        for (int i = 0; i < zonaOcazionali.getLocuri().size(); i++) {
            if (zonaOcazionali.getLocuri().get(i).getTip() == TipLoc.DIZABILITATI) {
                locuriDizabilitati++;
            }
        }
        assertEquals(2, locuriDizabilitati);
    }

    @Test
    void parcareaPrimesteTarifeleImplicite() {
        ParcareFactory factory = new ParcareFactoryImpl();
        Parcare parcare = factory.creeazaParcare("Parcare Centru", "Str. Lapusneanu 10", 10, 8, 2, 70);

        assertEquals(3, parcare.getTarife().size());
        assertNotNull(parcare.getDataCreare());
    }

    @Test
    void sesiuneaNouaEsteActiva() {
        SesiuneFactory factory = new SesiuneFactoryImpl();
        SesiuneParcare sesiune = factory.creeazaSesiune(new NumarInmatriculare("IS 12 ABC"),
                LocalDateTime.of(2026, 11, 10, 9, 0), 5L);

        assertEquals(StareSesiune.ACTIVA, sesiune.getStare());
        assertEquals(5L, sesiune.getIdLoc());
    }

    @Test
    void abonamentulNouAreOLunaPlatita() {
        AbonamentFactory factory = new AbonamentFactoryImpl();
        Abonament abonament = factory.creeazaAbonament(new NumarInmatriculare("IS 12 ABC"), 1L, true,
                LocalDate.of(2026, 11, 1), new BigDecimal("500.00"));

        PerioadaAbonament perioada = abonament.getPerioade().get(0);
        assertEquals(LocalDate.of(2026, 11, 30), perioada.getDataSfarsit());
        assertTrue(abonament.esteValabil(LocalDate.of(2026, 11, 20)));
    }

    @Test
    void perioadaUrmatoareIncepeDupaUltima() {
        AbonamentFactory factory = new AbonamentFactoryImpl();
        Abonament abonament = factory.creeazaAbonament(new NumarInmatriculare("IS 12 ABC"), 1L, true,
                LocalDate.of(2026, 11, 1), new BigDecimal("500.00"));

        PerioadaAbonament urmatoarea = factory.creeazaPerioadaUrmatoare(abonament, new BigDecimal("500.00"));
        assertEquals(LocalDate.of(2026, 12, 1), urmatoarea.getDataInceput());
        assertEquals(LocalDate.of(2026, 12, 31), urmatoarea.getDataSfarsit());
    }
}
