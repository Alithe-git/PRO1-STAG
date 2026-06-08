package uhk.slechjo1.stag.app;

import uhk.slechjo1.stag.model.Fakulta;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            Path dataDir = najdiDataDir();
            Fakulta fakulta = new Fakulta(
                    "Fakulta informatiky a managementu",
                    "FIM",
                    dataDir.resolve("predmety.csv"),
                    dataDir.resolve("ucitele.csv"),
                    dataDir.resolve("mistnosti.csv"),
                    dataDir.resolve("studenti.csv")
            );

            fakulta.nactiData();
            fakulta.vytvorRozvrh();
            fakulta.vypisSouhrn();
            fakulta.vypisRozvrhyStudentu();
            fakulta.vypisRozvrhyUcitelu();
            fakulta.vypisRozvrhyUceben();
        } catch (Exception e) {
            System.err.println("Aplikaci se nepodařilo spustit: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Path najdiDataDir() {
        Path mavenResources = Path.of("src", "main", "resources", "data");
        if (Files.isDirectory(mavenResources)) return mavenResources;

        Path localData = Path.of("data");
        if (Files.isDirectory(localData)) return localData;

        throw new IllegalStateException("Adresář s CSV daty nebyl nalezen. Očekávám src/main/resources/data nebo data.");
    }
}
