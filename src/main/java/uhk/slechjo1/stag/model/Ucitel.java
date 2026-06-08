package uhk.slechjo1.stag.model;

import java.util.Map;

public class Ucitel extends Osoba {
    private final String id;
    private final String titulPred;
    private final String titulZa;
    private final boolean platny;
    private final boolean zamestnanec;
    private final String katedra;
    private final String telefon;
    private final String zkrBudovy;
    private final String cisloMistnosti;

    public Ucitel(String id, String jmeno, String prijmeni, String titulPred, String titulZa, boolean platny,
                  boolean zamestnanec, String katedra, String email, String telefon, String zkrBudovy,
                  String cisloMistnosti) {
        super(jmeno, prijmeni, email);
        this.id = safe(id);
        this.titulPred = safe(titulPred);
        this.titulZa = safe(titulZa);
        this.platny = platny;
        this.zamestnanec = zamestnanec;
        this.katedra = safe(katedra);
        this.telefon = safe(telefon);
        this.zkrBudovy = safe(zkrBudovy);
        this.cisloMistnosti = safe(cisloMistnosti);
    }

    public static Ucitel zMapy(Map<String, String> mapa) {
        return new Ucitel(
                mapa.get("ucitIdno"), mapa.get("jmeno"), mapa.get("prijmeni"), mapa.get("titulPred"),
                mapa.get("titulZa"), jeAno(mapa.get("platnost")), jeAno(mapa.get("zamestnanec")),
                mapa.get("katedra"), mapa.get("email"), mapa.get("telefon"), mapa.get("zkrBudovy"),
                mapa.get("cisloMistnosti")
        );
    }

    private static boolean jeAno(String hodnota) { return "A".equalsIgnoreCase(safe(hodnota)); }
    private static String safe(String hodnota) { return hodnota == null ? "" : hodnota.trim(); }

    public boolean jePouzitelnyProRozvrh() { return platny && zamestnanec; }
    public String getId() { return id; }
    public String getTitulPred() { return titulPred; }
    public String getTitulZa() { return titulZa; }
    public boolean isPlatny() { return platny; }
    public boolean isZamestnanec() { return zamestnanec; }
    public String getKatedra() { return katedra; }
    public String getTelefon() { return telefon; }
    public String getZkrBudovy() { return zkrBudovy; }
    public String getCisloMistnosti() { return cisloMistnosti; }

    @Override
    public String toString() {
        String tituly = (titulPred + " " + getCeleJmeno() + " " + titulZa).trim().replaceAll("\\s+", " ");
        return tituly + " [" + id + ", " + katedra + "]";
    }
}
