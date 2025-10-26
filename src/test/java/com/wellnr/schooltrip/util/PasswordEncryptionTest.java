package com.wellnr.schooltrip.util;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncryptionTest {

    @Test
    public void encryptPassword() {
        var encoder = new BCryptPasswordEncoder();
        var encrypted = encoder.encode("vfcplauen4ever");
        System.out.println(encrypted);
    }

}
