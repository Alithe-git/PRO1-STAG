package uhk.slechjo1.stag.model;

import java.util.Map;

public class Ucebna {
    private final String budova;
    private final String cisloMistnosti;
    private final String zkratka;
    private final String katedra;
    private final String pracoviste;
    private final String typ;
    private final int kapacita;
    private final int podlazi;
    private final String obec;
    private final String adresaBudovy;

    public Ucebna(String budova, String cisloMistnosti, String katedra, String pracoviste, String typ,
                  int kapacita, int podlazi, String obec, String adresaBudovy) {
        this.budova = safe(budova);
        this.cisloMistnosti = safe(cisloMistnosti);
        this.zkratka = vytvorZkratku(this.budova, this.cisloMistnosti);
        this.katedra = safe(katedra);
        this.pracoviste = safe(pracoviste);
        this.typ = safe(typ);
        this.kapacita = kapacita;
        this.podlazi = podlazi;
        this.obec = safe(obec);
        this.adresaBudovy = safe(adresaBudovy);
    }

    public static Ucebna zMapy(Map<String, String> mapa) {
        return new Ucebna(
                mapa.get("zkrBudovy"), mapa.get("cisloMistnosti"), mapa.get("katedra"), mapa.get("pracoviste"),
                mapa.get("typ"), parseInt(mapa.get("kapacita"), 0), parseInt(mapa.get("podlazi"), 0),
                mapa.get("obec"), mapa.get("adresaBudovy")
        );
    }

    private static String vytvorZkratku(String budova, String cisloMistnosti) {
        if (cisloMistnosti == null || cisloMistnosti.isBlank()) return safe(budova);
        String cislo = cisloMistnosti.trim();
        String bud = safe(budova);
        if (!bud.isEmpty() && cislo.toUpperCase().startsWith(bud.toUpperCase())) return cislo;
        return bud + cislo;
    }

    private static int parseInt(String hodnota, int vychozi) {
        if (hodnota == null || hodnota.isBlank()) return vychozi;
        try { return Integer.parseInt(hodnota.trim()); }
        catch (NumberFormatException e) { return vychozi; }
    }

    private static String safe(String hodnota) { return hodnota == null ? "" : hodnota.trim(); }

    public String getBudova() { return budova; }
    public String getCisloMistnosti() { return cisloMistnosti; }
    public String getZkratka() { return zkratka; }
    public String getKatedra() { return katedra; }
    public String getPracoviste() { return pracoviste; }
    public String getTyp() { return typ; }
    public int getKapacita() { return kapacita; }
    public int getPodlazi() { return podlazi; }
    public String getObec() { return obec; }
    public String getAdresaBudovy() { return adresaBudovy; }

    @Override
    public String toString() {
        return zkratka + " (kapacita " + kapacita + ")";
    }
}
