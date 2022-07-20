import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ExcelCreator {
    static final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static void saveData(List<TimeTableResult> timeTableData, SelectorTableResult selectorData, LocalDate dayOfTheWeek) throws IOException {
        List<TimeTableResult> timeTableNumerator = new ArrayList<>();
        List<TimeTableResult> timeTableDenomerator = new ArrayList<>();
        for (TimeTableResult timeTableResult : timeTableData) {
            if (timeTableResult.frequency == Frequency.NUMERATOR) {
                timeTableNumerator.add(timeTableResult);
            } else if (timeTableResult.frequency == Frequency.DENOMINATOR) {
                timeTableDenomerator.add(timeTableResult);
            } else if (timeTableResult.frequency == Frequency.WEEKLY) {
                timeTableNumerator.add(timeTableResult);
                timeTableDenomerator.add(timeTableResult);
            }
        }
        XSSFWorkbook workbook = new XSSFWorkbook("src/main/resources/template.xlsx");
        CellStyle cs = workbook.createCellStyle();
        cs.setWrapText(true);
        Sheet sheetNumerator = workbook.getSheetAt(0);
        Sheet sheetDenominator = workbook.getSheetAt(1);
        saveDataToSheet(cs, sheetNumerator, timeTableNumerator, selectorData, dayOfTheWeek);
        saveDataToSheet(cs, sheetDenominator, timeTableDenomerator, selectorData, dayOfTheWeek.plusWeeks(1));
        Scanner sc = new Scanner(System.in);
        String path = sc.nextLine();
        try (
                FileOutputStream outputStream = new FileOutputStream(path + "/report_" + selectorData.departmentName + ".xlsx")) {
            workbook.write(outputStream);
        }
    }

    public static void saveDataToSheet(CellStyle cs, Sheet sheet, List<TimeTableResult> prepared, SelectorTableResult selectorData, LocalDate dayOfTheWeek){
        Cell days = sheet.getRow(1).getCell(0);
        Cell department = sheet.getRow(2).getCell(0);
        LocalDate monday = dayOfTheWeek.with(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.getValue());
        LocalDate saturday = dayOfTheWeek.with(ChronoField.DAY_OF_WEEK, DayOfWeek.SATURDAY.getValue());
        String fromToData = days.getStringCellValue()
                .replace("#fromDate", monday.format(DATE_TIME_PATTERN)
                .replace("#toDate", saturday.format(DATE_TIME_PATTERN)));
        String departmentName = department.getStringCellValue().replace("#departmentName", selectorData.departmentName);
        days.setCellValue(fromToData);
        department.setCellValue(departmentName);
        int counter = 1;
        var j = sheet.getPhysicalNumberOfRows();
        String previousName = null;
        int regionStart = 6;
        int offset = 6;
        for (int i = 0; i < prepared.size(); i++) {
            var currentTimetable = prepared.get(i);
            Row createdRow = sheet.createRow(j);
            createdRow.setHeight((short) 750);
            sheet.addMergedRegion(new CellRangeAddress(j, j, 2, 3));
            Cell professorNumbers = createdRow.createCell(0);
            Cell fioCell = createdRow.createCell(1);
            fioCell.setCellStyle(cs);
            Cell dateTimeTypeCell = createdRow.createCell(2);
            dateTimeTypeCell.setCellStyle(cs);
            dateTimeTypeCell.setCellValue(String.format("%s, %s, %s, %s", currentTimetable.day, currentTimetable.frequency.htmlValue, currentTimetable.time, currentTimetable.typeOfSubject));
            sheet.addMergedRegion(new CellRangeAddress(j, j, 4, 5));
            Cell groupCell = createdRow.createCell(4);
            groupCell.setCellStyle(cs);
            groupCell.setCellValue(currentTimetable.group);
            Cell placeCell = createdRow.createCell(6);
            placeCell.setCellStyle(cs);
            placeCell.setCellValue(currentTimetable.place);
            PropertyTemplate propertyTemplate = new PropertyTemplate();
            propertyTemplate.drawBorders(new CellRangeAddress(j, j, 0, 7),
                    BorderStyle.THIN, BorderExtent.ALL);
            propertyTemplate.applyBorders(sheet);
            j++;
            if (!currentTimetable.fio.equals(previousName)) {
                professorNumbers.setCellValue(counter++);
                previousName = currentTimetable.fio;
                fioCell.setCellValue(previousName);
                if (i != 0) {
                    sheet.addMergedRegion(new CellRangeAddress(regionStart, j - 2, 0, 0));
                    sheet.addMergedRegion(new CellRangeAddress(regionStart, j - 2, 1, 1));
                    regionStart = j - 1;
                }
            }
        }
        sheet.getRow(regionStart).getCell(0).setCellValue(counter - 1);
        sheet.getRow(regionStart).getCell(1).setCellValue(previousName);
        sheet.addMergedRegion(new CellRangeAddress(regionStart, prepared.size() + offset - 1, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(regionStart, prepared.size() + offset - 1, 1, 1));
    }
}
