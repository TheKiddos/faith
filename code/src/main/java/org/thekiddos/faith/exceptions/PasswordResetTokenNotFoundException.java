package org.thekiddos.faith.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No such Token")
public class PasswordResetTokenNotFoundException extends RuntimeException {

}