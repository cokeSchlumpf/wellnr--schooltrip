package com.wellnr.schooltrip.ui.components;

import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.wellnr.common.functions.Function1;

import java.util.HashMap;
import java.util.Map;

public class RouterTabs extends Tabs implements BeforeEnterObserver {

    private final Map<RouterLink, Tab> routerLinkTabMap = new HashMap<>();

    public void add(RouterLink routerLink, Function1<RouterLink, Tab> createTabComponent) {
        routerLink.setHighlightCondition(HighlightConditions.locationPrefix());
        routerLink.setHighlightAction(
            (link, shouldHighlight) -> {
                if (shouldHighlight) {
                    setSelectedTab(routerLinkTabMap.get(routerLink));
                }
            }
        );
        routerLinkTabMap.put(routerLink, new Tab(createTabComponent.get(routerLink)));
        add(routerLinkTabMap.get(routerLink));
    }

    public void add(RouterLink routerLink) {
        add(routerLink, Tab::new);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        setSelectedTab(null);
    }

}
