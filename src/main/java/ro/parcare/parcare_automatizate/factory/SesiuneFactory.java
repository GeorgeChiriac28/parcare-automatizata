package ro.parcare.parcare_automatizate.factory;

import ro.parcare.parcare_automatizate.model.NumarInmatriculare;
import ro.parcare.parcare_automatizate.model.SesiuneParcare;

import java.time.LocalDateTime;

// Construieste agregatul SesiuneParcare cand o masina intra in parcare.
public interface SesiuneFactory {

    SesiuneParcare creeazaSesiune(NumarInmatriculare nrAuto, LocalDateTime oraIntrare, Long idLoc);
}
