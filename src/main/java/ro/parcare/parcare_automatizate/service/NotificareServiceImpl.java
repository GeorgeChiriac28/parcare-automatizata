package ro.parcare.parcare_automatizate.service;

import org.springframework.stereotype.Service;
import ro.parcare.parcare_automatizate.enums.TipNotificare;
import ro.parcare.parcare_automatizate.model.Notificare;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificareServiceImpl implements NotificareService {

    // pana in Sprint 3 tinem notificarile intr-o lista in memorie
    private final List<Notificare> notificari = new ArrayList<>();
    private long urmatorulId = 1;

    @Override
    public Notificare trimite(TipNotificare tip, String mesaj, LocalDateTime data) {
        Notificare notificare = new Notificare(tip, mesaj, data);
        notificare.setId(urmatorulId);
        urmatorulId++;
        notificari.add(notificare);
        return notificare;
    }

    @Override
    public List<Notificare> getNotificari() {
        return notificari;
    }

    @Override
    public List<Notificare> nerezolvate() {
        List<Notificare> rezultat = new ArrayList<>();
        for (Notificare notificare : notificari) {
            if (!notificare.isRezolvata()) {
                rezultat.add(notificare);
            }
        }
        return rezultat;
    }

    @Override
    public void marcheazaRezolvata(Long idNotificare) {
        for (Notificare notificare : notificari) {
            if (notificare.getId().equals(idNotificare)) {
                notificare.setRezolvata(true);
            }
        }
    }
}
