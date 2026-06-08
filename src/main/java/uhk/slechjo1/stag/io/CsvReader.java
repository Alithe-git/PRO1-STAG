package uhk.slechjo1.stag.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CsvReader {
    private final char separator;

    public CsvReader() {
        this(';');
    }

    public CsvReader(char separator) {
        this.separator = separator;
    }

    public List<Map<String, String>> readCsv(Path path) throws IOException {
        Charset charset = zjistiCharset(path);
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            String headerLine = reader.readLine();
            if (headerLine == null) return List.of();
            List<String> headers = parseLine(headerLine);
            List<Map<String, String>> rows = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                List<String> values = parseLine(line);
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    row.put(headers.get(i), i < values.size() ? values.get(i) : "");
                }
                rows.add(row);
            }
            return rows;
        }
    }

    private Charset zjistiCharset(Path path) throws IOException {
        byte[] data = Files.readAllBytes(path);
        try {
            StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(java.nio.ByteBuffer.wrap(data));
            return StandardCharsets.UTF_8;
        } catch (CharacterCodingException e) {
            return Charset.forName("windows-1250");
        }
    }

    private List<String> parseLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == separator && !inQuotes) {
                result.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim());
        return result;
    }
}
