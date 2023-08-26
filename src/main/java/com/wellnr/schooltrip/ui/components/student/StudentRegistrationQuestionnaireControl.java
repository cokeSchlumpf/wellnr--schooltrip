package com.wellnr.schooltrip.ui.components.student;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.wellnr.common.markup.Tuple2;
import com.wellnr.common.markup.When;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.payments.PriceLineItem;
import com.wellnr.schooltrip.core.model.student.questionaire.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class StudentRegistrationQuestionnaireControl
    extends AbstractCompositeField<VerticalLayout, StudentRegistrationQuestionnaireControl, Questionaire> {

    private static final String SUM_LABEL = "Gesamt";

    private final DisciplinSection disciplinSection;
    private final CostSection costSection;
    private final AdditionalInformationSection additionalInformationSection;

    private SchoolTrip schoolTrip;

    public StudentRegistrationQuestionnaireControl(SchoolTrip schoolTrip, Student student) {
        super(student.getQuestionaire().orElse(Questionaire.empty()));

        this.schoolTrip = schoolTrip;

        disciplinSection = new DisciplinSection(student, this.getEmptyValue().getDisziplin());
        additionalInformationSection = new AdditionalInformationSection(this.getEmptyValue());
        costSection = new CostSection(schoolTrip, this.getEmptyValue());

        disciplinSection.addValueChangeListener(event -> {
            var questionnaire = this
                .getValue()
                .withDisziplin(event.getValue());

            this.setModelValue(questionnaire, true);
            this.costSection.setValue(this.schoolTrip, questionnaire);
        });

        additionalInformationSection.addValueChangeListener(event -> {
            var questionnaire = this
                .getValue()
                .withNutrition(event.getValue().getNutrition())
                .withComment(event.getValue().getComment());

            this.setModelValue(questionnaire, true);
            this.costSection.setValue(this.schoolTrip, questionnaire);
        });

        getContent().setMargin(false);
        getContent().setPadding(false);
        getContent().add(disciplinSection);
        getContent().add(additionalInformationSection);
        getContent().add(costSection);
    }

    @Override
    protected void setPresentationValue(Questionaire newPresentationValue) {
        this.disciplinSection.setValue(newPresentationValue.getDisziplin());
        this.additionalInformationSection.setValue(newPresentationValue);
        this.costSection.setValue(this.schoolTrip, newPresentationValue);
    }

    @Override
    public void setValue(Questionaire value) {
        super.setValue(value);
        this.setPresentationValue(value);
    }

    public void setSchoolTripAndStudent(SchoolTrip schoolTrip, Student student) {
        var questionnaire = student.getQuestionaire().orElse(Questionaire.empty());

        this.schoolTrip = schoolTrip;
        this.disciplinSection.setStudent(student);
        this.costSection.setValue(this.schoolTrip, questionnaire);

        this.setValue(questionnaire);
    }

    private static class DisciplinSection extends AbstractCompositeField<VerticalLayout, DisciplinSection, Discipline> {

        public final DisciplineRadioButtons discipline;

        private final ExperienceSection experienceSection;

        private final RentalSection rentalSection;

        public DisciplinSection(Student student, Discipline initialValue) {
            super(initialValue);

            this.discipline = new DisciplineRadioButtons();
            this.experienceSection = new ExperienceSection(student, initialValue.getExperience());
            this.rentalSection = new RentalSection(initialValue);

            discipline.addValueChangeListener(event -> rentalSection.setDiscipline(event.getValue()));
            experienceSection.addValueChangeListener(event -> this.setModelValue());
            rentalSection.addValueChangeListener(event -> this.setModelValue());

            this.getContent().setMargin(false);
            this.getContent().setPadding(false);
            this.getContent().add(discipline);
            this.getContent().add(experienceSection);
            this.getContent().add(rentalSection);

            this.setPresentationValue(initialValue);
            this.setStudent(student);
        }

        public void setStudent(Student student) {
            this.experienceSection.setStudent(student);
        }

        @Override
        protected void setPresentationValue(Discipline newPresentationValue) {
            this.discipline.setValue(newPresentationValue.getClass());
            this.rentalSection.setValue(Tuple2.apply(
                newPresentationValue.getRental().map(r -> new RentalDetails(r.getHeight(), r.getWeight())),
                newPresentationValue.getBootRental().map(r -> new BootRentalDetails(r.getSize()))
            ));
        }

        @Override
        public void setValue(Discipline value) {
            super.setValue(value);
            this.setPresentationValue(value);
        }

        private void setModelValue() {
            if (this.discipline.getValue().equals(Ski.class)) {
                var value = Ski.apply(this.experienceSection.getValue());

                var rental = this.rentalSection.getValue().get_1().map(details ->
                    SkiRental.apply(details.height, details.weight)
                );

                var bootRental = this.rentalSection.getValue().get_2().map(details ->
                    SkiBootRental.apply(details.size)
                );

                if (rental.isPresent()) {
                    value = value.withSkiRental(rental.get());
                }

                if (bootRental.isPresent()) {
                    value = value.withSkiBootRental(bootRental.get());
                }

                this.setModelValue(value, true);
            } else if (this.discipline.getValue().equals(Snowboard.class)) {
                var value = Snowboard.apply(this.experienceSection.getValue());

                var rental = this.rentalSection.getValue().get_1().map(details ->
                    SnowboardRental.apply(details.height, details.weight)
                );

                var bootRental = this.rentalSection.getValue().get_2().map(details ->
                    SnowboardBootRental.apply(details.size)
                );

                if (rental.isPresent()) {
                    value = value.withSnowboardRental(rental.get());
                }

                if (bootRental.isPresent()) {
                    value = value.withSnowboardBootRental(bootRental.get());
                }

                this.setModelValue(value, true);
            }
        }

    }

    private static class ExperienceSection extends AbstractCompositeField<VerticalLayout, ExperienceSection,
        Experience> {

        public final Paragraph info;

        public final ExperienceRadioButtons experience;

        public ExperienceSection(Student student, Experience experience) {
            super(experience);
            this.info = new Paragraph();
            this.experience = new ExperienceRadioButtons();

            this.experience.addValueChangeListener(event -> setModelValue(event.getValue(), true));

            this.getContent().setMargin(false);
            this.getContent().setPadding(false);
            this.getContent().add(new H4("Erfahrungs-Level des Kindes"));
            this.getContent().add(info);
            this.getContent().add(this.experience);

            this.setStudent(student);
        }

        public void setStudent(Student student) {
            this.info.removeAll();
            this.info.add(
                "Bitte geben Sie an, wie erfahren " + student.getFirstName() + " in der gewählten " +
                    "Sportart ist. Lorem " +
                    "Ipsum, erklären was die Level bedeuten."
            );
        }

        @Override
        protected void setPresentationValue(Experience newPresentationValue) {
            this.experience.setValue(newPresentationValue);
        }

        @Override
        public void setValue(Experience value) {
            super.setValue(value);
            this.setPresentationValue(value);
        }
    }

    private static class RentalSection extends AbstractCompositeField<
        VerticalLayout, RentalSection, Tuple2<Optional<RentalDetails>, Optional<BootRentalDetails>>> {

        private final RentalRadioButtons rental;

        private final RentalDetailsForm rentalDetails;

        private final BootRentalRadioButtons bootRental;

        private final BootRentalDetailsForm bootRentalDetails;

        public RentalSection(Discipline discipline) {
            super(Tuple2.apply(
                discipline.getRental().map(r -> new RentalDetails(r.getHeight(), r.getWeight())),
                discipline.getBootRental().map(r -> new BootRentalDetails(r.getSize()))
            ));

            this.rental = new RentalRadioButtons();
            this.rentalDetails = new RentalDetailsForm();
            this.bootRental = new BootRentalRadioButtons();
            this.bootRentalDetails = new BootRentalDetailsForm();

            this.rentalDetails.setEnabled(false);
            this.bootRentalDetails.setEnabled(false);

            rental.addValueChangeListener(event -> {
                rentalDetails.setEnabled(event.getValue());
                updateModelValue(true);
            });

            bootRental.addValueChangeListener(event -> {
                bootRentalDetails.setEnabled(event.getValue());
                updateModelValue(true);
            });

            this.getContent().setMargin(false);
            this.getContent().setPadding(false);
            this.getContent().add(new H4("Material-Ausleihe"));
            this.getContent().add(new Paragraph("Lorem Ipsum dolor"));
            this.getContent().add(rental);
            this.getContent().add(rentalDetails);
            this.getContent().add(bootRental);
            this.getContent().add(bootRentalDetails);
        }

        public void setDiscipline(Class<? extends Discipline> discipline) {
            rental.setDiscipline(discipline);
            bootRental.setDiscipline(discipline);

            this.updateModelValue(false);
        }

        @Override
        protected void setPresentationValue(Tuple2<Optional<RentalDetails>, Optional<BootRentalDetails>> newPresentationValue) {
            var maybeRental = newPresentationValue.get_1();
            var maybeBootRental = newPresentationValue.get_2();

            if (maybeRental.isPresent()) {
                var rental = maybeRental.get();
                this.rental.setValue(true);
                this.rentalDetails.setEnabled(true);
                this.rentalDetails.setValue(rental);
            } else {
                this.rental.setValue(false);
                this.rentalDetails.setEnabled(false);
            }

            if (maybeBootRental.isPresent()) {
                var bootRental = maybeBootRental.get();
                this.bootRental.setValue(true);
                this.bootRentalDetails.setEnabled(true);
                this.bootRentalDetails.setValue(bootRental);
            } else {
                this.bootRental.setValue(false);
                this.bootRentalDetails.setEnabled(false);
            }
        }

        @Override
        public void setValue(Tuple2<Optional<RentalDetails>, Optional<BootRentalDetails>> value) {
            super.setValue(value);
            this.setPresentationValue(value);
        }

        private void updateModelValue(boolean fromClient) {
            Optional<RentalDetails> rental = Optional.empty();
            Optional<BootRentalDetails> bootRental = Optional.empty();

            if (this.rental.getValue()) {
                var rentalDetails = this.rentalDetails.getValue();
                rental = Optional.of(rentalDetails);
            }

            if (this.bootRental.getValue()) {
                var bootRentalDetails = this.bootRentalDetails.getValue();
                bootRental = Optional.of(bootRentalDetails);
            }

            setModelValue(Tuple2.apply(
                rental, bootRental
            ), fromClient);
        }
    }

    private static class AdditionalInformationSection extends AbstractCompositeField<VerticalLayout,
        AdditionalInformationSection, Questionaire> {

        public final NutritionCheckboxes nutrition;

        public final TextArea comments;

        public AdditionalInformationSection(Questionaire questionaire) {
            super(questionaire);

            nutrition = new NutritionCheckboxes();
            nutrition.addValueChangeListener(event -> {
                var q = this.getValue().withNutrition(event.getValue());
                setModelValue(q, true);
            });

            comments = new TextArea("Möchten Sie uns noch etwas mitteilen?");
            comments.setWidthFull();
            comments.addValueChangeListener(event -> {
                var q = this.getValue().withComment(event.getValue());
                setModelValue(q, true);
            });

            this.getContent().setMargin(false);
            this.getContent().setPadding(false);
            this.getContent().add(new H4("Weitere Informationen"));
            this.getContent().add(new Paragraph("Lorem Ipsum dolor"));
            this.getContent().add(nutrition);
            this.getContent().add(comments);
        }

        @Override
        protected void setPresentationValue(Questionaire newPresentationValue) {
            this.nutrition.setValue(newPresentationValue.getNutrition());
            this.comments.setValue(newPresentationValue.getComment());
        }

        @Override
        public void setValue(Questionaire value) {
            super.setValue(value);
            this.setPresentationValue(value);
        }
    }

    private static class CostSection extends VerticalLayout {

        private final Grid<PriceLineItem> grid;

        private final DecimalFormat format;

        public CostSection(SchoolTrip schoolTrip, Questionaire questionaire) {
            format = new DecimalFormat("#.00");
            grid = new Grid<>(PriceLineItem.class, false);
            grid.addComponentColumn(item -> {
                if (item.label().equals(SUM_LABEL)) {
                    var span = new Span(item.label());
                    span.addClassName(LumoUtility.FontWeight.BOLD);

                    return span;
                } else {
                    return new Text(item.label());
                }
            }).setHeader("Position");
            grid
                .addComponentColumn(item -> {
                    var amount = format.format(item.amount()) + " €";

                    if (item.label().equals(SUM_LABEL)) {
                        var span = new Span(amount);
                        span.addClassName(LumoUtility.FontWeight.BOLD);

                        return span;
                    } else {
                        return new Text(amount);
                    }
                })
                .setHeader("Betrag")
                .setWidth("100px")
                .setTextAlign(ColumnTextAlign.END);

            grid.setAllRowsVisible(true);

            this.setMargin(false);
            this.setPadding(false);
            this.add(new H4("Kosten"));
            this.add(new Paragraph("Übersicht über entstehende Kosten."));
            this.add(grid);

            this.setValue(schoolTrip, questionaire);
        }

        public void setValue(SchoolTrip schoolTrip, Questionaire questionaire) {
            var items = Student.calculatePriceLineItems(schoolTrip, questionaire);
            var allItems = new ArrayList<>(items.getItems());

            allItems.add(new PriceLineItem(SUM_LABEL, items.getSum()));
            grid.setItems(allItems);
        }

    }

    private static class DisciplineRadioButtons extends RadioButtonGroup<Class<? extends Discipline>> {

        @SuppressWarnings("unchecked")
        public DisciplineRadioButtons() {
            this.setLabel("Meine Kind möchte teilnehmen:");
            this.setItems(Ski.class, Snowboard.class);

            this.setItemLabelGenerator(cls -> {
                if (Ski.class.isAssignableFrom(cls)) {
                    return "Ski";
                } else {
                    return "Snowboard";
                }
            });

            this.setValue(Ski.class);
            this.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        }

    }

    private static class ExperienceRadioButtons extends RadioButtonGroup<Experience> {

        public ExperienceRadioButtons() {
            this.setLabel("Erfahrung");
            this.setItems(Experience.BEGINNER, Experience.INTERMEDIATE, Experience.EXPERT);
            this.setItemLabelGenerator(exp -> switch (exp) {
                case BEGINNER -> "Anfänger";
                case INTERMEDIATE -> "Fortgeschritten/ Erste Erfahrungen gesammelt.";
                case EXPERT -> "Profi";
            });
            this.setValue(Experience.BEGINNER);
            this.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        }

    }

    private static class RentalRadioButtons extends RadioButtonGroup<Boolean> {

        public RentalRadioButtons() {
            this.setLabel("");
            this.setItems(Boolean.FALSE, Boolean.TRUE);
            this.setItemLabelGenerator(exp ->
                When
                    .isTrue(exp)
                    .then("Mein Kind möchte Ski und Zubehör ausleihen.")
                    .otherwise("Mein Kind nutzt seine eigenen Ski.")
            );
            this.setValue(Boolean.FALSE);
            this.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        }

        public void setDiscipline(Class<? extends Discipline> discipline) {
            if (discipline.equals(Ski.class)) {
                this.setItemLabelGenerator(exp ->
                    When
                        .isTrue(exp)
                        .then("Mein Kind möchte Ski und Zubehör ausleihen.")
                        .otherwise("Mein Kind nutzt seine eigenen Ski.")
                );
            } else {
                this.setItemLabelGenerator(exp ->
                    When
                        .isTrue(exp)
                        .then("Mein Kind möchte ein Snowboard ausleihen.")
                        .otherwise("Mein Kind nutzt sein eigenes Snowboard.")
                );
            }
        }

    }

    private static class NutritionCheckboxes
        extends AbstractCompositeField<VerticalLayout, NutritionCheckboxes, Nutrition> {

        private static final String VEGETARIAN = "vegetarian";
        private static final String HALAL = "halal";

        private final CheckboxGroup<String> checkboxGroup;

        public NutritionCheckboxes() {
            super(Nutrition.apply());

            this.checkboxGroup = new CheckboxGroup<>("Essgewohnheiten");
            this.checkboxGroup.setItems(VEGETARIAN, HALAL);
            this.checkboxGroup.setItemLabelGenerator(s -> {
                if (s.equals(VEGETARIAN)) {
                    return "Vegetarisch";
                } else {
                    return "Halal";
                }
            });
            this.checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
            this.checkboxGroup.addValueChangeListener(event -> this.setModelValue(
                Nutrition.apply(isVegetarian(), isHalal()), true
            ));

            this.getContent().add(this.checkboxGroup);
            this.getContent().setPadding(false);
            this.getContent().setMargin(false);
        }

        public boolean isVegetarian() {
            return this.checkboxGroup.getValue().contains(VEGETARIAN);
        }

        public boolean isHalal() {
            return this.checkboxGroup.getValue().contains(HALAL);
        }

        @Override
        protected void setPresentationValue(Nutrition newPresentationValue) {
            Set<String> selected = new HashSet<>();

            if (newPresentationValue.isHalal()) {
                selected.add(HALAL);
            }

            if (newPresentationValue.isVegetarian()) {
                selected.add(VEGETARIAN);
            }

            this.checkboxGroup.setValue(selected);
            this.checkboxGroup.setEnabled(true);
        }

        @Override
        public void setValue(Nutrition value) {
            super.setValue(value);
            this.setPresentationValue(value);
        }

    }

    private record RentalDetails(Integer height, Integer weight) {

    }

    private static class RentalDetailsForm extends AbstractCompositeField<
        FormLayout, RentalDetailsForm, RentalDetails> {

        public final IntegerField height;
        public final IntegerField weight;

        public RentalDetailsForm() {
            super(new RentalDetails(165, 40));
            height = new IntegerField("Körpergröße (in cm)");
            height.setValue(165);
            height.setMin(120);
            height.setMax(230);

            weight = new IntegerField("Körpergewicht (in KG)");
            weight.setValue(40);
            weight.setMin(30);
            weight.setMax(300);

            height.addValueChangeListener(event ->
                setModelValue(new RentalDetails(event.getValue(), weight.getValue()), true)
            );

            weight.addValueChangeListener(event ->
                setModelValue(new RentalDetails(height.getValue(), event.getValue()), true)
            );

            this.getContent().setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
            this.getContent().add(height, weight);
        }

        public void setEnabled(boolean enabled) {
            this.height.setEnabled(enabled);
            this.weight.setEnabled(enabled);
        }

        @Override
        protected void setPresentationValue(RentalDetails newPresentationValue) {
            this.height.setValue(newPresentationValue.height);
            this.weight.setValue(newPresentationValue.weight);
        }

        @Override
        public void setValue(RentalDetails value) {
            super.setValue(value);
            this.setPresentationValue(value);
        }

    }

    private static class BootRentalRadioButtons extends RadioButtonGroup<Boolean> {

        public BootRentalRadioButtons() {
            this.setLabel("Ausliehe Ski-Schuhe");
            this.setItems(Boolean.FALSE, Boolean.TRUE);
            this.setItemLabelGenerator(rent -> {
                if (rent) {
                    return "Mein Kind benötigt Ski-Schuhe";
                } else {
                    return "Mein Kind hat eigene Ski-Schuhe";
                }
            });
            this.setValue(Boolean.FALSE);
            this.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        }

        public void setDiscipline(Class<? extends Discipline> discipline) {
            if (discipline.equals(Ski.class)) {
                this.setItemLabelGenerator(exp ->
                    When
                        .isTrue(exp)
                        .then("Mein Kind benötigt Ski-Schuhe.")
                        .otherwise("Mein Kind nutzt seine eigenen Ski-Schuhe.")
                );
            } else {
                this.setItemLabelGenerator(exp ->
                    When
                        .isTrue(exp)
                        .then("Mein Kind möchte ein Snowboard Boots ausleihen.")
                        .otherwise("Mein Kind nutzt sein eigenen Boots.")
                );
            }
        }
    }

    private record BootRentalDetails(Integer size) {

    }

    private static class BootRentalDetailsForm extends AbstractCompositeField<FormLayout, BootRentalDetailsForm,
        BootRentalDetails> {

        public final IntegerField size;

        public BootRentalDetailsForm() {
            super(new BootRentalDetails(35));
            size = new IntegerField("Schuhgröße");
            size.setValue(35);
            size.setMax(20);
            size.setMax(60);

            size.addValueChangeListener(event -> setModelValue(new BootRentalDetails(event.getValue()), true));

            this.getContent().setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
            this.getContent().add(size);
        }

        public void setEnabled(boolean enabled) {
            this.size.setEnabled(enabled);
        }

        @Override
        protected void setPresentationValue(BootRentalDetails newPresentationValue) {
            this.size.setValue(newPresentationValue.size);
        }

        @Override
        public void setValue(BootRentalDetails value) {
            super.setValue(value);
            this.setPresentationValue(value);
        }

    }

}
