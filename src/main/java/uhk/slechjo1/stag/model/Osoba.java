package uhk.slechjo1.stag.model;

public abstract class Osoba {
    private final String jmeno;
    private final String prijmeni;
    private final String email;

    protected Osoba(String jmeno, String prijmeni, String email) {
        this.jmeno = hodnota(jmeno);
        this.prijmeni = hodnota(prijmeni);
        this.email = hodnota(email);
    }

    private static String hodnota(String text) {
        return text == null ? "" : text.trim();
    }

    public String getJmeno() { return jmeno; }
    public String getPrijmeni() { return prijmeni; }
    public String getEmail() { return email; }

    public String getCeleJmeno() {
        return (jmeno + " " + prijmeni).trim();
    }

    @Override
    public String toString() {
        return getCeleJmeno();
    }
}
