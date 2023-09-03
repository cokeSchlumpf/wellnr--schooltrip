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
import com.wellnr.common.Operators;
import com.wellnr.common.functions.Function1;
import com.wellnr.common.functions.Function2;
import com.wellnr.common.markup.Nothing;
import com.wellnr.common.markup.Tuple;
import com.wellnr.common.markup.Tuple2;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import com.wellnr.schooltrip.ui.components.forms.ApplicationForm;
import com.wellnr.schooltrip.ui.components.forms.ApplicationFormBuilder;
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

/**
 * An utitlity UI workflow to import data from an Excel file.
 *
 * @param <RESULT_TYPE>   The return type of data to be extracted from the Excel. The Excel will contain
 *                        a list (0..*) of this type.
 * @param <SETTINGS_TYPE> The type of the settings which might be collected during the workflow (e.g. information to
 *                        parse values from the Excel and transform to different data types (e.g. dates)).
 */
public class ExcelImportDialog<RESULT_TYPE, SETTINGS_TYPE> extends Dialog {

    private final SchoolTripMessages i18n;

    /**
     * A list of required fields (labels) which should be imported from the Excel.
     * This fields must be mapped into the RESULT_TYPE.
     */
    private final List<String> requiredFields;

    /**
     * The default settings of this import dialog.
     */
    private final SETTINGS_TYPE additionalSettingsDefaults;

    /**
     * Internal component to preview the imported data.
     */
    private final Grid<Row> previewDataGrid;

    /**
     * A button to go to the mapping step.
     */
    private final Button btnGotoMapping;

    /**
     * A back button to return to the upload step and restart the workflow.
     */
    private final Button btnBackToUpload;

    /**
     * A button to go to the settings step.
     */
    private final Button btnGotoSettings;

    /**
     * A button to go back to the mapping step.
     */
    private final Button btnBackToMapping;

    /**
     * A button to finish the workflow and import the data.
     */
    private final Button btnFinish;

    /**
     * A button to return to the mapping page of the dialog.
     */
    private final Button btnBackToImportTable;

    /**
     * The imported (raw) data from the Excel, in an intermediate data structure.
     */
    private Table importedData;

    /**
     * A set of columns to which the required fields are mapped.
     * Each item in the list indicates which column index maps to the required field.
     * E.g. mappedColumn[0] points to the column where requiredFields[0] is stored.
     */
    private List<Integer> mappedColumns;

    private ApplicationForm<SETTINGS_TYPE> settingsForm;

