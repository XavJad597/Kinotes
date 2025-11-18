package com.kinotes.kinotes.authentication.service;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomPasswordEncoder implements PasswordEncoder {

    private final Argon2 argon2;

    private final int iterations = 10;
    private final int memory = 65536;
    private final int parallelism = 1;

    public CustomPasswordEncoder() {
        this.argon2 = Argon2Factory.create(
                Argon2Factory.Argon2Types.ARGON2id,
                16,  // Salt length
                32   // Hash length
        );
    }


    @Override
    public String encode(CharSequence rawPassword) {
        return argon2.hash(iterations, memory, parallelism, rawPassword.toString().toCharArray());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return argon2.verify(encodedPassword, rawPassword.toString().toCharArray());
    }
}
