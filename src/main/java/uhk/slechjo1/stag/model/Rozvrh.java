package uhk.slechjo1.stag.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Rozvrh {
    private final Map<Den, List<RozvrhovaAkce>> akcePodleDne;

    public Rozvrh() {
        this.akcePodleDne = new EnumMap<>(Den.class);
        for (Den den : Den.values()) {
            akcePodleDne.put(den, new ArrayList<>());
        }
    }

    public boolean lzeVlozit(RozvrhovaAkce akce) {
        return akcePodleDne.get(akce.getDen()).stream().noneMatch(existujici -> existujici.kolidujeS(akce));
    }

    public boolean pridejAkci(RozvrhovaAkce akce) {
        if (!lzeVlozit(akce)) return false;
        List<RozvrhovaAkce> denniAkce = akcePodleDne.get(akce.getDen());
        denniAkce.add(akce);
        denniAkce.sort(Comparator.comparing(RozvrhovaAkce::getCasOd));
        return true;
    }

    public List<RozvrhovaAkce> getAkceVDen(Den den) {
        return List.copyOf(akcePodleDne.getOrDefault(den, List.of()));
    }

    public List<RozvrhovaAkce> getVsechnyAkce() {
        List<RozvrhovaAkce> vsechny = new ArrayList<>();
        for (Den den : Den.values()) {
            vsechny.addAll(akcePodleDne.get(den));
        }
        return List.copyOf(vsechny);
    }

    public String formatovanyVypis() {
        StringBuilder sb = new StringBuilder();
        for (Den den : Den.values()) {
            List<RozvrhovaAkce> akce = akcePodleDne.get(den);
            if (akce.isEmpty()) continue;
            sb.append(den.getPopisek()).append(":\n");
            for (RozvrhovaAkce a : akce) {
                sb.append("  ")
                  .append(String.format("%-11s", a.formatCas()))
                  .append(" ").append(String.format("%-7s", a.getSemestr()))
                  .append(" ").append(String.format("%-8s", a.getPredmet().getZkratka()))
                  .append(" ").append(String.format("%-9s", a.getTypAkce()))
                  .append(" ").append(String.format("%-6s", a.getUcebna().getZkratka()))
                  .append(" ").append(a.getUcitel().getCeleJmeno())
                  .append(" [").append(a.getPocetPrihlasenych()).append("/").append(a.getKapacita()).append("]")
                  .append("\n");
            }
        }
        if (sb.isEmpty()) sb.append("  Bez rozvrhových akcí.\n");
        return sb.toString();
    }

    public void vypisRozvrh() {
        System.out.print(formatovanyVypis());
    }
}
