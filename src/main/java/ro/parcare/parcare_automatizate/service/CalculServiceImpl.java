package ro.parcare.parcare_automatizate.service;

import org.springframework.stereotype.Service;
import ro.parcare.parcare_automatizate.enums.StarePlata;
import ro.parcare.parcare_automatizate.enums.TipPerioada;
import ro.parcare.parcare_automatizate.enums.TipZi;
import ro.parcare.parcare_automatizate.model.Notificare;
import ro.parcare.parcare_automatizate.model.Parcare;
import ro.parcare.parcare_automatizate.model.Plata;
import ro.parcare.parcare_automatizate.model.SesiuneParcare;
import ro.parcare.parcare_automatizate.model.Tarif;
import ro.parcare.parcare_automatizate.model.ZiSpeciala;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalculServiceImpl implements CalculService {

    private static final int MINUTE_PE_ORA = 60;
    private static final int MINUTE_PE_ZI = 24 * 60;

    @Override
    public TipZi tipulZilei(LocalDate data, List<ZiSpeciala> zileSpeciale) {
        // mai intai verificam daca e o zi cu eveniment
        for (ZiSpeciala zi : zileSpeciale) {
            if (zi.getData().equals(data)) {
                return TipZi.EVENIMENT;
            }
        }
        DayOfWeek ziuaSaptamanii = data.getDayOfWeek();
        if (ziuaSaptamanii == DayOfWeek.SATURDAY || ziuaSaptamanii == DayOfWeek.SUNDAY) {
            return TipZi.WEEKEND;
        }
        return TipZi.LUCRATOARE;
    }

    @Override
    public Tarif gasesteTarif(Parcare parcare, LocalDate data) {
        TipZi tipZi = tipulZilei(data, parcare.getZileSpeciale());
        for (Tarif tarif : parcare.getTarife()) {
            if (tarif.getTipZi() == tipZi) {
                return tarif;
            }
        }
        throw new IllegalStateException("Parcarea nu are tarif pentru tipul de zi " + tipZi);
    }

    @Override
    public long oreFacturate(LocalDateTime inceput, LocalDateTime sfarsit) {
        long minute = Duration.between(inceput, sfarsit).toMinutes();
        // orice ora inceputa se plateste intreaga: 1 h 10 min = 2 ore
        long ore = (long) Math.ceil(minute / (double) MINUTE_PE_ORA);
        // se plateste cel putin o ora
        if (ore < 1) {
            ore = 1;
        }
        return ore;
    }

    @Override
    public BigDecimal sumaPentruOre(Tarif tarif, LocalDateTime inceput, LocalDateTime sfarsit) {
        long minute = Duration.between(inceput, sfarsit).toMinutes();

        // zilele intregi se platesc cu pretul pe zi
        long zileIntregi = minute / MINUTE_PE_ZI;
        BigDecimal suma = tarif.getPretZi().multiply(BigDecimal.valueOf(zileIntregi));

        // pentru timpul ramas platim ore, dar nu mai mult decat pretul pe o zi
        long minuteRamase = minute % MINUTE_PE_ZI;
        if (minuteRamase > 0 || zileIntregi == 0) {
            long ore = oreFacturate(inceput, inceput.plusMinutes(minuteRamase));
            BigDecimal sumaRest = tarif.getPretOra().multiply(BigDecimal.valueOf(ore));
            if (sumaRest.compareTo(tarif.getPretZi()) > 0) {
                sumaRest = tarif.getPretZi();
            }
            suma = suma.add(sumaRest);
        }
        return suma;
    }

    @Override
    public BigDecimal sumaPentruPerioada(Tarif tarif, TipPerioada tipPerioada,
                                         LocalDateTime oraIntrare, LocalDateTime acum) {
        if (tipPerioada == TipPerioada.ZI) {
            return tarif.getPretZi();
        }
        if (tipPerioada == TipPerioada.SAPTAMANA) {
            return tarif.getPretSaptamana();
        }
        // pe ore: se plateste timpul stat pana acum
        return sumaPentruOre(tarif, oraIntrare, acum);
    }

    @Override
    public LocalDateTime valabilPana(TipPerioada tipPerioada, LocalDateTime oraIntrare, LocalDateTime acum) {
        if (tipPerioada == TipPerioada.ZI) {
            return oraIntrare.plusDays(1);
        }
        if (tipPerioada == TipPerioada.SAPTAMANA) {
            return oraIntrare.plusWeeks(1);
        }
        // pe ore: pana la sfarsitul ultimei ore platite
        long ore = oreFacturate(oraIntrare, acum);
        return oraIntrare.plusHours(ore);
    }

    @Override
    public BigDecimal taxaIntarziere(Tarif tarif, TipPerioada tipPerioada) {
        // C4: la plata pe ore taxa este pretul pe o zi,
        // la plata pe zi sau saptamana taxa este pretul pe o saptamana
        if (tipPerioada == TipPerioada.ORA) {
            return tarif.getPretZi();
        }
        return tarif.getPretSaptamana();
    }

    @Override
    public BigDecimal incasariPeZi(List<SesiuneParcare> sesiuni, LocalDate zi) {
        BigDecimal total = BigDecimal.ZERO;
        for (SesiuneParcare sesiune : sesiuni) {
            for (Plata plata : sesiune.getPlati()) {
                boolean reusita = plata.getStare() == StarePlata.REUSITA;
                boolean dinZiuaCeruta = plata.getData().toLocalDate().equals(zi);
                if (reusita && dinZiuaCeruta) {
                    total = total.add(plata.getSuma());
                }
            }
        }
        return total;
    }

    @Override
    public int notificariNerezolvate(List<Notificare> notificari) {
        int numar = 0;
        for (Notificare notificare : notificari) {
            if (!notificare.isRezolvata()) {
                numar++;
            }
        }
        return numar;
    }
}
