package com.wellnr.schooltrip.infrastructure;

import com.wellnr.common.Operators;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.schooltrip.ExportInviteMailingLetterData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;

@RestController
@AllArgsConstructor
public class ApplicationRESTAPIController {

    private final ApplicationUserSession userSession;

    private final SchoolTripDomainRegistry domainRegistry;

    @GetMapping("/api/trips/{name}/exports/invitation-mailing")
    public ResponseEntity<InputStreamResource> exportSchoolTripInivitationMailings(
        @PathVariable("name") String schoolTrip) {

        var path = ExportInviteMailingLetterData
            .apply(schoolTrip)
            .run(userSession.getUser(), domainRegistry)
            .getData();

        var contentDisposition = ContentDisposition
            .builder("inline")
            .filename(schoolTrip + "--invitation-mailings.zip")
            .build();

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .headers(headers -> {
                headers.setContentDisposition(contentDisposition);
            })
            .body(new InputStreamResource(
                Operators.suppressExceptions(() -> new FileInputStream(path.toFile()))
            ));
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return userSession.login(request.getUsername(), request.getPassword());
    }

    @GetMapping("/test")
    public String test() {
        return userSession.getUser().toString();
    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
    public static class LoginRequest {

        String username;

        String password;

    }

}
