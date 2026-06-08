package uhk.slechjo1.stag.service;

import uhk.slechjo1.stag.model.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class StudentEnrollmentService {
    public void zapisStudenty(AkademickyRok akademickyRok, Map<String, Student> studenti) {
        for (Student student : studenti.values()) {
            for (Predmet predmet : student.getZapsanePredmety()) {
                List<RozvrhovaAkce> akcePredmetu = akademickyRok.najdiAkcePredmetu(predmet.getZkratka());
                zapisNaPrednasku(akademickyRok, student, akcePredmetu);
                zapisNaJednoCviceniNeboSeminar(akademickyRok, student, akcePredmetu);
            }
        }
    }

    private void zapisNaPrednasku(AkademickyRok akademickyRok, Student student, List<RozvrhovaAkce> akcePredmetu) {
        akcePredmetu.stream()
                .filter(a -> a.getTypAkce() == TypAkce.PREDNASKA)
                .min(Comparator.comparing(RozvrhovaAkce::getCasOd))
                .ifPresent(a -> akademickyRok.zapisStudentaNaAkci(student, a));
    }

    private void zapisNaJednoCviceniNeboSeminar(AkademickyRok akademickyRok, Student student, List<RozvrhovaAkce> akcePredmetu) {
        akcePredmetu.stream()
                .filter(a -> a.getTypAkce() == TypAkce.CVICENI || a.getTypAkce() == TypAkce.SEMINAR)
                .sorted(Comparator.comparing(RozvrhovaAkce::getPocetPrihlasenych)
                        .thenComparing(RozvrhovaAkce::getCasOd))
                .filter(a -> akademickyRok.zapisStudentaNaAkci(student, a))
                .findFirst();
    }
}
