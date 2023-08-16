package com.wellnr.schooltrip.ui.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.shared.Registration;
import com.wellnr.common.functions.Function1;
import com.wellnr.common.markup.Tuple;
import com.wellnr.common.markup.Tuple2;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExcelImportDialog<RESULT_TYPE> extends Dialog {

    private final List<String> requiredFields;

    private final Grid<Row> previewDataGrid;

    private final Button btnGotoMapping;

    private final Button btnBackToUpload;

    private final Button btnFinish;

    private final Button btnBackToImportTable;

    private Table importedData;

    private List<Integer> mappedColumns;

    public ExcelImportDialog(List<String> requiredFields, Function1<List<Object>, RESULT_TYPE> createObject) {
        this.requiredFields = requiredFields;
        this.previewDataGrid = new Grid<>();

        this.btnGotoMapping = new Button("Next >");
        this.btnGotoMapping.addClickListener(event -> showMappingPage());

        this.btnBackToUpload = new Button("< Back");
        this.btnBackToUpload.addClickListener(event -> showUploadPage());

        this.btnFinish = new Button("Import Data");
        this.btnFinish.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        this.btnFinish.addClickListener(event -> {
            var result = this.importedData
                .getRows()
                .stream()
                .map(row -> {
                    var values = mappedColumns
                        .stream()
                        .map(row::getColumn)
                        .map(opt -> opt.orElse(null))
                        .toList();

                    return createObject.get(values);
                })
                .toList();

            this.close();
            this.removeFromParent();

            fireEvent(
                new DataImportedEvent<>(this, false, result)
            );
        });

        this.btnBackToImportTable = new Button("< Back");
        this.btnBackToImportTable.addClickListener(event -> showImportTablePage());

        this.setHeaderTitle("Import Excel File");
        this.setMinWidth("60%");
        this.setMinHeight("400px");

        this.getFooter().add(
            new Button("Previous")
        );

        this.getFooter().add(
            new Button("Next")
        );

        this.showUploadPage();

    }

    @SuppressWarnings({"unchecked", "UnusedReturnValue"})
    public Registration addDataImportedListener(
        ComponentEventListener<DataImportedEvent<RESULT_TYPE>> listener
    ) {
        var event = new DataImportedEvent<>(this, true, List.of());
        return addListener((Class<DataImportedEvent<RESULT_TYPE>>) event.getClass(), listener);
    }

    private Table readTableFromXlsx(InputStream is) {
        try (var workbook = new XSSFWorkbook(is)) {
            var sheet = workbook.getSheetAt(0);
            var rows = Lists.<Row>newArrayList();

            for (var rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                var row = sheet.getRow(rowIndex);
                var cols = Lists.newArrayList();

                for (var colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
                    var cell = row.getCell(colIndex);

                    switch (cell.getCellType()) {
                        case _NONE, BLANK -> cols.add("");
                        case BOOLEAN -> cols.add(cell.getBooleanCellValue());
                        case NUMERIC -> {
                            if (DateUtil.isCellDateFormatted(cell)) {
                                cols.add(cell.getDateCellValue());
                            } else {
                                cols.add(cell.getNumericCellValue());
                            }
                        }
                        case STRING -> cols.add(cell.getStringCellValue());
                        default -> cols.add("n/a");

                    }
                }

                rows.add(new Row(cols));
            }

            return new Table(true, rows);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearComponents() {
        this.removeAll();
        this.getFooter().removeAll();
    }

    private void showUploadPage() {
        this.clearComponents();

        var buffer = new MemoryBuffer();
        var upload = new Upload(buffer);
        upload.setSizeFull();

        upload.addSucceededListener(event -> {
            importedData = readTableFromXlsx(buffer.getInputStream());

            var columns = importedData.getColumns();
            var mappedColumns = this.requiredFields
                .stream()
                .map(field -> columns
                    .stream()
                    .filter(t -> t._2.equalsIgnoreCase(field))
                    .findFirst()
                    .orElse(columns.get(0))
                    ._1)
                .toList();

            this.mappedColumns = Lists.newArrayList();
            this.mappedColumns.addAll(mappedColumns);

            updateGrid();
            showImportTablePage();
        });

        this.add(upload);
    }

    private void showImportTablePage() {
        this.clearComponents();

        var includesHeader = new Checkbox("File includes header row.", true);
        includesHeader.addValueChangeListener(event -> {
            importedData.hasHeader = event.getValue();
            updateGrid();
        });

        this.add(includesHeader);
        this.add(this.previewDataGrid);
        this.getFooter().add(btnBackToUpload, btnGotoMapping);
    }

    private void showMappingPage() {
        this.clearComponents();

        var form = new FormLayout();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep(
            "0", 3
        ));

        var columns = importedData
            .getColumns()
            .stream()
            .collect(Collectors.toMap(
                t -> t._1,
                t -> t._2
            ));

        for (var i = 0; i < this.requiredFields.size(); i++) {
            var field = requiredFields.get(i);
            var finalIndex = i;

            var selectBox = new Select<Integer>();
            selectBox.setLabel(field);
            selectBox.setItems(columns.keySet());
            selectBox.setItemLabelGenerator(columns::get);
            selectBox.addValueChangeListener(
                event -> this.mappedColumns.set(finalIndex, event.getValue())
            );
            selectBox.setValue(this.mappedColumns.get(i));

            form.add(selectBox);
        }

        this.add(form);
        this.getFooter().add(btnBackToImportTable, btnFinish);
    }

    private void updateGrid() {
        if (Objects.isNull(importedData)) {
            return;
        }

        previewDataGrid.removeAllColumns();

        for (Tuple2<Integer, String> col : importedData.getColumns()) {
            previewDataGrid.addColumn(row -> row.getColumn(col._1).orElse("").toString()).setHeader(col._2);
        }
        previewDataGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        previewDataGrid.setItems(importedData.getRows());
        previewDataGrid.setSizeFull();
    }

    public static class DataImportedEvent<T> extends ComponentEvent<ExcelImportDialog<T>> {

        private final List<T> result;

        public DataImportedEvent(
            ExcelImportDialog<T> source, boolean fromClient, List<T> result
        ) {
            super(source, fromClient);
            this.result = List.copyOf(result);
        }

        public List<T> getResult() {
            return result;
        }

    }

    @ToString
    @AllArgsConstructor
    private static class Table {

        private boolean hasHeader;

        private List<Row> rows;

        public List<Tuple2<Integer, String>> getColumns() {
            int maxColumnCount = rows
                .stream()
                .map(row -> row.data.size())
                .max(Comparator.comparing(i -> i))
                .orElse(0);

            if (this.hasHeader && this.rows.size() > 0) {
                var columns = Lists.<Tuple2<Integer, String>>newArrayList();
                var headerRow = rows.get(0);

                for (var i = 0; i < maxColumnCount; i++) {
                    if (headerRow.data.size() > i) {
                        columns.add(Tuple.apply(i, headerRow.data.get(i).toString()));
                    } else {
                        columns.add(Tuple.apply(i, "Column " + (i + 1)));
                    }
                }

                return List.copyOf(columns);
            } else {
                return IntStream
                    .range(1, maxColumnCount + 1)
                    .mapToObj(i -> Tuple.apply(i - 1, "Column " + i))
                    .collect(Collectors.toList());
            }
        }

        public List<Row> getRows() {
            if (this.hasHeader) {
                var result = Lists.<Row>newArrayList();
                result.addAll(this.rows);
                result.remove(0);

                return List.copyOf(result);
            } else {
                return List.copyOf(this.rows);
            }
        }

    }

    @ToString
    @AllArgsConstructor
    private static class Row {

        List<Object> data;

        public Optional<Object> getColumn(int index) {
            if (this.data.size() > index) {
                return Optional.ofNullable(data.get(index));
            } else {
                return Optional.empty();
            }
        }

    }

}
