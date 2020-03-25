package com.testswitch_api.testswitchapi.controllers;


import com.testswitch_api.testswitchapi.models.LoginCredentials;
import com.testswitch_api.testswitchapi.models.LoginResponse;
import com.testswitch_api.testswitchapi.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {

    private LoginService loginService;
    private LoginCredentials masterCredentials;

    @Autowired
    private LoginController(LoginService loginService, LoginCredentials masterCredentials) {
        this.loginService = loginService;
        this.masterCredentials = masterCredentials;
    }

    @PostMapping(consumes = {"application/json"})
    public ResponseEntity authenticateLoginCredentials(@RequestBody LoginCredentials credentials) {
        LoginResponse loginResponse = new LoginResponse();
        if (credentials.getUsername().equals(masterCredentials.getUsername()) && credentials.getPassword().equals(masterCredentials.getPassword())) {
            loginResponse.setToken(loginService.generateToken());
            loginResponse.setLoggedIn(true);
        }
        return ResponseEntity.ok().body(loginResponse);
    }
}
