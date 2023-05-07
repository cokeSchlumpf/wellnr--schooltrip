package com.wellnr.schooltrip.core.ports;

public interface PasswordEncryptionPort {

    String encode(String password);

    boolean matches(String password, String encodedPassword);

}
