package uhk.slechjo1.stag.io;

import uhk.slechjo1.stag.model.Predmet;
import uhk.slechjo1.stag.model.Student;
import uhk.slechjo1.stag.model.Ucebna;
import uhk.slechjo1.stag.model.Ucitel;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataLoader {
    private final CsvReader csvReader = new CsvReader();

    public Map<String, Predmet> nactiPredmety(Path path) throws IOException {
        Map<String, Predmet> predmety = new LinkedHashMap<>();
        for (Map<String, String> row : csvReader.readCsv(path)) {
            Predmet predmet = Predmet.zMapy(row);
            if (!predmet.getZkratka().isBlank()) predmety.putIfAbsent(predmet.getZkratka(), predmet);
        }
        return predmety;
    }

    public Map<String, Ucitel> nactiUcitele(Path path) throws IOException {
        Map<String, Ucitel> ucitele = new LinkedHashMap<>();
        for (Map<String, String> row : csvReader.readCsv(path)) {
            Ucitel ucitel = Ucitel.zMapy(row);
            if (!ucitel.getId().isBlank() && ucitel.jePouzitelnyProRozvrh()) {
                ucitele.putIfAbsent(ucitel.getId(), ucitel);
            }
        }
        return ucitele;
    }

    public Map<String, Ucebna> nactiUcebny(Path path) throws IOException {
        Map<String, Ucebna> ucebny = new LinkedHashMap<>();
        for (Map<String, String> row : csvReader.readCsv(path)) {
            Ucebna ucebna = Ucebna.zMapy(row);
            if (!ucebna.getZkratka().isBlank() && ucebna.getKapacita() > 0) {
                ucebny.putIfAbsent(ucebna.getZkratka(), ucebna);
            }
        }
        return ucebny;
    }

    public Map<String, Student> nactiStudenty(Path path, Map<String, Predmet> predmety) throws IOException {
        Map<String, Student> studenti = new LinkedHashMap<>();
        for (Map<String, String> row : csvReader.readCsv(path)) {
            Student student = Student.zMapy(row, predmety);
            if (!student.getUsername().isBlank()) studenti.putIfAbsent(student.getUsername(), student);
        }
        return studenti;
    }
}
