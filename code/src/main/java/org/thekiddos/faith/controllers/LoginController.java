package org.thekiddos.faith.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.thekiddos.faith.dtos.PasswordConfirmDto;
import org.thekiddos.faith.exceptions.PasswordResetTokenNotFoundException;
import org.thekiddos.faith.repositories.PasswordResetTokenRepository;
import org.thekiddos.faith.services.UserService;

import javax.validation.Valid;

@Slf4j
@Controller
public class LoginController {
    private final UserService userService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public LoginController( UserService userService, PasswordResetTokenRepository passwordResetTokenRepository ) {
        this.userService = userService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @GetMapping( value = "/login" )
    String get() {
        return "accounts/login";
    }

    @GetMapping( value = "/forgot-password" )
    String getForgotPasswordPage() {
        return "accounts/forgot-password";
    }

    @PostMapping( value = "/forgot-password" )
    String generateToken( String email ) {
        userService.createForgotPasswordToken( email );
        return "accounts/forgot-password";
    }

    @GetMapping( value = "/reset-password/{token}" )
    String getResetPasswordPage( @PathVariable String token, Model model ) {
        passwordResetTokenRepository.findByToken( token ).orElseThrow( PasswordResetTokenNotFoundException::new );

        model.addAttribute( "passwordObject", new PasswordConfirmDto() );
        model.addAttribute( "token", token );
        return "accounts/reset-password";
    }

    @PostMapping( value = "/reset-password/{token}" )
    String resetUserPassword( @PathVariable String token, @Valid PasswordConfirmDto passwordConfirmDto, BindingResult binding, Model model ) {
        passwordResetTokenRepository.findByToken( token ).orElseThrow( PasswordResetTokenNotFoundException::new );

        if ( binding.hasErrors() ) {
            model.addAttribute( "passwordObject", passwordConfirmDto );
            model.addAttribute( "token", token );
            return "accounts/reset-password";
        }

        userService.resetUserPassword( token, passwordConfirmDto.getPassword() );
        return "redirect:/login";
    }
}
