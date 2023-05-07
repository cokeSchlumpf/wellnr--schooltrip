package com.wellnr.schooltrip.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import com.wellnr.schooltrip.views.layout.SchoolTripAppLayout;
import com.wellnr.schooltrip.views.layout.SchoolTripAppView;

@Route(value = "profile", layout = SchoolTripAppLayout.class)
public class ProfileView extends Div implements SchoolTripAppView {

    public ProfileView() {
        this.add(new Span("Profile View"));
    }

    @Override
    public String getSectionTitle() {
        return "Profile";
    }
}
