package br.joshaby.springboot2keycloak.resource;

import br.joshaby.springboot2keycloak.config.KeycloakProvider;
import br.joshaby.springboot2keycloak.http.request.CreateUserRequest;
import br.joshaby.springboot2keycloak.http.request.LoginRequest;
import br.joshaby.springboot2keycloak.service.KeycloakAdminClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserResource {

    private final KeycloakAdminClientService keycloakAdminClientService;

    private final KeycloakProvider keycloakProvider;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest user) {
        Response createdResponse = keycloakAdminClientService.createKeycloakUser(user);
        return ResponseEntity.status(createdResponse.getStatus()).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@NotNull @RequestBody LoginRequest login) {
        Keycloak keycloak = keycloakProvider.newKeycloakBuilderWithPasswordCredentials(
                login.getUsername(), login.getPassword()).build();
        AccessTokenResponse token = null;
        try {
            token = keycloak.tokenManager().getAccessToken();
            return ResponseEntity.status(HttpStatus.OK).body(token);
        } catch (BadRequestException ex) {
            log.warn("invalid account. User probably hasn't verified email.", ex);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(token);
        }
    }
}
