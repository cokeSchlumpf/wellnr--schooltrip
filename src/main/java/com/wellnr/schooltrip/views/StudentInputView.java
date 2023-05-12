package com.wellnr.schooltrip.views;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.wellnr.common.markup.When;
import com.wellnr.schooltrip.core.application.commands.GetStudentActionsForToken;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.questionaire.*;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.views.components.Container;
import com.wellnr.schooltrip.views.components.Markdown;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Route("/students/:token")
@PageTitle("School Trip")
public class StudentInputView extends Container implements BeforeEnterObserver {

    private static final double SKI_RENTAL_PRICE = 65.0;
    private static final double SKI_BOOTS_PRICE = 15.0;
    private static final double SNOWBOARD_PRICE = 70.0;
    private static final double SNOWBOARD_BOOTS_PRICE = 75.0;

    private final SchoolTripCommandRunner commandRunner;

    private DisciplinSection disciplinSection;
    private ExperienceSection experienceSection;
    private RentalSection rentalSection;
    private CostSection costSection;
    private NutritionSection nutritionSection;

    private Student student;

    public StudentInputView(SchoolTripCommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        var token = beforeEnterEvent
            .getRouteParameters()
            .get("token")
            .orElseThrow();

        student = commandRunner
            .run(
                GetStudentActionsForToken.apply(token)
            )
            .getData();

        /*
         * Initialize view.
         */
        disciplinSection = new DisciplinSection();
        experienceSection = new ExperienceSection();
        rentalSection = new RentalSection();
        costSection = new CostSection();
        nutritionSection = new NutritionSection();

        var layout = new VerticalLayout();
        layout.add(new H3("Anmeldung Ski-Kurs 2023 für " + student.getDisplayName()));
        layout.add(disciplinSection);
        layout.add(experienceSection);
        layout.add(rentalSection);
        layout.add(nutritionSection);
        layout.add(costSection);
        layout.add(new SubmitSection());

        this.add(layout);
    }

    private record PriceLineItem(String label, double amount) {

    }

    private static abstract class Section extends VerticalLayout {

        public void setDiscipline(Class<? extends Discipline> discipline) {

        }

        public List<PriceLineItem> getPriceLineItems() {
            return List.of();
        }

    }

    private class DisciplinSection extends Section {

        public final DisciplineRadioButtons disciplin;

        public DisciplinSection() {
            disciplin = new DisciplineRadioButtons();
            this.add(new Paragraph("Nutzen Sie das folgende Formular um " + student.getFirstName() + " für den Ski-Kurs 2023 anzumelden."));
            this.add(disciplin);

            disciplin.addValueChangeListener(event -> {
                experienceSection.setDiscipline(event.getValue());
                rentalSection.setDiscipline(event.getValue());
                costSection.update();
            });
        }

        @Override
        public List<PriceLineItem> getPriceLineItems() {
            return List.of(
                new PriceLineItem("An- und Abfahrt, Unterkunft und Verpflegung (Semi-Voll-Pension)", 442.0)
            );
        }
    }

    private class ExperienceSection extends Section {

        public final ExperienceRadioButtons experience;

        public ExperienceSection() {
            this.experience = new ExperienceRadioButtons();

            this.add(new H4("Erfahrungs-Level des Kindes"));
            this.add(new Paragraph("Bitte geben Sie an, wie erfahren " + student.getFirstName() + " in der gewählten Sportart ist. Lorem " +
                "Ipsum, erklären was die Level bedeuten."));
            this.add(experience);
        }

    }

    private class RentalSection extends Section {

        private final RentalRadioButtons rental;

        private final RentalDetailsForm rentalDetails;

        private final BootRentalRadioButtons bootRental;

        private final BootRentalDetailsForm bootRentalDetails;

        public RentalSection() {
            this.rental = new RentalRadioButtons();
            this.rentalDetails = new RentalDetailsForm();
            this.bootRental = new BootRentalRadioButtons();
            this.bootRentalDetails = new BootRentalDetailsForm();

            this.rentalDetails.setEnabled(false);
            this.bootRentalDetails.setEnabled(false);

            rental.addValueChangeListener(event -> {
                costSection.update();
                rentalDetails.setEnabled(event.getValue());
            });

            bootRental.addValueChangeListener(event -> {
                costSection.update();
                bootRentalDetails.setEnabled(event.getValue());
            });

            this.add(new H4("Material-Ausleihe"));
            this.add(new Paragraph("Lorem Ipsum dolor"));
            this.add(rental);
            this.add(rentalDetails);
            this.add(bootRental);
            this.add(bootRentalDetails);
        }

        @Override
        public void setDiscipline(Class<? extends Discipline> discipline) {
            rental.setDiscipline(discipline);
            bootRental.setDiscipline(discipline);
        }

