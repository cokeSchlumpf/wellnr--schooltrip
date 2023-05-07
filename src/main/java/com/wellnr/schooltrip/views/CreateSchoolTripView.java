package com.wellnr.schooltrip.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.StatusChangeEvent;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.CreateSchoolTripCommand;
import com.wellnr.schooltrip.infrastructure.UserSession;
import com.wellnr.schooltrip.views.components.Container;
import com.wellnr.schooltrip.views.layout.SchoolTripAppLayout;
import com.wellnr.schooltrip.views.layout.SchoolTripAppView;

@Route(value = "trips/create", layout = SchoolTripAppLayout.class)
public class CreateSchoolTripView extends Container implements SchoolTripAppView {

    private final SchoolTripDomainRegistry domainRegistry;

    private final UserSession userSession;

    private final Binder<CreateSchoolTripCommand> binder;

    @SuppressWarnings("FieldCanBeLocal")
    private final TextField title;

    private final Button submit;

    public CreateSchoolTripView(SchoolTripDomainRegistry domainRegistry, UserSession userSession) {
        this.domainRegistry = domainRegistry;
        this.userSession = userSession;

        this.title = new TextField("Trip Name");
        this.title.setValueChangeMode(ValueChangeMode.LAZY);

        this.submit = new Button("Create");
        this.submit.setEnabled(false);
        this.submit.addClickShortcut(Key.ENTER);
        this.submit.addClickListener(this::onCreateClick);

        this.binder = new BeanValidationBinder<>(CreateSchoolTripCommand.class);
        this.binder.addStatusChangeListener(this::onStatusChange);
        this.binder.bindInstanceFields(this);
        this.binder.setBean(CreateSchoolTripCommand.apply(""));

        var form = new FormLayout();
        form.add(this.title);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        var layout = new VerticalLayout();
        layout.add(form);
        layout.add(this.submit);

        this.add(layout);
    }

    @Override
    public String getSectionTitle() {
        return "Create School Trip";
    }

    private void onCreateClick(ClickEvent<Button> event) {
        var result = this.binder
            .getBean()
            .run(
                userSession.getRegisteredUser().orElseGet(
                    () -> domainRegistry.getUsers().getOneByEmail("michael.wellner@gmail.com")
                ),
                domainRegistry
            );

        UI.getCurrent().navigate(
            SchoolTripView.class,
            new RouteParameters(
                new RouteParam("name", result.getData().getName())
            )
        );
    }

    private void onStatusChange(StatusChangeEvent event) {
        this.submit.setEnabled(binder.isValid());
    }

}
