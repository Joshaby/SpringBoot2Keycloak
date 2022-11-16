package br.joshaby.springboot2keycloak.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/test")
public class TestResource {

    @GetMapping("/anonymous")
    public String getAnonymous() {
        return "Endpoint anonymous";
    }

    @RolesAllowed("user")
    @GetMapping("/user")
    public String getUser() {
        return "Endpoint user";
    }

    @RolesAllowed("admin")
    @GetMapping("/admin")
    public String getAdmin() {
        return "Endpoint admin";
    }

    @RolesAllowed({"admin", "user"})
    @GetMapping("/all-user")
    public String getAllUser() {
        return "Endpoint all users";
    }
}
