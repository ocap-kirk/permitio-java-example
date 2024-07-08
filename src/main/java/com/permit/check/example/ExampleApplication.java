package com.permit.check.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;
import io.permit.sdk.Permit;
import io.permit.sdk.PermitConfig;
import io.permit.sdk.api.PermitApiError;
import io.permit.sdk.api.PermitContextError;
import io.permit.sdk.enforcement.Resource;
import io.permit.sdk.enforcement.User;
import io.permit.sdk.openapi.models.UserCreate;
import io.permit.sdk.openapi.models.UserRead;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@SpringBootApplication
public class ExampleApplication {

    final Permit permit;
    // final UserRead user;

    public ExampleApplication() {
        // init the permit SDK
        this.permit = new Permit(
            new PermitConfig.Builder("permit_key_vresSm6t2q8BCW3xDHmTUa7dJO7OXsIGxNBDtUAeQkoM1hvMtkOCv6U449phGEJ11mW1Fn1IgXmCAR26EAM62p")
			.withPdpAddress("http://localhost:7766")
			// optionally, if you wish to get more debug messages to your log, set this to true
			.withDebugMode(true)
			.build()
        );
		

        // try {
        //     // typically you would sync a user to the permission system
        //     // and assign an initial role when the user signs up to the system
        //     this.user = permit.api.users.sync(
        //         // the user "key" is any id that identifies the user uniquely
        //         // but is typically taken straight from the user JWT `sub` claim
        //         new UserCreate("[A_USER_ID]")
        //             .withEmail("user@example.com")
        //             .withFirstName("Joe")
        //             .withLastName("Doe")
        //     ).getResult();

        //     // assign the `admin` role to the user in the `default` tenant
        //     permit.api.users.assignRole(user.key, "admin", "default");
        // } catch (IOException | PermitApiError | PermitContextError e) {
        //     throw new RuntimeException(e);
        // }
    }

    @GetMapping("/API/{num}")
    ResponseEntity<String> home(@PathVariable("num") String num, @RequestParam("user") String quser) throws IOException, PermitApiError, PermitContextError {
        // is `user` allowed to do `action` on `resource`?
        User user = User.fromString(quser); // pass the user key to init a user from string
        String action = "GET";
        Resource resource = new Resource.Builder(String.format("/API/%s", num))
            .withTenant("default")
            .build();

        // to run a permission check, use permit.check()
        boolean permitted = permit.check(user, action, resource);

        if (permitted) {
            return ResponseEntity.status(HttpStatus.OK).body(String.format("%s is PERMITTED to create document!", quser)
                
            );
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(String.format("%s is NOT PERMITTED to create document!", quser)
                
            );
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}
