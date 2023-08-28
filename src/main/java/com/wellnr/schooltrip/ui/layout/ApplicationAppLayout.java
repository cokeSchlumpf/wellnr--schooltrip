package com.wellnr.schooltrip.ui.layout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.stream.Collectors;

public class ApplicationAppLayout extends AppLayout {

    private final VerticalLayout mainmenuContainer = new VerticalLayout();

    public ApplicationAppLayout() {
        this.addNavBarContent();
        this.addDrawerContent();

        this.setDrawerOpened(true);
    }

    @Override
    public void setContent(Component content) {
        super.setContent(content);

        if (content instanceof ApplicationAppView view) {
            this.mainmenuContainer.removeAll();

            var li = new UnorderedList();
            li.addClassNames("app--app-layout--mainmenu");
            li.add(
                view
                    .getMainMenuComponents()
                    .stream()
                    .map(ListItem::new)
                    .collect(Collectors.toList())
            );

            this.mainmenuContainer.add(li);
            ;
        }
    }

    private void addDrawerContent() {
        this.mainmenuContainer.setPadding(false);

        var content = new VerticalLayout();
        content.setPadding(false);
        content.setMinHeight(100, Unit.PERCENTAGE);
        content.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        var releaseInfo = new Div();
        releaseInfo.getElement().setProperty(
            "innerHTML",
            """
            Release 1.2.3<br />
            &copy; Michael Wellner 2023
            """);
        releaseInfo.addClassNames(
            LumoUtility.Margin.Left.MEDIUM,
            LumoUtility.Margin.Right.MEDIUM,
            LumoUtility.FontSize.SMALL,
            LumoUtility.TextColor.TERTIARY
        );

        content.add(this.mainmenuContainer, releaseInfo);
        addToDrawer(content);
    }

    private void addNavBarContent() {
        /*
         * Drawer
         */
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        /*
         * User Menu
         */
        var areaTitle = new Brand("GaS Merzig", "Ski-Lehrfahrt");
        var userMenu = new UserMenu();
        var components = new NavbarComponents(areaTitle, userMenu);


        addToNavbar(true, toggle, components);
    }

    private static class Brand extends Span {

        public Brand(String company, String product) {
            super(company);

            var span = new Span(product);
            span.addClassNames(
                "app--app-layout--brand--product"
            );

            this.add(span);
            this.addClassNames(
                "app--app-layout--brand",
                LumoUtility.Margin.NONE
            );
        }

    }

    private static class UserMenu extends HorizontalLayout {

        public UserMenu() {
            var menubar = new MenuBar();

            menubar.setOpenOnHover(true);

            var item = menubar.addItem(new Icon(VaadinIcon.USER));
            item.add(new Text("Michael Wellner"));

            item.getSubMenu().addItem("Settings");
            item.getSubMenu().add(new Hr());
            item.getSubMenu().addItem("Logout");
            this.add(menubar);
        }

    }

    private static class NavbarComponents extends HorizontalLayout {

        public NavbarComponents(Component... components) {
            super(components);
            this.setSizeFull();
            this.setAlignItems(FlexComponent.Alignment.CENTER);
            this.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

            this.addClassNames("app--app-layout--navbar");
        }

    }

}