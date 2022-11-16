package br.joshaby.springboot2keycloak.http.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    private String username;

    private String password;

    private String email;

    private String firstname;

    private String lastname;
}
