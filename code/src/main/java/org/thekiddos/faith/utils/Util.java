package org.thekiddos.faith.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class Util {
    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
}
