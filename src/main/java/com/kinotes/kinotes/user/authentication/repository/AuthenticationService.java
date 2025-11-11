package com.kinotes.kinotes.user.authentication.repository;

public interface AuthenticationService {

    void login(String username, String password);

    void logout();

    void register(String username, String password);
}
