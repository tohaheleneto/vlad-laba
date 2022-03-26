import exceptions.ElementNotFoundException;
import org.assertj.core.api.Assertions;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParserTest {

    @Test
    public void findContentTest() throws ElementNotFoundException, IOException {
        String html = Files.readString(Path.of("src/test/resources/find_content.html"));
        var elementResult = Parser.findContent(html);
        assertThat(elementResult.text()).isEqualTo("the content you are looking for");
    }

    @Test
    public void findContentShouldThrowErrorTest() throws ElementNotFoundException { //проверить то что выбрасывается эксепшн
        String html = "<html></html>";
        assertThatThrownBy(() -> Parser.findContent(html))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessage("Element not found with " + Parser.TABLE_CSS_QUERY);
    }
    @Test
    public void selectorTableResultTest() throws IOException {
        var t = Files.readString(Path.of("src/test/resources/selectorTableResult.html"));
        Element e = new Element(t);
        var res = Parser.parseSelectorTable(e);
        assertThat(res.dataTerm).isEqualTo("Осенний");
        assertThat(res.dataYear).isEqualTo("2021-2022");
        assertThat(res.departmentName).isEqualTo("Прикладной математики");

    }
}