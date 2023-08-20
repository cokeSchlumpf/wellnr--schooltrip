package com.wellnr.schooltrip.ui.components;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class ApplicationNotifications {

    private ApplicationNotifications() {

    }

    public static void success(String message) {
        var notification = Notification.show(message);
        notification.setPosition(Notification.Position.BOTTOM_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    public static void error(String message) {
        var notification = Notification.show(message);
        notification.setPosition(Notification.Position.BOTTOM_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

}
