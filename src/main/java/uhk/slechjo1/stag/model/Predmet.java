package uhk.slechjo1.stag.model;

import java.util.Map;

public class Predmet {
    private final String zkratka;
    private final String katedra;
    private final String nazev;
    private final int kreditu;
    private final int rok;
    private final String statut;
    private final int doporucenyRocnik;
    private final Semestr doporucenySemestr;
    private final String vyznamPredmetu;
    private final boolean vyukaLS;
    private final boolean vyukaZS;
    private final String rozsah;
    private final int hodinPrednasek;
    private final int hodinCviceni;
    private final int hodinSeminaru;
    private final String typZk;

    public Predmet(String zkratka, String katedra, String nazev, int kreditu, int rok, String statut,
                   int doporucenyRocnik, Semestr doporucenySemestr, String vyznamPredmetu,
                   boolean vyukaLS, boolean vyukaZS, String rozsah, String typZk) {
        this.zkratka = safe(zkratka);
        this.katedra = safe(katedra);
        this.nazev = safe(nazev);
        this.kreditu = kreditu;
        this.rok = rok;
        this.statut = safe(statut);
        this.doporucenyRocnik = doporucenyRocnik;
        this.doporucenySemestr = doporucenySemestr;
        this.vyznamPredmetu = safe(vyznamPredmetu);
        this.vyukaLS = vyukaLS;
        this.vyukaZS = vyukaZS;
        this.rozsah = safe(rozsah);
        this.typZk = safe(typZk);
        int[] hodiny = parseRozsah(this.rozsah);
        this.hodinPrednasek = hodiny[0];
        this.hodinCviceni = hodiny[1];
        this.hodinSeminaru = hodiny[2];
    }

    public static Predmet zMapy(Map<String, String> mapa) {
        return new Predmet(
                mapa.get("zkratka"),
                mapa.get("katedra"),
                mapa.get("nazev"),
                parseInt(mapa.get("kreditu"), 0),
                parseInt(mapa.get("rok"), 0),
                mapa.get("statut"),
                parseInt(mapa.get("doporucenyRocnik"), 0),
                parseSemestr(mapa.get("doporucenySemestr")),
                mapa.get("vyznamPredmetu"),
                jeAno(mapa.get("vyukaLS")),
                jeAno(mapa.get("vyukaZS")),
                mapa.get("rozsah"),
                mapa.get("typZk")
        );
    }

    private static int[] parseRozsah(String rozsah) {
        int[] vysledek = {0, 0, 0};
        if (rozsah == null || rozsah.isBlank()) return vysledek;
        String[] casti = rozsah.split("\\+");
        for (int i = 0; i < Math.min(3, casti.length); i++) {
            vysledek[i] = parseInt(casti[i], 0);
        }
        return vysledek;
    }

    private static Semestr parseSemestr(String hodnota) {
        if (hodnota == null || hodnota.isBlank()) return Semestr.ZS;
        return Semestr.valueOf(hodnota.trim().toUpperCase());
    }

    private static boolean jeAno(String hodnota) {
        return "A".equalsIgnoreCase(safe(hodnota));
    }

    private static int parseInt(String hodnota, int vychozi) {
        if (hodnota == null || hodnota.isBlank()) return vychozi;
        try { return Integer.parseInt(hodnota.trim()); }
        catch (NumberFormatException e) { return vychozi; }
    }

    private static String safe(String hodnota) { return hodnota == null ? "" : hodnota.trim(); }

    public boolean vyucujeSeVSemestru(Semestr semestr) {
        return semestr == Semestr.LS ? vyukaLS : vyukaZS;
    }

    public boolean maPrednasku() { return hodinPrednasek > 0; }
    public boolean maCviceni() { return hodinCviceni > 0; }
    public boolean maSeminar() { return hodinSeminaru > 0; }

    public String getZkratka() { return zkratka; }
    public String getKatedra() { return katedra; }
    public String getNazev() { return nazev; }
    public int getKreditu() { return kreditu; }
    public int getRok() { return rok; }
    public String getStatut() { return statut; }
    public int getDoporucenyRocnik() { return doporucenyRocnik; }
    public Semestr getDoporucenySemestr() { return doporucenySemestr; }
    public String getVyznamPredmetu() { return vyznamPredmetu; }
    public String getRozsah() { return rozsah; }
    public int getHodinPrednasek() { return hodinPrednasek; }
    public int getHodinCviceni() { return hodinCviceni; }
    public int getHodinSeminaru() { return hodinSeminaru; }
    public String getTypZk() { return typZk; }

    @Override
    public String toString() {
        return zkratka + " - " + nazev + " (" + rozsah + ", " + kreditu + " kr.)";
    }
}
