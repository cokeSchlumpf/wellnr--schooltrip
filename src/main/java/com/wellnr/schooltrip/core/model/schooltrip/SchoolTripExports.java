package com.wellnr.schooltrip.core.model.schooltrip;

import com.google.zxing.EncodeHintType;
import com.wellnr.common.Operators;
import com.wellnr.common.markup.Tuple2;
import com.wellnr.schooltrip.core.application.SchoolTripApplicationConfiguration;
import com.wellnr.schooltrip.core.model.student.Gender;
import com.wellnr.schooltrip.core.model.student.RegistrationState;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.StudentsReadRepository;
import com.wellnr.schooltrip.core.model.student.questionaire.*;
import com.wellnr.schooltrip.core.model.user.User;
import com.wellnr.schooltrip.core.model.user.rbac.DomainPermissions;
import com.wellnr.schooltrip.core.utils.ExcelExport;
import com.wellnr.schooltrip.core.utils.FileZipper;
import lombok.AllArgsConstructor;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class to bundle export functionalities for a school trip.
 */
@AllArgsConstructor(staticName = "apply")
public class SchoolTripExports {

    SchoolTrip schoolTrip;

    /**
     * This operation creates an Excel file containing the student data for the invitation
     * mailing. The file returned will be a ZIP-file the Excel file and QR-code images.
     *
     * @param executor The user executing the operation.
     * @param students The repository to read/write student information.
     * @param config   The configuration of the application.
     * @return The exported data.
     */
    public Path exportInviteLetterMailingData(
        User executor, StudentsReadRepository students, SchoolTripApplicationConfiguration config
    ) {

        // Check permissions
        executor.checkPermission(
            DomainPermissions.ManageSchoolTrips.apply(),
            DomainPermissions.ManageSchoolTrip.apply(this.schoolTrip.getId())
        );

        // Create data.
        var tmpOutputFile = Operators.suppressExceptions(
            () -> Files.createTempFile("schooltrips", ".zip")
        );

        var tmpDirectory = Operators.suppressExceptions(
            () -> Files.createTempDirectory("schooltrips")
        );

        var qrCodesDir = Operators.suppressExceptions(
            () -> Files.createDirectory(tmpDirectory.resolve("QRCodes"))
        );

        /*
         * Create Excel file and QR Code images.
         */
        var excelFile = tmpDirectory.resolve("students.xlsx");
        var workbook = Operators.suppressExceptions(() -> new XSSFWorkbook());
        var sheet = workbook.createSheet("Schüler");

        var row = sheet.createRow(0);
        row.createCell(0).setCellValue("School Class");
        row.createCell(1).setCellValue("Last Name");
        row.createCell(2).setCellValue("First Name");
        row.createCell(3).setCellValue("Token");
        row.createCell(4).setCellValue("Registration Link");
        row.createCell(5).setCellValue("Registration QR-Code");
        row.createCell(6).setCellValue("Anrede");

        var allStudents = getAllStudents(students);
        for (var i = 0; i < allStudents.size(); i++) {
            var student = allStudents.get(i);

            // Create QR-Code
            var qrCodeFileName =
                student.getSchoolClass() + "--" + Operators.stringToKebabCase(student.getLastName()) + "--" + Operators.stringToKebabCase(student.getFirstName()) + ".png";

            var qrCodeLink = config.getUi().getBaseUrl() + "students/response/" + student.getToken();

            var qrCodeFile = QRCode
                .from(qrCodeLink)
                .withSize(config.getQrCodeSize(), config.getQrCodeSize())
                .to(ImageType.PNG)
                .withHint(EncodeHintType.MARGIN, "0")
                .file();

            Operators.suppressExceptions(
                () -> Files.move(qrCodeFile.toPath(), qrCodesDir.resolve(qrCodeFileName))
            );

            // Create row in Excel
            row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(student.getSchoolClass());
            row.createCell(1).setCellValue(student.getLastName());
            row.createCell(2).setCellValue(student.getFirstName());
            row.createCell(3).setCellValue(student.getToken());
            row.createCell(4).setCellValue(qrCodeLink);
            row.createCell(5).setCellValue(qrCodeFileName);
            row.createCell(6).setCellValue(student.getGender().equals(Gender.Male) ? "lieber" : "liebe");
        }

        // Save Excel Workbook
        try (var fos = new FileOutputStream(excelFile.toFile())) {
            workbook.write(fos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // ZIP directory and save as temporary file
        FileZipper.zip(tmpDirectory, tmpOutputFile, "invitation-mailing");
        Operators.ignoreExceptions(() -> FileUtils.deleteDirectory(tmpDirectory.toFile()));

        return tmpOutputFile;
    }

    public Path exportRentals(
        User executor, StudentsReadRepository students
    ) {
        var i18n = executor.getMessages();

        // Check permissions
        executor.checkPermission(
            DomainPermissions.ManageSchoolTrips.apply(),
            DomainPermissions.ManageSchoolTrip.apply(this.schoolTrip.getId())
        );

        var tmpDirectory = Operators.suppressExceptions(
            () -> Files.createTempDirectory("schooltrips")
        );

        var outputDir = Operators.suppressExceptions(
            () -> Files.createTempDirectory("schooltrips")
        );

        List<List<Object>> skiRentals = new ArrayList<>();
        List<List<Object>> snowboardRentals = new ArrayList<>();

        List<String> skiHeaders = List.of(
            i18n.id(), i18n.schoolClass(), i18n.lastName(), i18n.firstName(), i18n.experience(),
            i18n.skiRental(), i18n.bodyHeight(), i18n.bodyWeight(), i18n.bootRental(), i18n.shoeSize(),
            i18n.helmetRental()
        );

        List<String> snowboardHeaders = List.of(
            i18n.id(), i18n.schoolClass(), i18n.lastName(), i18n.firstName(), i18n.experience(),
            i18n.snowboardRental(), i18n.bodyHeight(), i18n.bodyWeight(), i18n.bootRental(), i18n.shoeSize(),
            i18n.helmetRental()
        );

        this
            .getAllStudents(students)
            .stream()
            .filter(student ->
                student.getRegistrationState().equals(RegistrationState.REGISTERED) || student.getRegistrationState()
                    .equals(RegistrationState.WAITING_FOR_CONFIRMATION)
            )
            .filter(student -> student.getQuestionnaire().isPresent())
            .map(student -> Tuple2.apply(student, student.getQuestionnaire().get()))
            .forEach(tuple -> {
                var questionnaire = tuple._2;
                var student = tuple._1;

                if (questionnaire.getDisziplin() instanceof Ski ski) {
                    skiRentals.add(List.of(
                        student.getSchoolTripStudentId().orElse(0),
                        student.getSchoolClass(),
                        student.getLastName(),
                        student.getFirstName(),
                        i18n.experience(i18n, ski.getExperience()),
                        ski.getRental().isPresent() ? i18n.yes() : i18n.no(),
                        ski.getRental().<Object>map(SkiRental::getHeight).orElse(""),
                        ski.getRental().<Object>map(SkiRental::getWeight).orElse(""),
                        ski.getBootRental().isPresent() ? i18n.yes() : i18n.no(),
                        ski.getBootRental().<Object>map(SkiBootRental::getSize).orElse(""),
                        ski.hasHelmRental() ? i18n.yes() : i18n.no()
                    ));
                } else if (questionnaire.getDisziplin() instanceof Snowboard sb) {
                    snowboardRentals.add(List.of(
                        student.getSchoolTripStudentId().orElse(0),
                        student.getSchoolClass(),
                        student.getLastName(),
                        student.getFirstName(),
                        i18n.experience(i18n, sb.getExperience()),
                        sb.getRental().isPresent() ? i18n.yes() : i18n.no(),
                        sb.getRental().<Object>map(SnowboardRental::getHeight).orElse(""),
                        sb.getRental().<Object>map(SnowboardRental::getWeight).orElse(""),
                        sb.getBootRental().isPresent() ? i18n.yes() : i18n.no(),
                        sb.getBootRental().<Object>map(SnowboardBootRental::getSize).orElse(""),
                        sb.hasHelmRental() ? i18n.yes() : i18n.no()
                    ));
                }
            });

        var skiFile = tmpDirectory.resolve("ski-rentals.xlsx");
        var snowboardFile = tmpDirectory.resolve("snowboard-rentals.xlsx");

        try (
            var skiFos = new FileOutputStream(skiFile.toFile());
            var snowboardFos = new FileOutputStream(snowboardFile.toFile())
        ) {
            ExcelExport.createExcel(skiHeaders, skiRentals, skiFos);
            ExcelExport.createExcel(snowboardHeaders, snowboardRentals, snowboardFos);
        } catch (IOException e) {
            throw new RuntimeException("An exception ocurred while creating rental Excel exports.", e);
        }

        var zipFile = outputDir.resolve("rentals.zip");
        FileZipper.zip(tmpDirectory, zipFile, "rentals");
        Operators.ignoreExceptions(() -> FileUtils.deleteDirectory(tmpDirectory.toFile()));
        return zipFile;
    }

    private List<Student> getAllStudents(StudentsReadRepository students) {
        return students
            .findStudentsBySchoolTrip(new SchoolTripId(this.schoolTrip.getId()))
            .stream()
            .sorted(
                Comparator
                    .comparing(Student::getSchoolClass)
                    .thenComparing(Student::getLastName)
                    .thenComparing(Student::getFirstName)
            )
            .toList();
    }

}