    /**
     * Creates a new instance of the workflow dialog.
     *
     * @param requiredFields             A set of required fields (labels) to extract from each line in the Excel
     *                                   file. Each field should be mapped to a column in the Excel during the worklfow.
     * @param createObject               A function which gets the list of read information from a row (in the order
     *                                   of the required fields) and should map the fields/ columns into the result
     *                                   type.
     * @param additionalSettingsDefaults A class providing additional settings (with default values)
     */
    @SuppressWarnings("unchecked")
    private ExcelImportDialog(
        List<String> requiredFields,
        Function2<List<Object>, SETTINGS_TYPE, RESULT_TYPE> createObject,
        SETTINGS_TYPE additionalSettingsDefaults,
        SchoolTripMessages i18n
    ) {
        this.i18n = i18n;

        this.requiredFields = requiredFields;
        this.additionalSettingsDefaults = additionalSettingsDefaults;
        this.previewDataGrid = new Grid<>();

        this.btnGotoMapping = new Button(i18n.nextButton());
        this.btnGotoMapping.addClickListener(event -> showMappingPage());

        this.btnBackToUpload = new Button(i18n.backButton());
        this.btnBackToUpload.addClickListener(event -> showUploadPage());

        this.btnGotoSettings = new Button(i18n.nextButton());
        this.btnGotoSettings.addClickListener(event -> showSettingsPage());

        this.btnBackToMapping = new Button(i18n.backButton());
        this.btnBackToMapping.addClickListener(event -> showMappingPage());

        if (!(this.additionalSettingsDefaults instanceof Nothing)) {
            this.settingsForm = new ApplicationFormBuilder<>(
                (Class<SETTINGS_TYPE>) this.additionalSettingsDefaults.getClass(),
                () -> this.additionalSettingsDefaults
            )
                .build();
        }

        this.btnFinish = new Button(i18n.importData());
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

                    if (Objects.isNull(this.settingsForm)) {
                        return createObject.get(values, additionalSettingsDefaults);
                    } else {
                        Operators.suppressExceptions(
                            () -> settingsForm.getBinder().writeBean(additionalSettingsDefaults)
                        );

                        return createObject.get(values, additionalSettingsDefaults);
                    }
                })
                .toList();

            this.close();
            this.removeFromParent();

            fireEvent(
                new DataImportedEvent<>(this, false, result)
            );
        });

        this.btnBackToImportTable = new Button(i18n.backButton());
        this.btnBackToImportTable.addClickListener(event -> showImportTablePage());

        this.setHeaderTitle(i18n.importExcelFile());
        this.setMinWidth("60%");
        this.setMinHeight("400px");

        this.getFooter().add(
            new Button(i18n.backButton())
        );

        this.getFooter().add(
            new Button(i18n.nextButton())
        );

        this.showUploadPage();

    }

    /**
     * Creates a new instance of the workflow dialog.
     *
     * @param requiredFields             A set of required fields (labels) to extract from each line in the Excel
     *                                   file. Each field should be mapped to a column in the Excel during the worklfow.
     * @param createObject               A function which gets the list of read information from a row (in the order
     *                                   of the required fields) and should map the fields/ columns into the result
     *                                   type.
     * @param additionalSettingsDefaults A class providing additional settings (with default values)
     */
    public static <R, S> ExcelImportDialog<R, S> apply(
        List<String> requiredFields,
        Function2<List<Object>, S, R> createObject,
        S additionalSettingsDefaults,
        SchoolTripMessages i18n
    ) {
        return new ExcelImportDialog<>(requiredFields, createObject, additionalSettingsDefaults, i18n);
    }

    /**
     * Creates a new instance of the workflow dialog.
     *
     * @param requiredFields A set of required fields (labels) to extract from each line in the Excel file. Each field
     *                       should be mapped to a column in the Excel during the worklfow.
     * @param createObject   A function which gets the list of read information from a row (in the order of the
     *                       required fields) and should map the fields/ columns into the result type.
     */
    public static <R> ExcelImportDialog<R, Nothing> apply(
        List<String> requiredFields,
        Function1<List<Object>, R> createObject,
        SchoolTripMessages i18n
    ) {
        return new ExcelImportDialog<>(requiredFields, (p, nothing) -> createObject.apply(p), Nothing.getInstance(), i18n);
    }

    @SuppressWarnings({"unchecked", "UnusedReturnValue"})
    public Registration addDataImportedListener(
        ComponentEventListener<DataImportedEvent<RESULT_TYPE, SETTINGS_TYPE>> listener
    ) {
        var event = new DataImportedEvent<>(this, true, List.of());
        return addListener((Class<DataImportedEvent<RESULT_TYPE, SETTINGS_TYPE>>) event.getClass(), listener);
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

        var includesHeader = new Checkbox(i18n.fileIncludesHeaderRow(), true);
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

        if (this.additionalSettingsDefaults instanceof Nothing) {
            this.getFooter().add(btnBackToImportTable, btnFinish);
        } else {
            this.getFooter().add(btnBackToImportTable, btnGotoSettings);
        }
    }

    private void showSettingsPage() {
        this.clearComponents();

        this.add(settingsForm);
        this.getFooter().add(btnBackToMapping, btnFinish);
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

    public static class DataImportedEvent<T, S> extends ComponentEvent<ExcelImportDialog<T, S>> {

        private final List<T> result;

        public DataImportedEvent(
            ExcelImportDialog<T, S> source, boolean fromClient, List<T> result
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
    private class Table {

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
                        columns.add(Tuple.apply(i, i18n.tableColumn() + " " + (i + 1)));
                    }
                }

                return List.copyOf(columns);
            } else {
                return IntStream
                    .range(1, maxColumnCount + 1)
                    .mapToObj(i -> Tuple.apply(i - 1, i18n.tableColumn() + " " + i))
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
