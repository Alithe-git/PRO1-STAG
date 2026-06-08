package uhk.slechjo1.stag.service;

import uhk.slechjo1.stag.model.*;
import uhk.slechjo1.stag.util.TimeSlot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RozvrhService {
    private final List<TimeSlot> sloty = TimeSlot.vychoziSloty();
    private final java.util.Map<String, Integer> obsazenostSlotu = new java.util.HashMap<>();

    public void vytvorRozvrh(AkademickyRok akademickyRok,
                             Map<String, Predmet> predmety,
                             Map<String, Ucitel> ucitele,
                             Map<String, Ucebna> ucebny,
                             Map<String, Student> studenti) {
        List<Predmet> predmetyKRozvrzeni = new ArrayList<>(predmety.values());
        predmetyKRozvrzeni.sort(Comparator
                .comparing(Predmet::getDoporucenyRocnik)
                .thenComparing(Predmet::getDoporucenySemestr)
                .thenComparing(Predmet::getZkratka));

        for (Predmet predmet : predmetyKRozvrzeni) {
            vytvorAkcePredmetu(akademickyRok, predmet, ucitele, ucebny, studenti);
        }
    }

    private void vytvorAkcePredmetu(AkademickyRok akademickyRok,
                                    Predmet predmet,
                                    Map<String, Ucitel> ucitele,
                                    Map<String, Ucebna> ucebny,
                                    Map<String, Student> studenti) {
        int pocetStudentu = spocitejStudentyPredmetu(predmet, studenti);
        if (predmet.maPrednasku()) {
            vytvorJednuAkci(akademickyRok, predmet, TypAkce.PREDNASKA, Math.max(30, pocetStudentu), ucitele, ucebny, studenti);
        }
        if (predmet.maCviceni()) {
            int pocetCviceni = Math.max(1, (int) Math.ceil(Math.max(1, pocetStudentu) / 24.0));
            for (int i = 0; i < pocetCviceni; i++) {
                vytvorJednuAkci(akademickyRok, predmet, TypAkce.CVICENI, Math.max(15, Math.min(24, pocetStudentu)), ucitele, ucebny, studenti);
            }
        }
        if (predmet.maSeminar()) {
            vytvorJednuAkci(akademickyRok, predmet, TypAkce.SEMINAR, Math.max(10, pocetStudentu), ucitele, ucebny, studenti);
        }
    }

    private int spocitejStudentyPredmetu(Predmet predmet, Map<String, Student> studenti) {
        int pocet = 0;
        for (Student student : studenti.values()) {
            if (student.maZapsanyPredmet(predmet)) pocet++;
        }
        return pocet;
    }

    private boolean vytvorJednuAkci(AkademickyRok akademickyRok,
                                    Predmet predmet,
                                    TypAkce typAkce,
                                    int minimalniKapacita,
                                    Map<String, Ucitel> ucitele,
                                    Map<String, Ucebna> ucebny,
                                    Map<String, Student> studenti) {
        List<Ucitel> kandidatiUcitelu = seradUcitele(predmet, ucitele);
        List<Ucebna> kandidatiUceben = seradUcebny(typAkce, minimalniKapacita, ucebny);

        for (Semestr semestr : Semestr.values()) {
            if (!predmet.vyucujeSeVSemestru(semestr)) continue;
            for (KandidatCasu kandidatCasu : serazeneKandidatyCasu(semestr)) {
                for (Ucitel ucitel : kandidatiUcitelu) {
                    for (Ucebna ucebna : kandidatiUceben) {
                        RozvrhovaAkce akce = new RozvrhovaAkce(
                                typAkce, semestr, kandidatCasu.den(), kandidatCasu.slot().od(),
                                kandidatCasu.slot().doCas(), predmet, ucebna, ucitel
                        );
                        if (akademickyRok.lzeVlozitRozvrhovouAkci(akce)
                                && nekolidujeStudentum(akademickyRok, akce, studenti)) {
                            boolean vlozeno = akademickyRok.pridejRozvrhovouAkci(akce);
                            if (vlozeno) zvysObsazenost(semestr, kandidatCasu.den(), kandidatCasu.indexSlotu());
                            return vlozeno;
                        }
                    }
                }
            }
        }
        return false;
    }

    private List<KandidatCasu> serazeneKandidatyCasu(Semestr semestr) {
        List<KandidatCasu> kandidati = new ArrayList<>();
        for (Den den : Den.values()) {
            for (int i = 0; i < sloty.size(); i++) {
                kandidati.add(new KandidatCasu(den, sloty.get(i), i));
            }
        }
        kandidati.sort(Comparator
                .comparingInt((KandidatCasu k) -> obsazenostSlotu.getOrDefault(klic(semestr, k.den(), k.indexSlotu()), 0))
                .thenComparingInt(k -> k.den().ordinal())
                .thenComparingInt(KandidatCasu::indexSlotu));
        return kandidati;
    }

    private void zvysObsazenost(Semestr semestr, Den den, int indexSlotu) {
        String klic = klic(semestr, den, indexSlotu);
        obsazenostSlotu.put(klic, obsazenostSlotu.getOrDefault(klic, 0) + 1);
    }

    private String klic(Semestr semestr, Den den, int indexSlotu) {
        return semestr + ":" + den + ":" + indexSlotu;
    }

    private record KandidatCasu(Den den, TimeSlot slot, int indexSlotu) {}

    private boolean nekolidujeStudentum(AkademickyRok akademickyRok, RozvrhovaAkce kandidat, Map<String, Student> studenti) {
        for (Student student : studenti.values()) {
            if (!student.maZapsanyPredmet(kandidat.getPredmet())) continue;
            for (Predmet zapsanyPredmet : student.getZapsanePredmety()) {
                for (RozvrhovaAkce existujici : akademickyRok.najdiAkcePredmetu(zapsanyPredmet.getZkratka())) {
                    if (kandidat.kolidujeS(existujici)) return false;
                }
            }
        }
        return true;
    }

    private List<Ucitel> seradUcitele(Predmet predmet, Map<String, Ucitel> ucitele) {
        return ucitele.values().stream()
                .sorted(Comparator
                        .comparing((Ucitel u) -> !u.getKatedra().equalsIgnoreCase(predmet.getKatedra()))
                        .thenComparing(Ucitel::getPrijmeni)
                        .thenComparing(Ucitel::getJmeno))
                .collect(Collectors.toList());
    }

    private List<Ucebna> seradUcebny(TypAkce typAkce, int minimalniKapacita, Map<String, Ucebna> ucebny) {
        Comparator<Ucebna> comparator;
        if (typAkce == TypAkce.PREDNASKA) {
            comparator = Comparator.comparingInt(Ucebna::getKapacita).reversed();
        } else {
            comparator = Comparator.comparingInt(Ucebna::getKapacita);
        }
        return ucebny.values().stream()
                .filter(u -> u.getKapacita() >= minimalniKapacita)
                .sorted(comparator.thenComparing(Ucebna::getZkratka))
                .collect(Collectors.toList());
    }
}
