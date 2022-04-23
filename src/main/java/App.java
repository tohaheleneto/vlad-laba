import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Input the path to html: ");
        String html = Files.readString(Path.of(sc.nextLine()));
        var findContent = Parser.findContent(html);
        var getTablesResult = Parser.getTables(findContent);
        var getSelectorTableResult = Parser.parseSelectorTable(getTablesResult.selectorsElement);
        var getTimetableSelectorResult = Parser.parseTimetableSelector(getTablesResult.timetableElement);
        System.out.println("Input the path to save .xlsx: ");
        ExcelCreator.saveData(getTimetableSelectorResult, getSelectorTableResult, LocalDate.now());
    }
}
