package com.example.account.controller.login;

import com.example.account.dto.login.LoginRequest;
import com.example.account.service.login.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/signin")
    public String signin(@RequestBody LoginRequest request) {
        return loginService.login(request);
    }

    @PostMapping("/signup")
    public String signup(@RequestBody LoginRequest request) {
        return loginService.localSignup(request);
    }
}