        @Override
        public List<PriceLineItem> getPriceLineItems() {
            var items = new ArrayList<PriceLineItem>();

            if (disciplinSection.disciplin.getValue().equals(Ski.class)) {
                if (rental.getValue().equals(Boolean.TRUE)) {
                    items.add(new PriceLineItem("Ski-Ausleihe", SKI_RENTAL_PRICE));
                }

                if (bootRental.getValue().equals(Boolean.TRUE)) {
                    items.add(new PriceLineItem("Ski-Schuhe-Ausleihe", SKI_BOOTS_PRICE));
                }
            } else {
                if (rental.getValue().equals(Boolean.TRUE)) {
                    items.add(new PriceLineItem("Snowboard-Ausleihe", SNOWBOARD_PRICE));
                }

                if (rental.getValue().equals(Boolean.TRUE)) {
                    items.add(new PriceLineItem("Snowboard-Boots-Ausleihe", SNOWBOARD_BOOTS_PRICE));
                }
            }

            return List.copyOf(items);
        }
    }

    private class NutritionSection extends Section {

        public final NutritionCheckboxes nutrition;

        public final TextArea comments;

        public NutritionSection() {
            nutrition = new NutritionCheckboxes();

            comments = new TextArea("Möchten Sie uns noch etwas mitteilen?");
            comments.setWidthFull();

            this.add(new H4("Weitere Informationen"));
            this.add(new Paragraph("Lorem Ipsum dolor"));
            this.add(nutrition);
            this.add(comments);
        }

    }

    private class CostSection extends Section {

        private final Grid<PriceLineItem> grid;

        private final DecimalFormat format;

        public CostSection() {
            format = new DecimalFormat("#.##");
            grid = new Grid<>(PriceLineItem.class, false);
            grid.addColumn(PriceLineItem::label).setHeader("Position");
            grid
                .addColumn(item -> format.format(item.amount) + " €")
                .setHeader("Betrag")
                .setWidth("100px")
                .setTextAlign(ColumnTextAlign.END);

            grid.setAllRowsVisible(true);

            this.add(new H4("Kosten"));
            this.add(new Paragraph("Übersicht über entstehende Kosten."));
            this.add(grid);

            this.update();
        }

        public void update() {
            var lineItems = new ArrayList<PriceLineItem>();
            lineItems.addAll(disciplinSection.getPriceLineItems());
            lineItems.addAll(experienceSection.getPriceLineItems());
            lineItems.addAll(rentalSection.getPriceLineItems());

            var sum = lineItems
                .stream()
                .map(PriceLineItem::amount)
                .reduce(Double::sum)
                .map(d -> Double.valueOf(format.format(d)))
                .orElse(0d);

            lineItems.add(new PriceLineItem("Gesamt", sum));
            grid.setItems(lineItems);
        }

    }

    private static class SubmitSection extends Section {

        public SubmitSection() {
            var email = new EmailField("E-Mail");

            add(new H4("Bestätigung"));
            add(new Paragraph("Bitte verifizieren Sie die verbindliche Anmeldung. Die Angaben zu Sportart, " +
                "Ausleihe, Körpergröße und -gewicht können noch bis zum 13.12.2023 angepasst werden."));

            this.add(Markdown.apply("# Hello\n\nHello **World**!"));

            var form = new FormLayout();
            form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
            form.add(email);

            var submit = new Button("Absenden");
            submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            this.add(form);
            this.add(submit);
        }

    }

    private static class DisciplineRadioButtons extends RadioButtonGroup<Class<? extends Discipline>> {

        @SuppressWarnings("unchecked")
        public DisciplineRadioButtons() {
            this.setLabel("Meine Tochter xxx möchte am Skikurs 2024 teilnehmen als ...");
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
                case EXPERT -> "Profil";
            });
            this.setValue(Experience.BEGINNER);
            this.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        }

        public void setDiscipline(Class<? extends Discipline> discipline) {
            // Nothing to do.
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

    private static class NutritionCheckboxes extends CheckboxGroup<String> {

        private static String VEGETARIAN = "vegetarian";
        private static String HALAL = "halal";

        public NutritionCheckboxes() {
            this.setLabel("Essgewohnheiten");
            this.setItems(VEGETARIAN, HALAL);
            this.setItemLabelGenerator(s -> {
                if (s.equals(VEGETARIAN)) {
                    return "Vegetarisch";
                } else {
                    return "Halal";
                }
            });
            this.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        }

        public boolean isVegetarian() {
            return this.getValue().contains(VEGETARIAN);
        }

        public boolean isHalal() {
            return this.getValue().equals(HALAL);
        }

    }

    private static class RentalDetailsForm extends FormLayout {

        public final IntegerField height;
        public final IntegerField weight;

        public RentalDetailsForm() {
            height = new IntegerField("Körpergröße (in cm)");
            height.setValue(165);
            height.setMin(120);
            height.setMax(130);

            weight = new IntegerField("Körpergewicht (in KG)");
            weight.setValue(60);
            weight.setMin(30);
            weight.setMax(300);

            this.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
            this.add(height, weight);
        }

        public void setEnabled(boolean enabled) {
            this.height.setEnabled(enabled);
            this.weight.setEnabled(enabled);
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

    private static class BootRentalDetailsForm extends FormLayout {

        public final IntegerField size;

        public BootRentalDetailsForm() {
            size = new IntegerField("Schuhgröße");
            size.setValue(35);
            size.setMax(20);
            size.setMax(60);

            this.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
            this.add(size);
        }

        public void setEnabled(boolean enabled) {
            this.size.setEnabled(enabled);
        }

    }

}
