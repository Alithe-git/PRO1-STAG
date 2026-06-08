package uhk.slechjo1.stag.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AkademickyRok {
    private final int rok;
    private final LocalDate zacatek;
    private final LocalDate zacatekLetnihoSemestru;
    private final LocalDate konec;
    private final List<RozvrhovaAkce> rozvrhoveAkce;
    private final Map<String, Rozvrh> rozvrhyUcitelu;
    private final Map<String, Rozvrh> rozvrhyUceben;
    private final Map<String, Rozvrh> rozvrhyPredmetu;
    private final Map<String, Rozvrh> rozvrhyStudentu;
    private final List<String> varovani;

    public AkademickyRok(int rok) {
        this.rok = rok;
        this.zacatek = LocalDate.of(rok, 9, 1);
        this.zacatekLetnihoSemestru = LocalDate.of(rok + 1, 2, 9);
        this.konec = LocalDate.of(rok + 1, 8, 31);
        this.rozvrhoveAkce = new ArrayList<>();
        this.rozvrhyUcitelu = new HashMap<>();
        this.rozvrhyUceben = new HashMap<>();
        this.rozvrhyPredmetu = new HashMap<>();
        this.rozvrhyStudentu = new HashMap<>();
        this.varovani = new ArrayList<>();
    }

    public boolean lzeVlozitRozvrhovouAkci(RozvrhovaAkce akce) {
        Rozvrh rozvrhUcitele = rozvrhyUcitelu.getOrDefault(akce.getUcitel().getId(), new Rozvrh());
        Rozvrh rozvrhUcebny = rozvrhyUceben.getOrDefault(akce.getUcebna().getZkratka(), new Rozvrh());
        Rozvrh rozvrhPredmetu = rozvrhyPredmetu.getOrDefault(akce.getPredmet().getZkratka(), new Rozvrh());
        return rozvrhUcitele.lzeVlozit(akce)
                && rozvrhUcebny.lzeVlozit(akce)
                && rozvrhPredmetu.lzeVlozit(akce);
    }

    public boolean pridejRozvrhovouAkci(RozvrhovaAkce akce) {
        if (!lzeVlozitRozvrhovouAkci(akce)) {
            varovani.add("Akci nelze vložit kvůli kolizi: " + akce);
            return false;
        }

        Rozvrh rozvrhUcitele = rozvrhyUcitelu.computeIfAbsent(akce.getUcitel().getId(), id -> new Rozvrh());
        Rozvrh rozvrhUcebny = rozvrhyUceben.computeIfAbsent(akce.getUcebna().getZkratka(), id -> new Rozvrh());
        Rozvrh rozvrhPredmetu = rozvrhyPredmetu.computeIfAbsent(akce.getPredmet().getZkratka(), id -> new Rozvrh());

        rozvrhoveAkce.add(akce);
        rozvrhUcitele.pridejAkci(akce);
        rozvrhUcebny.pridejAkci(akce);
        rozvrhPredmetu.pridejAkci(akce);
        return true;
    }

    public boolean zapisStudentaNaAkci(Student student, RozvrhovaAkce akce) {
        Rozvrh rozvrhStudenta = rozvrhyStudentu.computeIfAbsent(student.getUsername(), id -> student.getRozvrh());
        if (!akce.lzeZapsat(student)) {
            varovani.add("Studenta nelze zapsat kvůli kapacitě nebo duplicitě: " + student.getUsername() + " -> " + akce);
            return false;
        }
        if (!rozvrhStudenta.lzeVlozit(akce)) {
            varovani.add("Kolize studenta: " + student.getUsername() + " -> " + akce);
            return false;
        }
        if (!akce.pridejStudenta(student)) return false;
        return rozvrhStudenta.pridejAkci(akce);
    }

    public List<RozvrhovaAkce> najdiAkcePredmetu(String predmetZkratka) {
        List<RozvrhovaAkce> vysledek = new ArrayList<>();
        for (RozvrhovaAkce akce : rozvrhoveAkce) {
            if (akce.getPredmet().getZkratka().equals(predmetZkratka)) vysledek.add(akce);
        }
        return vysledek;
    }

    public Rozvrh getRozvrhUcitele(String ucitelId) { return rozvrhyUcitelu.getOrDefault(ucitelId, new Rozvrh()); }
    public Rozvrh getRozvrhUcebny(String ucebnaZkratka) { return rozvrhyUceben.getOrDefault(ucebnaZkratka, new Rozvrh()); }
    public Rozvrh getRozvrhPredmetu(String predmetZkratka) { return rozvrhyPredmetu.getOrDefault(predmetZkratka, new Rozvrh()); }
    public Rozvrh getRozvrhStudenta(String username) { return rozvrhyStudentu.getOrDefault(username, new Rozvrh()); }

    public int getRok() { return rok; }
    public LocalDate getZacatek() { return zacatek; }
    public LocalDate getZacatekLetnihoSemestru() { return zacatekLetnihoSemestru; }
    public LocalDate getKonec() { return konec; }
    public List<RozvrhovaAkce> getRozvrhoveAkce() { return Collections.unmodifiableList(rozvrhoveAkce); }
    public Map<String, Rozvrh> getRozvrhyUcitelu() { return Collections.unmodifiableMap(rozvrhyUcitelu); }
    public Map<String, Rozvrh> getRozvrhyUceben() { return Collections.unmodifiableMap(rozvrhyUceben); }
    public Map<String, Rozvrh> getRozvrhyPredmetu() { return Collections.unmodifiableMap(rozvrhyPredmetu); }
    public Map<String, Rozvrh> getRozvrhyStudentu() { return Collections.unmodifiableMap(rozvrhyStudentu); }
    public List<String> getVarovani() { return Collections.unmodifiableList(varovani); }
}
