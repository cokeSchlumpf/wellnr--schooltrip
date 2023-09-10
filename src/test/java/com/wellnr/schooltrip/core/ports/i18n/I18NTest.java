package com.wellnr.schooltrip.core.ports.i18n;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.*;

class I18NTest {

    @Test
    void createInstance() {
        var messages = I18N.createInstance(SchoolTripMessages.class, Locale.GERMANY);

        assertThat(messages).isNotNull();
        assertThat(messages.backButton()).isEqualTo("< Zurück");
    }
}