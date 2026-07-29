package com.example.cinema.api.shared.fixtures;

import org.springframework.security.test.context.support.WithSecurityContext;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockAuthenticatedUserSecurityContextFactory.class)
public @interface WithMockAuthenticatedUser {
    String username() default "admin@example.com";
    String[] roles() default {"SUPER_ADMIN"};
    long cinemaId() default -1L;
}