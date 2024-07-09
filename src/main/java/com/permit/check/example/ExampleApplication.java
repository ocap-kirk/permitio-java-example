package com.permit.check.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;
import java.util.HashMap;

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
		/*
         * Frodo/High Potential: {"sub":"frodo@middle-earth.com","name":"Frodo Baggins","subscription":"High Potential"}
         * Gimli/Sustain: {"sub":"gimli@middle-earth.com","name":"Gimli","subscription":"Sustain"}
         * Gandalf/Core: {"sub":"gandalf@middle-earth.com","name":"Gandalf the Grey","subscription":"Core"}
         * Aragorn/Flagship: {"sub":"aragorn@middle-earth.com","name":"Aragorn","subscription":"Flagship"}
         */
    }

    @GetMapping("/API/{num}")
    ResponseEntity<String> home(
        @PathVariable("num") String num, @RequestParam("user") String quser, @RequestParam(required = false) String segment) throws IOException, PermitApiError, PermitContextError {
        //Setup the user object
        User user;
        if(segment != null && !segment.trim().isEmpty()) { 
            HashMap<String, Object> userAttributes = new HashMap<>();
            userAttributes.put("segment", segment);

            user = new User.Builder(quser)
            .withEmail(quser)
            .withAttributes(userAttributes)
            .build();
            // System.out.println(user.getAttributes().toString());
        }
        else
            user = User.fromString(quser); // pass the user key to init a user from string
        String action = "GET";
        Resource resource = new Resource.Builder(String.format("api_%s", num))
            .withTenant("default")
            .build();            

        // to run a permission check, use permit.check()
        boolean permitted = permit.check(user, action, resource);

        if (permitted) {
            return ResponseEntity.status(HttpStatus.OK).body(String.format("`%s` IS PERMITTED to GET API %s!", quser, num)
                
            );
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(String.format("`%s` is NOT PERMITTED to GET API %s!", quser, num)
                
            );
        }
    }

    @GetMapping("/dashboard/{name}")
    ResponseEntity<String> dashboard(
        @PathVariable("name") String name, @RequestParam("user") String quser, @RequestParam(required = false) String action) throws IOException, PermitApiError, PermitContextError {
        //Setup the user object
        User user;
        user = User.fromString(quser); // pass the user key to init a user from string
        
        Resource resource = new Resource.Builder(String.format("Dashboard:%s", name))
            .withTenant("default")
            .build();            

        // to run a permission check, use permit.check()
        boolean permitted = permit.check(user, action, resource);

        if (permitted) {
            return ResponseEntity.status(HttpStatus.OK).body(String.format("`%s` IS PERMITTED to %s Dashboard %s!", quser, action,name)
                
            );
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(String.format("`%s` is NOT PERMITTED to %s Dashboard %s!", quser, action, name)
                
            );
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}
