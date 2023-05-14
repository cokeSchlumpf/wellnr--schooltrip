package com.wellnr.schooltrip.infrastructure;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.wellnr.common.helper.FakeMailSender;
import com.wellnr.ddd.BeanValidation;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.student.StudentsRepository;
import com.wellnr.schooltrip.core.model.user.RegisteredUsersRepository;
import com.wellnr.schooltrip.core.ports.PasswordEncryptionPort;
import com.wellnr.schooltrip.core.ports.SchoolTripMessages;
import com.wellnr.schooltrip.infrastructure.repositories.SchoolTripsMongoRepository;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.Method;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class SchoolTripApplicationConfiguration implements WebMvcConfigurer {

    @Bean
    public SchoolTripDomainRegistry getSchoolTripDomainRegistry(
        BeanValidation beanValidation,
        SchoolTripsMongoRepository schoolTrips,
        StudentsRepository students,
        RegisteredUsersRepository users,
        PasswordEncryptionPort passwordEncryptionPort,
        JavaMailSender mailSender
    ) {
        return SchoolTripDomainRegistry.apply(
            beanValidation,
            schoolTrips,
            students,
            users,
            passwordEncryptionPort,
            new SchoolTripMessages() { },
            mailSender
        );
    }

    @Bean
    public BeanValidation getBeanValidation() {
        return new BeanValidation() {
            @Override
            public void validateObject(Object object) {

            }

            @Override
            public void validateParameters(Object object, Method method, Object... args) {

            }
        };
    }

    @Bean
    public JavaMailSender getMailSender() {
        return new FakeMailSender();
    }

    @Bean
    public RSAKey rsaKey() throws NoSuchAlgorithmException {
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        var keyPair = generator.generateKeyPair();
        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
            .privateKey(keyPair.getPrivate())
            .keyID(UUID.randomUUID().toString())
            .build();
    }

    @Bean
    public JwtEncoder jwtEncoder(RSAKey key) {
        return new NimbusJwtEncoder(
            new ImmutableJWKSet<>(new JWKSet(key))
        );
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAKey key) throws JOSEException {
        return NimbusJwtDecoder.withPublicKey(key.toRSAPublicKey()).build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .build();
    }

    @Bean
    public PasswordEncryptionPort passwordEncryptionPort() {
        var encoder = new BCryptPasswordEncoder();

        return new PasswordEncryptionPort() {
            @Override
            public String encode(String password) {
                return encoder.encode(password);
            }

            @Override
            public boolean matches(String password, String encodedPassword) {
                return encoder.matches(password, encodedPassword);
            }
        };
    }

}
