import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class App {
    public static void main(String[] args) throws IOException {
        String html = Files.readString(Path.of("src/main/resources/full_example.html"));
        var t = Parser.findContent(html);
        var m = Parser.getTables(t);
        Parser.parseSelectorTable(m.selectorsElement);
    }
}
