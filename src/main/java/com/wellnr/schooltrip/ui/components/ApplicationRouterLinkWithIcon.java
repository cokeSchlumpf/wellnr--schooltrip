package com.wellnr.schooltrip.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class ApplicationRouterLinkWithIcon extends RouterLink {

    public ApplicationRouterLinkWithIcon(Icon icon, String text, Class<? extends Component> navigationTarget,
                                         RouteParameters paramters) {
        super(text, navigationTarget, paramters);

        this.removeAll();
        this.add(icon);
        this.add(new Text(text));

        this.setHighlightCondition(HighlightConditions.locationPrefix());
    }

    public ApplicationRouterLinkWithIcon(Icon icon, String text, Class<? extends Component> navigationTarget) {
        this(icon, text, navigationTarget, RouteParameters.empty());
    }

    public ApplicationRouterLinkWithIcon(VaadinIcon icon, String text, Class<? extends Component> navigationTarget,
                                         RouteParameters paramters) {
        this(new Icon(icon), text, navigationTarget, paramters);
    }

    public ApplicationRouterLinkWithIcon(VaadinIcon icon, String text, Class<? extends Component> navigationTarget) {
        this(icon, text, navigationTarget, RouteParameters.empty());
    }

    public void setDivider(boolean value) {
        if (value) {
            this.addClassNames(LumoUtility.Margin.Bottom.LARGE);
        } else {
            this.removeClassName(LumoUtility.Margin.Bottom.LARGE);
        }
    }

}
