package ro.parcare.parcare_automatizate.factory;

import org.springframework.stereotype.Component;
import ro.parcare.parcare_automatizate.model.NumarInmatriculare;
import ro.parcare.parcare_automatizate.model.SesiuneParcare;

import java.time.LocalDateTime;

@Component
public class SesiuneFactoryImpl implements SesiuneFactory {

    private long urmatorulId = 1;

    @Override
    public SesiuneParcare creeazaSesiune(NumarInmatriculare nrAuto, LocalDateTime oraIntrare, Long idLoc) {
        // constructorul pune deja starea ACTIVA
        SesiuneParcare sesiune = new SesiuneParcare(nrAuto, oraIntrare, idLoc);
        sesiune.setId(urmatorulId);
        urmatorulId++;
        sesiune.setDataCreare(LocalDateTime.now());
        return sesiune;
    }
}
