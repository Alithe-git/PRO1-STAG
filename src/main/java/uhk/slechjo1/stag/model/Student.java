package uhk.slechjo1.stag.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Student extends Osoba {
    private final String username;
    private final Obor obor;
    private final FormaStudia formaStudia;
    private final int rocnik;
    private final List<Predmet> zapsanePredmety;
    private final Rozvrh rozvrh;

    public Student(String username, String jmeno, String prijmeni, String email, Obor obor,
                   FormaStudia formaStudia, int rocnik, List<Predmet> zapsanePredmety) {
        super(jmeno, prijmeni, email);
        this.username = safe(username);
        this.obor = obor;
        this.formaStudia = formaStudia;
        this.rocnik = rocnik;
        this.zapsanePredmety = new ArrayList<>(zapsanePredmety == null ? List.of() : zapsanePredmety);
        this.rozvrh = new Rozvrh();
    }

    public static Student zMapy(Map<String, String> mapa, Map<String, Predmet> predmety) {
        List<Predmet> zapsane = new ArrayList<>();
        String zkratky = mapa.get("zapsanePredmety");
        if (zkratky != null && !zkratky.isBlank()) {
            for (String zkratka : zkratky.split(",")) {
                Predmet predmet = predmety.get(zkratka.trim());
                if (predmet != null) zapsane.add(predmet);
            }
        }
        return new Student(
                mapa.get("username"), mapa.get("jmeno"), mapa.get("prijmeni"), mapa.get("email"),
                parseEnum(Obor.class, mapa.get("obor"), Obor.AI3),
                parseEnum(FormaStudia.class, mapa.get("formaStudia"), FormaStudia.PREZ),
                parseInt(mapa.get("rocnik"), 1), zapsane
        );
    }

    private static <E extends Enum<E>> E parseEnum(Class<E> typ, String hodnota, E vychozi) {
        if (hodnota == null || hodnota.isBlank()) return vychozi;
        try { return Enum.valueOf(typ, hodnota.trim().toUpperCase()); }
        catch (IllegalArgumentException e) { return vychozi; }
    }

    private static int parseInt(String hodnota, int vychozi) {
        if (hodnota == null || hodnota.isBlank()) return vychozi;
        try { return Integer.parseInt(hodnota.trim()); }
        catch (NumberFormatException e) { return vychozi; }
    }

    private static String safe(String hodnota) { return hodnota == null ? "" : hodnota.trim(); }

    public boolean maZapsanyPredmet(Predmet predmet) {
        return zapsanePredmety.stream().anyMatch(p -> p.getZkratka().equals(predmet.getZkratka()));
    }

    public String getUsername() { return username; }
    public Obor getObor() { return obor; }
    public FormaStudia getFormaStudia() { return formaStudia; }
    public int getRocnik() { return rocnik; }
    public List<Predmet> getZapsanePredmety() { return Collections.unmodifiableList(zapsanePredmety); }
    public Rozvrh getRozvrh() { return rozvrh; }

    @Override
    public String toString() {
        return getCeleJmeno() + " (" + username + ", " + obor + ", " + formaStudia + ", " + rocnik + ". ročník)";
    }
}
