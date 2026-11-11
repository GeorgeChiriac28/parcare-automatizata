package ro.parcare.parcare_automatizate.service;

import ro.parcare.parcare_automatizate.model.Abonament;
import ro.parcare.parcare_automatizate.model.LocParcare;
import ro.parcare.parcare_automatizate.model.NumarInmatriculare;
import ro.parcare.parcare_automatizate.model.Parcare;
import ro.parcare.parcare_automatizate.model.Plata;
import ro.parcare.parcare_automatizate.model.SesiuneParcare;
import ro.parcare.parcare_automatizate.model.Vehicul;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Regulile de validare pe agregate din documentul Sprint 1 (A1 - A8),
// plus regulile V2 si V3, care au nevoie de toata parcarea.
public interface ValidareService {

    // V2: capacitatea este egala cu numarul de locuri din zone
    boolean capacitateCorecta(Parcare parcare);

    // V3: numerele locurilor sunt unice in parcare
    boolean numereLocuriUnice(Parcare parcare);

    // A2: un loc pentru dizabilitati se ocupa doar de un vehicul cu card valabil
    boolean poateOcupaLoc(LocParcare loc, Vehicul vehicul, LocalDate data);

    // A1: exista un loc liber in zona ocazionalilor pe care il poate ocupa vehiculul
    boolean existaLocLiberOcazional(Parcare parcare, Vehicul vehicul, LocalDate data);

    // A3: mai sunt locuri pentru un abonament nou
    boolean poateCreaAbonament(Parcare parcare, List<Abonament> abonamente);

    // A3: locul nu este deja rezervat de alt abonament activ
    boolean locDisponibilPentruAbonament(Long idLoc, List<Abonament> abonamente);

    // A4: numarul de inmatriculare are deja o sesiune activa
    boolean areSesiuneActiva(NumarInmatriculare nrAuto, List<SesiuneParcare> sesiuni);

    // A5: soferul poate iesi fara taxa (in perioada platita + 15 minute)
    boolean poateIesiFaraTaxa(SesiuneParcare sesiune, LocalDateTime oraIesire);

    // A6: doar o plata reusita prelungeste perioada platita
    boolean plataPrelungestePerioada(Plata plata);

    // A7: locul rezervat este ocupat, dar abonatul nu este in parcare
    boolean locOcupatIncorect(LocParcare locRezervat, Abonament abonament, List<SesiuneParcare> sesiuni);

    // A8: abonatul este in parcare, dar locul lui rezervat este liber
    boolean abonatParcatPeAltLoc(LocParcare locRezervat, Abonament abonament, List<SesiuneParcare> sesiuni);
}
