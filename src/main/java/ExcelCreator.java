import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ExcelCreator {
    static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

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
        Sheet sheet = workbook.getSheetAt(0);
        saveDataToSheet(cs, sheet, timeTableNumerator, selectorData, dayOfTheWeek);
        saveDataToSheet(cs, workbook.getSheetAt(1), timeTableDenomerator, selectorData, dayOfTheWeek.plus(7, ChronoUnit.DAYS));
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
        LocalDate monday = dayOfTheWeek.with(ChronoField.DAY_OF_WEEK, 1);
        LocalDate saturday = dayOfTheWeek.with(ChronoField.DAY_OF_WEEK, 6);
        String fromToData = days.getStringCellValue().replace("#fromDate", monday.format(DT)
                .replace("#toDate", saturday.format(DT)));
        String departmentName = department.getStringCellValue().replace("#departmentName", selectorData.departmentName);
        days.setCellValue(fromToData);
        department.setCellValue(departmentName);
        int counter = 1;
        int j = 6;
        String previousName = null;
        int regionStart = 6;
        int offset = 6;
        for (int i = 0; i < prepared.size(); i++) {
            var currentTimetable = prepared.get(i);
            Row createdRow = sheet.createRow(j);
            createdRow.setHeight((short) 750);
            sheet.addMergedRegion(new CellRangeAddress(j, j, 2, 3));
            Cell numbers = createdRow.createCell(0);
            Cell createdCell1 = createdRow.createCell(1);
            createdCell1.setCellStyle(cs);
            Cell createdCell2 = createdRow.createCell(2);
            createdCell2.setCellStyle(cs);
            createdCell2.setCellValue(currentTimetable.day + "," + currentTimetable.frequency.htmlValue + "," + currentTimetable.time + "\n" + currentTimetable.typeOfSubject);
            sheet.addMergedRegion(new CellRangeAddress(j, j, 4, 5));
            Cell createdCell3 = createdRow.createCell(4);
            createdCell3.setCellStyle(cs);
            createdCell3.setCellValue(currentTimetable.group + "," + "________чел.");
            Cell createdCell4 = createdRow.createCell(6);
            createdCell4.setCellStyle(cs);
            createdCell4.setCellValue(currentTimetable.place);
            PropertyTemplate propertyTemplate = new PropertyTemplate();
            propertyTemplate.drawBorders(new CellRangeAddress(j, j, 0, 7),
                    BorderStyle.THIN, BorderExtent.ALL);
            propertyTemplate.applyBorders(sheet);
            j++;
            if (!currentTimetable.fio.equals(previousName)) {
                numbers.setCellValue(counter++);
                previousName = currentTimetable.fio;
                createdCell1.setCellValue(previousName);
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
