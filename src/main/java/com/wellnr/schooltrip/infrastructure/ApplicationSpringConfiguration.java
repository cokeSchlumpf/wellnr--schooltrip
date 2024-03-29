package com.wellnr.schooltrip.infrastructure;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.wellnr.common.Operators;
import com.wellnr.common.helper.FakeMailSender;
import com.wellnr.ddd.BeanValidation;
import com.wellnr.schooltrip.SchooltripApplication;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.SchoolTripApplicationConfiguration;
import com.wellnr.schooltrip.core.model.stripe.StripePaymentsRepository;
import com.wellnr.schooltrip.core.model.student.StudentsRepository;
import com.wellnr.schooltrip.core.model.user.RegisteredUsersRepository;
import com.wellnr.schooltrip.core.ports.PasswordEncryptionPort;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import com.wellnr.schooltrip.infrastructure.repositories.SchoolTripsMongoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
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
import org.springframework.web.util.WebUtils;

import java.lang.reflect.Method;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;

@Slf4j
@Configuration
public class ApplicationSpringConfiguration implements WebMvcConfigurer {

    @Value("${app.jwt.rsa}")
    String rsaKey;

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
    public JavaMailSender getMailSender(SchoolTripApplicationConfiguration config) {
        var cfg = config.getEmail();

        if (cfg.getMode().equals("fake")) {
            return new FakeMailSender(config);
        } else {
            var mailSender = new JavaMailSenderImpl();
            mailSender.setHost(cfg.getHost());
            mailSender.setPort(cfg.getPort());

            mailSender.setUsername(cfg.getUsername());
            mailSender.setPassword(cfg.getPassword());

            var props = mailSender.getJavaMailProperties();
            props.putAll(cfg.getProperties());

            return mailSender;
        }
    }

    @Bean
    public SchoolTripDomainRegistry getSchoolTripDomainRegistry(
        SchoolTripApplicationConfiguration config,
        BeanValidation beanValidation,
        SchoolTripsMongoRepository schoolTrips,
        StudentsRepository students,
        StripePaymentsRepository payments,
        RegisteredUsersRepository users,
        PasswordEncryptionPort passwordEncryptionPort,
        JavaMailSender mailSender
    ) {
        return SchoolTripDomainRegistry.apply(
            config,
            beanValidation,
            schoolTrips,
            students,
            payments,
            users,
            passwordEncryptionPort,
            mailSender
        );
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAKey key) throws JOSEException {
        return NimbusJwtDecoder.withPublicKey(key.toRSAPublicKey()).build();
    }

    @Bean
    public JwtEncoder jwtEncoder(RSAKey key) {
        return new NimbusJwtEncoder(
            new ImmutableJWKSet<>(new JWKSet(key))
        );
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

    @Bean
    public RSAKey rsaKey() throws NoSuchAlgorithmException {
        rsaKey = rsaKey == null ? null : rsaKey.trim();

        if (rsaKey != null && rsaKey.length() > 0) {
            log.info("Use RSA-key from configuration.");
            return Operators.suppressExceptions(() -> RSAKey.parse(rsaKey));
        } else {
            log.info("Creating new RSA Key file.");

            var generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            var keyPair = generator.generateKeyPair();

            return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey(keyPair.getPrivate())
                .keyID("secret")
                .build();
        }
    }

    @Bean
    public SecurityFilterChain securityFilterChain(JwtDecoder jwtDecoder, HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .oauth2ResourceServer(c -> c.jwt().and().bearerTokenResolver(request -> {
                // Check if JWT token is sent within header ...
                var header = request.getHeader(HttpHeaders.AUTHORIZATION);

                if (Objects.nonNull(header)) {
                    return header.replace("Bearer ", "");
                }

                // If not check for cookie.
                var cookie = WebUtils.getCookie(request, SchooltripApplication.SECURITY_COOKIE_NAME);

                if (Objects.nonNull(cookie) && Objects.nonNull(cookie.getValue()) && cookie.getValue().length() > 0) {
                    var token = cookie.getValue();

                    try {
                        jwtDecoder.decode(token);
                        return token;
                    } catch (Exception e) {
                        return null;
                    }
                } else {
                    return null;
                }
            }))
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .build();
    }

}
