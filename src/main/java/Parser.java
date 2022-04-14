import exceptions.ElementNotFoundException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;




public class Parser {
    public final static String TABLE_CSS_QUERY = "div#result";
    public final static String SELECTORS_ELEMENT = "table.selectors";
    public final static String TIMETABLE_ELEMENT = "table.timetable";

    public static Element findContent(String html) throws ElementNotFoundException {
        Document doc = Jsoup.parse(html);
        Element element = doc.selectFirst(TABLE_CSS_QUERY);
        if (element == null) {
            throw new ElementNotFoundException("Element not found with " + TABLE_CSS_QUERY);
        }
        return element;
    }

    public static ReturnResult getTables(Element tableContainer) {
        var selectorElement = tableContainer.selectFirst("table.selectors");
        if (selectorElement == null) {
            throw new ElementNotFoundException("Element not found with " + SELECTORS_ELEMENT);
        }
        var timetableElement = tableContainer.selectFirst("table.timetable");
        if (timetableElement == null) {
            throw new ElementNotFoundException("Element not found with " + TIMETABLE_ELEMENT);
        }
        return new ReturnResult(selectorElement, timetableElement);
    }

    public static SelectorTableResult parseSelectorTable(Element selectorElement) {
        var thIter = selectorElement.select("th").iterator();
        var tdIter = selectorElement.select("td").iterator();
        String dY = null;
        String dT = null;
        String dN = null;
        while (thIter.hasNext() && tdIter.hasNext()) {
            String thKey = thIter.next().text();
            String tdValue = tdIter.next().text();
            switch (thKey) {
                case "Учебный год:":
                    dY = tdValue;
                    break;
                case "Семестр:":
                    dT = tdValue;
                    break;
                case "Кафедра:":
                    dN = tdValue;
                    break;
                case "Форма обучения:":
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        if (thIter.hasNext() || tdIter.hasNext()){
            throw new RuntimeException("Different number of elements");
        }
        return new SelectorTableResult(dY, dT, dN);
    }

    public static TimeTableResult parseTimetableSelector(Element timetableElement) {

//        return new TimeTableResult("","", "", "", "", "","", "");
        return null;
    }
}




