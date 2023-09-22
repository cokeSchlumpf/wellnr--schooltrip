package com.wellnr.schooltrip.ui.components.student;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.wellnr.common.markup.Tuple3;
import com.wellnr.common.markup.When;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.payments.PriceLineItem;
import com.wellnr.schooltrip.core.model.student.questionaire.*;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class StudentRegistrationQuestionnaireControl
    extends AbstractCompositeField<VerticalLayout, StudentRegistrationQuestionnaireControl, Questionnaire> {

    private final String SUM_LABEL;
    private final SchoolTripMessages i18n;

    private final DisciplinSection disciplinSection;
    private final CostSection costSection;
    private final AdditionalInformationSection additionalInformationSection;

    private SchoolTrip schoolTrip;

    public StudentRegistrationQuestionnaireControl(SchoolTripMessages i18n, SchoolTrip schoolTrip, Student student) {
        super(student.getQuestionnaire().orElse(Questionnaire.empty()));

        this.i18n = i18n;
        this.schoolTrip = schoolTrip;
        this.SUM_LABEL = i18n.amountSum();

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
        getContent().add(new Section(additionalInformationSection));
        getContent().add(new Section(costSection));
    }

    public void setSchoolTripAndStudent(SchoolTrip schoolTrip, Student student) {
        var questionnaire = student.getQuestionnaire().orElse(Questionnaire.empty());

        this.schoolTrip = schoolTrip;
        this.disciplinSection.setStudent(student);
        this.costSection.setValue(this.schoolTrip, questionnaire);

        this.setValue(questionnaire);
    }

    @Override
    public void setValue(Questionnaire value) {
        super.setValue(value);
        this.setPresentationValue(value);
    }

    @Override
    protected void setPresentationValue(Questionnaire newPresentationValue) {
        this.disciplinSection.setValue(newPresentationValue.getDisziplin());
        this.additionalInformationSection.setValue(newPresentationValue);
        this.costSection.setValue(this.schoolTrip, newPresentationValue);
    }

    private record RentalDetails(Integer height, Integer weight) {

    }

    private record BootRentalDetails(Integer size) {

    }

    private static class InfoTextParagraph extends Paragraph {

        public InfoTextParagraph(String text) {
            super(text);
            this.addClassName("app__student-registration-questionnaire__info-text");
        }

    }

    private static class Section extends Div {

        public Section(Component... components) {
            super(components);
            this.addClassName("app__student-registration-questionnaire__section");
        }

    }

    private class DisciplinSection extends AbstractCompositeField<VerticalLayout, DisciplinSection, Discipline> {

        public final DisciplineRadioButtons discipline;

        private final ExperienceSection experienceSection;

        private final RentalSection rentalSection;

        public DisciplinSection(Student student, Discipline initialValue) {
            super(initialValue);

            this.discipline = new DisciplineRadioButtons(student);
            this.experienceSection = new ExperienceSection(student, initialValue.getExperience());
            this.rentalSection = new RentalSection(initialValue);

            discipline.addValueChangeListener(event -> {
                rentalSection.setDiscipline(event.getValue());
                this.setModelValue();
            });
            experienceSection.addValueChangeListener(event -> this.setModelValue());
            rentalSection.addValueChangeListener(event -> this.setModelValue());

            this.getContent().setMargin(false);
            this.getContent().setPadding(false);

            this.getContent().add(new H4(i18n.disciplineSelection()));
            this.getContent().add(new Section(discipline));

            this.getContent().add(new Section(experienceSection));
            this.getContent().add(new Section(rentalSection));

            this.setPresentationValue(initialValue);
            this.setStudent(student);
        }

        public void setStudent(Student student) {
            this.discipline.setStudent(student);
            this.experienceSection.setStudent(student);
        }

        @Override
        public void setValue(Discipline value) {
            super.setValue(value);
            this.setPresentationValue(value);
        }

        @Override
        protected void setPresentationValue(Discipline newPresentationValue) {
            this.discipline.setValue(newPresentationValue.getClass());
            this.rentalSection.setValue(Tuple3.apply(
                newPresentationValue.getRental().map(r -> new RentalDetails(r.getHeight(), r.getWeight())),
                newPresentationValue.getBootRental().map(r -> new BootRentalDetails(r.getSize())),
                newPresentationValue.hasHelmRental()
            ));
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

                value = value.withHelmRental(this.rentalSection.getValue().get_3());

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

                value = value.withHelmRental(this.rentalSection.getValue().get_3());

                this.setModelValue(value, true);
            }
        }

    }

    private class ExperienceSection extends AbstractCompositeField<VerticalLayout, ExperienceSection,
        Experience> {

        public final Div info;

        public final ExperienceRadioButtons experience;

        public ExperienceSection(Student student, Experience experience) {
            super(experience);
            this.info = new Div();
            this.experience = new ExperienceRadioButtons();

            this.experience.addValueChangeListener(event -> setModelValue(event.getValue(), true));

            this.getContent().setMargin(false);
            this.getContent().setPadding(false);
            this.getContent().add(new H5(i18n.studentsExperienceLevel()));
            this.getContent().add(info);
            this.getContent().add(this.experience);

            this.setStudent(student);
        }

        public void setStudent(Student student) {
            this.info.removeAll();
            this.info.add(new InfoTextParagraph(i18n.studentsExperienceLevelInfo(student)));
        }

        @Override
        public void setValue(Experience value) {
            super.setValue(value);
            this.setPresentationValue(value);
        }

        @Override
        protected void setPresentationValue(Experience newPresentationValue) {
            this.experience.setValue(newPresentationValue);
        }
    }

    private class RentalSection extends AbstractCompositeField<
        VerticalLayout, RentalSection, Tuple3<Optional<RentalDetails>, Optional<BootRentalDetails>, Boolean>> {

        private final RentalRadioButtons rental;

        private final RentalDetailsForm rentalDetails;

        private final BootRentalRadioButtons bootRental;

        private final BootRentalDetailsForm bootRentalDetails;

        private final HelmetRentalForm helmetRentalForm;

        public RentalSection(Discipline discipline) {
            super(Tuple3.apply(
                discipline.getRental().map(r -> new RentalDetails(r.getHeight(), r.getWeight())),
                discipline.getBootRental().map(r -> new BootRentalDetails(r.getSize())),
                false
            ));

            this.rental = new RentalRadioButtons();
            this.rentalDetails = new RentalDetailsForm();
            this.bootRental = new BootRentalRadioButtons();
            this.bootRentalDetails = new BootRentalDetailsForm();
            this.helmetRentalForm = new HelmetRentalForm();

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

            helmetRentalForm.addValueChangeListener(event -> updateModelValue(true));

            this.getContent().setMargin(false);
            this.getContent().setPadding(false);
            this.getContent().add(new H5(i18n.materialRental()));
            this.getContent().add(i18n.materialRentalInfo());
            this.getContent().add(rental);
            this.getContent().add(rentalDetails);
            this.getContent().add(bootRental);
            this.getContent().add(bootRentalDetails);
            this.getContent().add(helmetRentalForm);
        }

        public void setDiscipline(Class<? extends Discipline> discipline) {
            rental.setDiscipline(discipline);
            bootRental.setDiscipline(discipline);

            this.updateModelValue(false);
        }

        @Override
        public void setValue(Tuple3<Optional<RentalDetails>, Optional<BootRentalDetails>, Boolean> value) {
            super.setValue(value);
            this.setPresentationValue(value);
        }

        @Override
        protected void setPresentationValue(Tuple3<Optional<RentalDetails>, Optional<BootRentalDetails>, Boolean> newPresentationValue) {
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

            this.helmetRentalForm.setValue(newPresentationValue._3);
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

            setModelValue(Tuple3.apply(
                rental, bootRental, helmetRentalForm.getValue()
            ), fromClient);
        }
    }

    private class AdditionalInformationSection extends AbstractCompositeField<VerticalLayout,
        AdditionalInformationSection, Questionnaire> {

        public final NutritionCheckboxes nutrition;

        public final TextArea comments;

        public AdditionalInformationSection(Questionnaire questionnaire) {
            super(questionnaire);

            nutrition = new NutritionCheckboxes();
            nutrition.addValueChangeListener(event -> {
                var q = this.getValue().withNutrition(event.getValue());
                setModelValue(q, true);
            });
            nutrition.setValue(questionnaire.getNutrition());

            comments = new TextArea("Möchten Sie uns noch etwas mitteilen?");
            comments.setWidthFull();
            comments.addValueChangeListener(event -> {
                var q = this.getValue().withComment(event.getValue());
                setModelValue(q, true);
            });
            comments.setValue(questionnaire.getComment());

            var form = new FormLayout();
            form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("650px", 2)
            );
            form.add(comments);

            this.getContent().setMargin(false);
            this.getContent().setPadding(false);
            this.getContent().add(
                new H4(i18n.additionalInformation()),
                new InfoTextParagraph(i18n.additionalInformationInfo()),
                nutrition,
                form
            );
        }

        @Override
        public void setValue(Questionnaire value) {
            super.setValue(value);
            this.setPresentationValue(value);
        }

        @Override
        protected void setPresentationValue(Questionnaire newPresentationValue) {
            this.nutrition.setValue(newPresentationValue.getNutrition());
            this.comments.setValue(newPresentationValue.getComment());
        }
    }

    private class CostSection extends VerticalLayout {

        private final Grid<PriceLineItem> grid;

        private final DecimalFormat format;

        public CostSection(SchoolTrip schoolTrip, Questionnaire questionnaire) {
            format = new DecimalFormat(i18n.currencyNumberFormat());
            grid = new Grid<>(PriceLineItem.class, false);
            grid.addComponentColumn(item -> {
                if (item.label().equals(SUM_LABEL)) {
                    var span = new Span(item.label());
                    span.addClassName(LumoUtility.FontWeight.BOLD);

                    return span;
                } else {
                    return new Text(item.label());
                }
            }).setHeader(i18n.invoicePosition());
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
                .setHeader(i18n.currencyAmount())
                .setWidth("100px")
                .setTextAlign(ColumnTextAlign.END);

            grid.setAllRowsVisible(true);

            this.setMargin(false);
            this.setPadding(false);
            this.add(
                new H4(i18n.costs()),
                new InfoTextParagraph(i18n.costsInfo()),
                grid
            );

            this.setValue(schoolTrip, questionnaire);
        }

        public void setValue(SchoolTrip schoolTrip, Questionnaire questionnaire) {
            var items = Student.calculatePriceLineItems(schoolTrip, questionnaire, i18n);
            var allItems = new ArrayList<>(items.getItems());

            allItems.add(new PriceLineItem(SUM_LABEL, items.getSum()));
            grid.setItems(allItems);
        }

    }

    private class DisciplineRadioButtons extends RadioButtonGroup<Class<? extends Discipline>> {

        @SuppressWarnings("unchecked")
        public DisciplineRadioButtons(Student student) {
            this.setItems(Ski.class, Snowboard.class);

            this.setStudent(student);
            this.setValue(Ski.class);
            this.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        }

        public void setStudent(Student student) {
            this.setItemLabelGenerator(cls -> {
                if (Ski.class.isAssignableFrom(cls)) {
                    return i18n.myChildWantsToSki(student);
                } else {
                    return i18n.myChildWantsToBoard(student);
                }
            });
        }

    }

    private class ExperienceRadioButtons extends RadioButtonGroup<Experience> {

        public ExperienceRadioButtons() {
            this.setItems(Experience.BEGINNER, Experience.INTERMEDIATE, Experience.EXPERT);
            this.setItemLabelGenerator(exp -> switch (exp) {
                case BEGINNER -> i18n.beginnerWithDesc();
                case INTERMEDIATE -> i18n.intermediateWithDesc();
                case EXPERT -> i18n.expertWithDesc();
            });
            this.setValue(Experience.BEGINNER);
            this.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        }

    }

    private class RentalRadioButtons extends RadioButtonGroup<Boolean> {

        public RentalRadioButtons() {
            this.setLabel("");
            this.setItems(Boolean.FALSE, Boolean.TRUE);
            this.setItemLabelGenerator(exp ->
                When
                    .isTrue(exp)
                    .then(i18n.studentWantsToRentSki())
                    .otherwise(i18n.studentWantsToUseOwnSki())
            );
            this.setValue(Boolean.FALSE);
            this.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        }

        public void setDiscipline(Class<? extends Discipline> discipline) {
            if (discipline.equals(Ski.class)) {
                this.setItemLabelGenerator(exp ->
                    When
                        .isTrue(exp)
                        .then(i18n.studentWantsToRentSki())
                        .otherwise(i18n.studentWantsToUseOwnSki())
                );
            } else {
                this.setItemLabelGenerator(exp ->
                    When
                        .isTrue(exp)
                        .then(i18n.studentWantsToRentSnowboard())
                        .otherwise(i18n.studentWantsToUseOwnSnowboard())
                );
            }
        }

    }

    private class NutritionCheckboxes
        extends AbstractCompositeField<VerticalLayout, NutritionCheckboxes, Nutrition> {

        private static final String VEGETARIAN = "vegetarian";
        private static final String HALAL = "halal";

        private final CheckboxGroup<String> checkboxGroup;

        public NutritionCheckboxes() {
            super(Nutrition.apply());

            this.checkboxGroup = new CheckboxGroup<>(i18n.nutrition());
            this.checkboxGroup.setItems(VEGETARIAN, HALAL);
            this.checkboxGroup.setItemLabelGenerator(s -> {
                if (s.equals(VEGETARIAN)) {
                    return i18n.nutritionVegetarian();
                } else {
                    return i18n.nutritionHalal();
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

        public boolean isHalal() {
            return this.checkboxGroup.getValue().contains(HALAL);
        }

        public boolean isVegetarian() {
            return this.checkboxGroup.getValue().contains(VEGETARIAN);
        }

        @Override
        public void setValue(Nutrition value) {
            super.setValue(value);
            this.setPresentationValue(value);
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

    }

    private class RentalDetailsForm extends AbstractCompositeField<
        FormLayout, RentalDetailsForm, RentalDetails> {

        public final IntegerField height;
        public final IntegerField weight;

        public RentalDetailsForm() {
            super(new RentalDetails(165, 40));
            height = new IntegerField(i18n.bodyHeight());
            height.setValue(165);
            height.setMin(120);
            height.setMax(230);

            weight = new IntegerField(i18n.bodyWeight());
            weight.setValue(40);
            weight.setMin(30);
            weight.setMax(300);

            height.addValueChangeListener(event ->
                setModelValue(new RentalDetails(event.getValue(), weight.getValue()), true)
            );

            weight.addValueChangeListener(event ->
                setModelValue(new RentalDetails(height.getValue(), event.getValue()), true)
            );

            this.getContent().setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("650px", 2)
            );
            this.getContent().add(height, weight);
        }

        public void setEnabled(boolean enabled) {
            this.height.setEnabled(enabled);
            this.weight.setEnabled(enabled);
        }

        @Override
        public void setValue(RentalDetails value) {
            super.setValue(value);
            this.setPresentationValue(value);
        }

        @Override
        protected void setPresentationValue(RentalDetails newPresentationValue) {
            this.height.setValue(newPresentationValue.height);
            this.weight.setValue(newPresentationValue.weight);
        }

    }

    private class BootRentalRadioButtons extends RadioButtonGroup<Boolean> {

        public BootRentalRadioButtons() {
            this.setLabel(i18n.skiBootRental());
            this.setItems(Boolean.FALSE, Boolean.TRUE);
            this.setItemLabelGenerator(rent -> {
                if (rent) {
                    return i18n.studentWantsToRentSkiBoots();
                } else {
                    return i18n.studentsWantsToUseOwnSkiBoots();
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
                        .then(i18n.studentWantsToRentSkiBoots())
                        .otherwise(i18n.studentsWantsToUseOwnSkiBoots())
                );
            } else {
                this.setItemLabelGenerator(exp ->
                    When
                        .isTrue(exp)
                        .then(i18n.studentWantsToRentSnowboardBoots())
                        .otherwise(i18n.studentsWantsToUseOwnSnowboardBoots())
                );
            }
        }
    }

    private class HelmetRentalForm extends AbstractCompositeField<FormLayout, HelmetRentalForm, Boolean> {

        private final Checkbox checkbox;

        public HelmetRentalForm() {
            super(false);

            var p = new Paragraph(i18n.helmetRentalInfo());

            checkbox = new Checkbox(i18n.studentWantsToRentHelmet());
            checkbox.addValueChangeListener(event -> setModelValue(event.getValue(), true));

            this.getContent().setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("650px", 2)
            );
            this.getContent().add(p, checkbox);
            this.getContent().setColspan(p, 2);
        }

        @Override
        public void setValue(Boolean value) {
            super.setValue(value);
            this.setPresentationValue(value);
        }

        @Override
        protected void setPresentationValue(Boolean newPresentationValue) {
            this.checkbox.setValue(newPresentationValue);
        }

    }

    private class BootRentalDetailsForm extends AbstractCompositeField<FormLayout, BootRentalDetailsForm,
        BootRentalDetails> {

        public final IntegerField size;

        public BootRentalDetailsForm() {
            super(new BootRentalDetails(35));
            size = new IntegerField(i18n.shoeSize());
            size.setValue(35);
            size.setMax(20);
            size.setMax(60);

            size.addValueChangeListener(event -> setModelValue(new BootRentalDetails(event.getValue()), true));

            this.getContent().setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("650px", 2)
            );
            this.getContent().add(size);
        }

        public void setEnabled(boolean enabled) {
            this.size.setEnabled(enabled);
        }

        @Override
        public void setValue(BootRentalDetails value) {
            super.setValue(value);
            this.setPresentationValue(value);
        }

        @Override
        protected void setPresentationValue(BootRentalDetails newPresentationValue) {
            this.size.setValue(newPresentationValue.size);
        }

    }

}
