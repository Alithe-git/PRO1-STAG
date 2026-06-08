package uhk.slechjo1.stag.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RozvrhovaAkce {
    private final TypAkce typAkce;
    private final Semestr semestr;
    private final Den den;
    private final LocalTime casOd;
    private final LocalTime casDo;
    private final Predmet predmet;
    private final Ucebna ucebna;
    private final Ucitel ucitel;
    private final int kapacita;
    private final List<Student> studenti;

    public RozvrhovaAkce(TypAkce typAkce, Semestr semestr, Den den, LocalTime casOd, LocalTime casDo,
                         Predmet predmet, Ucebna ucebna, Ucitel ucitel) {
        if (casOd == null || casDo == null || !casOd.isBefore(casDo)) {
            throw new IllegalArgumentException("Čas začátku musí být před časem konce.");
        }
        this.typAkce = typAkce;
        this.semestr = semestr;
        this.den = den;
        this.casOd = casOd;
        this.casDo = casDo;
        this.predmet = predmet;
        this.ucebna = ucebna;
        this.ucitel = ucitel;
        this.kapacita = ucebna == null ? 0 : ucebna.getKapacita();
        this.studenti = new ArrayList<>();
    }

    public boolean kolidujeS(RozvrhovaAkce jinaAkce) {
        if (jinaAkce == null) return false;
        if (this.semestr != jinaAkce.semestr || this.den != jinaAkce.den) return false;
        return this.casOd.isBefore(jinaAkce.casDo) && jinaAkce.casOd.isBefore(this.casDo);
    }

    public boolean lzeZapsat(Student student) {
        return student != null && !studenti.contains(student) && studenti.size() < kapacita;
    }

    public boolean pridejStudenta(Student student) {
        if (!lzeZapsat(student)) return false;
        studenti.add(student);
        return true;
    }

    public int getPocetPrihlasenych() { return studenti.size(); }
    public int getVolnaKapacita() { return Math.max(0, kapacita - studenti.size()); }
    public TypAkce getTypAkce() { return typAkce; }
    public Semestr getSemestr() { return semestr; }
    public Den getDen() { return den; }
    public LocalTime getCasOd() { return casOd; }
    public LocalTime getCasDo() { return casDo; }
    public Predmet getPredmet() { return predmet; }
    public Ucebna getUcebna() { return ucebna; }
    public Ucitel getUcitel() { return ucitel; }
    public int getKapacita() { return kapacita; }
    public List<Student> getStudenti() { return Collections.unmodifiableList(studenti); }

    public String formatCas() {
        return casOd + "-" + casDo;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s %s %s %s/%d", den.getPopisek(), formatCas(), semestr,
                predmet.getZkratka(), typAkce, ucebna.getZkratka(), ucitel.getCeleJmeno(), kapacita);
    }
}
