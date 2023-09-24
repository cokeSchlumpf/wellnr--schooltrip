package com.wellnr.schooltrip.util;

import com.nimbusds.jose.jwk.RSAKey;
import com.wellnr.common.Operators;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;



public class SomeTest {

    @Test
    public void test() {

        KeyPairGenerator generator = Operators.suppressExceptions(() -> KeyPairGenerator.getInstance("RSA"));
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();

        RSAKey key = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
            .privateKey(keyPair.getPrivate())
            .keyID("secret")
            .build();

        System.out.println(key.toJSONString());
    }

}
