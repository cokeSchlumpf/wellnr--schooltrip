package com.wellnr.schooltrip.infrastructure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class ApplicationAuthenticationController {

    private final ApplicationUserSession userSession;

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
