import exceptions.ElementNotFoundException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Parser {
    public final static String TABLE_CSS_QUERY = "div#result";

    public static Element findContent(String html) throws ElementNotFoundException {
        Document doc = Jsoup.parse(html);
        Element element = doc.selectFirst(TABLE_CSS_QUERY);
        if (element == null) {
            throw new ElementNotFoundException("Element not found with " + TABLE_CSS_QUERY);
        }
        return element;
    }

}


