package com.wellnr.schooltrip.core.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ExcelExport {

    public static void createExcel(List<String> headers, List<List<Object>> rows, OutputStream os) {
        validateInput(headers, rows);

        try (
            var workbook = new XSSFWorkbook();
        ) {
            var sheet = workbook.createSheet("Table 1");
            createHeaderRow(workbook, sheet, headers);
            createContentRows(workbook, sheet, rows);
            adjustColumnWidths(sheet, headers);
            workbook.write(os);
        } catch (IOException e) {
            throw new RuntimeException("An exception occurred writing Excel workbook.", e);
        }
    }

    private static void adjustColumnWidths(Sheet sheet, List<?> columns) {
        for (var i = 0; i < columns.size(); i++) {
            sheet.autoSizeColumn(i);

            sheet.setColumnWidth(i, Math.toIntExact(Math.round(sheet.getColumnWidth(i) * 1.5)));
        }
    }

    private static void createHeaderRow(Workbook workbook, Sheet sheet, List<String> headers) {
        var cellStyle = createHeaderCellStyle(workbook);
        var headerRow = sheet.createRow(0);

        for (var i = 0; i < headers.size(); i++) {
            var cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(cellStyle);
        }
    }

    private static CellStyle createHeaderCellStyle(Workbook workbook) {
        var style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.DOUBLE);
        style.setFont(createFont(workbook, true));

        return style;
    }

    private static Font createFont(Workbook workbook, boolean bold) {
        var font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 12);
        font.setBold(bold);

        return font;
    }

    private static void createContentRows(Workbook workbook, Sheet sheet, List<List<Object>> rows) {
        var cellStyles = createContentCellStyles(workbook, rows);

        for (var i = 0; i < rows.size(); i++) {
            var row = sheet.createRow(i + 1);

            for (var j = 0; j < rows.get(i).size(); j++) {
                var cell = row.createCell(j);
                setCellValue(cell, rows.get(i).get(j));
                cell.setCellStyle(cellStyles.get(j));
            }
        }
    }

    private static void setCellValue(Cell cell, Object value) {
        if (value instanceof Double dbl) {
            cell.setCellValue(dbl);
        } else if (value instanceof Integer intVal) {
            cell.setCellValue(intVal);
        } else if (value instanceof Date dateVal) {
            cell.setCellValue(dateVal);
        } else if (value instanceof LocalDate localDateVal) {
            cell.setCellValue(localDateVal);
        } else if (value instanceof LocalDateTime localDateTimeVal) {
            cell.setCellValue(localDateTimeVal);
        } else if (!(value instanceof String s && (s.isEmpty() || s.isBlank()))) {
            cell.setCellValue(value.toString());
        }
    }

    private static List<CellStyle> createContentCellStyles(Workbook workbook, List<List<Object>> rows) {
        return getColumnTypes(rows)
            .stream()
            .map(maybeType -> {
                var style = workbook.createCellStyle();
                style.setFont(createFont(workbook, false));

                if (maybeType.isPresent()) {
                    var type = maybeType.get();

                    if (type.isAssignableFrom(Double.class) || type.isAssignableFrom(double.class)) {
                        var fmt = workbook.createDataFormat();
                        style.setDataFormat(fmt.getFormat("0.00"));
                        style.setAlignment(HorizontalAlignment.RIGHT);
                    }  else if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(int.class)) {
                        style.setAlignment(HorizontalAlignment.RIGHT);
                    } else if (type.isAssignableFrom(Date.class) || type.isAssignableFrom(LocalDate.class)) {
                        var fmt = workbook.createDataFormat();
                        style.setDataFormat(fmt.getFormat("dd.MM.yyyy"));
                        style.setAlignment(HorizontalAlignment.RIGHT);
                    }
                }

                return style;
            })
            .toList();
    }

    private static void validateInput(List<String> headers, List<List<Object>> rows) {
        for (var row : rows) {
            if (row.size() != headers.size()) {
                throw new IllegalArgumentException("The number of headers and columns in each row must be the same.");
            }

            for (var i = 0; i < row.size(); i++) {
                // Null values are allowed.
                if (row.get(i) == null) {
                    continue;
                }

                // Each column must have similar types in each row.
                var columnTypes = getColumnTypes(rows);
                if (!columnTypes.get(i).orElse(Object.class).isAssignableFrom(row.get(i).getClass())) {
                    throw new IllegalArgumentException("Each column in each row must have similar types.");
                }
            }
        }
    }

    private static List<Optional<Class<?>>> getColumnTypes(List<List<Object>> rows) {
        if (rows.size() == 0) {
            return List.of();
        }

        List<Optional<Class<?>>> valueTypes = new ArrayList<>();

        for (var i = 0; i < rows.get(0).size(); i++) {
            Class<?> valueType = null;
            for (var j = 1; j < rows.size(); j++) {
                if (rows.get(j).get(i) == null || Objects.equals(rows.get(j).get(i).toString(), "")) {
                    continue;
                }

                valueType = rows.get(0).get(i).getClass();
                if (valueType != null) {
                    break;
                }
            }

            valueTypes.add(Optional.ofNullable(valueType));
        }

        return valueTypes;
    }

}
