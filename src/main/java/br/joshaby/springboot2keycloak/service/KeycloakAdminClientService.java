package br.joshaby.springboot2keycloak.service;

import br.joshaby.springboot2keycloak.config.KeycloakProvider;
import br.joshaby.springboot2keycloak.http.request.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.*;

@Service
@RequiredArgsConstructor
public class KeycloakAdminClientService {

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    public String clientID;

    private final KeycloakProvider keycloakProvider;

    public Response createKeycloakUser(CreateUserRequest user) {
        UsersResource usersResource = keycloakProvider.getInstance().realm(realm).users();

        CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());

        UserRepresentation keycloakUser = new UserRepresentation();

        keycloakUser.setUsername(user.getEmail());
        keycloakUser.setCredentials(Collections.singletonList(credentialRepresentation));
        keycloakUser.setFirstName(user.getFirstname());
        keycloakUser.setLastName(user.getLastname());
        keycloakUser.setEmail(user.getEmail());
        keycloakUser.setEnabled(true);
        keycloakUser.setEmailVerified(false);

        Response createdResponse =  usersResource.create(keycloakUser);

        String userId = CreatedResponseUtil.getCreatedId(createdResponse);

        UserResource userResource = usersResource.get(userId);

        ClientRepresentation facilifarmaClient = keycloakProvider.getInstance().realm(realm)
                .clients().findByClientId(clientID).get(0);

        RoleRepresentation userRole = keycloakProvider.getInstance().realm(realm).clients()
                .get(facilifarmaClient.getId()).roles().get("user").toRepresentation();

        userResource.roles().clientLevel(facilifarmaClient.getId()).add(Collections.singletonList(userRole));

        return createdResponse;
    }

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }
}
