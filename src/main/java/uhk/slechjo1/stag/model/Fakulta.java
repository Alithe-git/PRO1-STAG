package uhk.slechjo1.stag.model;

import uhk.slechjo1.stag.io.DataLoader;
import uhk.slechjo1.stag.service.RozvrhService;
import uhk.slechjo1.stag.service.StudentEnrollmentService;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Fakulta {
    private final String nazev;
    private final String zkratka;
    private final Path predmetyPath;
    private final Path ucitelePath;
    private final Path ucebnyPath;
    private final Path studentiPath;

    private Map<String, Student> studenti = new LinkedHashMap<>();
    private Map<String, Ucitel> ucitele = new LinkedHashMap<>();
    private Map<String, Predmet> predmety = new LinkedHashMap<>();
    private Map<String, Ucebna> ucebny = new LinkedHashMap<>();
    private AkademickyRok akademickyRok;

    public Fakulta(String nazev, String zkratka, Path predmetyPath, Path ucitelePath, Path ucebnyPath, Path studentiPath) {
        this.nazev = nazev;
        this.zkratka = zkratka;
        this.predmetyPath = predmetyPath;
        this.ucitelePath = ucitelePath;
        this.ucebnyPath = ucebnyPath;
        this.studentiPath = studentiPath;
        this.akademickyRok = new AkademickyRok(urciAkademickyRok(LocalDate.now()));
    }

    public void nactiData() throws IOException {
        DataLoader loader = new DataLoader();
        predmety = loader.nactiPredmety(predmetyPath);
        ucitele = loader.nactiUcitele(ucitelePath);
        ucebny = loader.nactiUcebny(ucebnyPath);
        studenti = loader.nactiStudenty(studentiPath, predmety);
    }

    public void vytvorRozvrh() {
        RozvrhService rozvrhService = new RozvrhService();
        rozvrhService.vytvorRozvrh(akademickyRok, predmety, ucitele, ucebny, studenti);
        StudentEnrollmentService enrollmentService = new StudentEnrollmentService();
        enrollmentService.zapisStudenty(akademickyRok, studenti);
    }

    private int urciAkademickyRok(LocalDate datum) {
        return datum.getMonthValue() >= 9 ? datum.getYear() : datum.getYear() - 1;
    }

    public void vypisSouhrn() {
        System.out.println("============================================================");
        System.out.println(nazev + " (" + zkratka + ")");
        System.out.println("Akademický rok: " + akademickyRok.getRok() + "/" + (akademickyRok.getRok() + 1));
        System.out.println("============================================================");
        System.out.println("Načteno:");
        System.out.println("- předměty: " + predmety.size());
        System.out.println("- učitelé použitelní pro rozvrh: " + ucitele.size());
        System.out.println("- učebny: " + ucebny.size());
        System.out.println("- studenti: " + studenti.size());
        System.out.println();
        System.out.println("Vytvořené rozvrhové akce: " + akademickyRok.getRozvrhoveAkce().size());
        System.out.println("Rozvrhy učitelů: " + akademickyRok.getRozvrhyUcitelu().size());
        System.out.println("Rozvrhy učeben: " + akademickyRok.getRozvrhyUceben().size());
        System.out.println("Rozvrhy předmětů: " + akademickyRok.getRozvrhyPredmetu().size());
        System.out.println("Rozvrhy studentů: " + akademickyRok.getRozvrhyStudentu().size());
        if (!akademickyRok.getVarovani().isEmpty()) {
            System.out.println();
            System.out.println("Varování / nevyřešené situace: " + akademickyRok.getVarovani().size());
            akademickyRok.getVarovani().stream().limit(15).forEach(v -> System.out.println("- " + v));
            if (akademickyRok.getVarovani().size() > 15) {
                System.out.println("- ... další varování zkrácena");
            }
        }
    }

    public void vypisRozvrhyStudentu() {
        System.out.println();
        System.out.println("============================================================");
        System.out.println("ROZVRHY STUDENTŮ");
        System.out.println("============================================================");
        studenti.values().stream()
                .sorted(Comparator.comparing(Student::getUsername))
                .forEach(student -> {
                    System.out.println();
                    System.out.println(student);
                    akademickyRok.getRozvrhStudenta(student.getUsername()).vypisRozvrh();
                });
    }

    public void vypisRozvrhyUcitelu() {
        System.out.println();
        System.out.println("============================================================");
        System.out.println("UKÁZKA ROZVRHŮ UČITELŮ");
        System.out.println("============================================================");
        ucitele.values().stream()
                .filter(u -> !akademickyRok.getRozvrhUcitele(u.getId()).getVsechnyAkce().isEmpty())
                .sorted(Comparator.comparing(Ucitel::getPrijmeni).thenComparing(Ucitel::getJmeno))
                .limit(5)
                .forEach(ucitel -> {
                    System.out.println();
                    System.out.println(ucitel);
                    akademickyRok.getRozvrhUcitele(ucitel.getId()).vypisRozvrh();
                });
    }

    public void vypisRozvrhyUceben() {
        System.out.println();
        System.out.println("============================================================");
        System.out.println("UKÁZKA ROZVRHŮ UČEBEN");
        System.out.println("============================================================");
        ucebny.values().stream()
                .filter(u -> !akademickyRok.getRozvrhUcebny(u.getZkratka()).getVsechnyAkce().isEmpty())
                .sorted(Comparator.comparing(Ucebna::getZkratka))
                .limit(5)
                .forEach(ucebna -> {
                    System.out.println();
                    System.out.println(ucebna);
                    akademickyRok.getRozvrhUcebny(ucebna.getZkratka()).vypisRozvrh();
                });
    }

    public Map<String, Student> getStudenti() { return Map.copyOf(studenti); }
    public Map<String, Ucitel> getUcitele() { return Map.copyOf(ucitele); }
    public Map<String, Predmet> getPredmety() { return Map.copyOf(predmety); }
    public Map<String, Ucebna> getUcebny() { return Map.copyOf(ucebny); }
    public AkademickyRok getAkademickyRok() { return akademickyRok; }
}
