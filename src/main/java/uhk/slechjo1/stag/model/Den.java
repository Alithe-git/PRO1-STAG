package uhk.slechjo1.stag.model;

public enum Den {
    PO("Po"), UT("Ut"), ST("St"), CT("Ct"), PA("Pa");

    private final String popisek;

    Den(String popisek) {
        this.popisek = popisek;
    }

    public String getPopisek() {
        return popisek;
    }
}
