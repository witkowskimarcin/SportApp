package com.example.sportapp.service;

import com.google.firebase.auth.FirebaseUser;

public class AuthenticationService {
    private FirebaseUser user = null;

    private static AuthenticationService instance;

    public static AuthenticationService getInstance() {
        if (instance == null)
            instance = new AuthenticationService();
        return instance;
    }

    private AuthenticationService() { }

    public FirebaseUser getUser() {
        return user;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public static void setInstance(AuthenticationService instance) {
        AuthenticationService.instance = instance;
    }

    public boolean isAuthenticated(){
        return user != null;
    }
}
