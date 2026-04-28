package io.noteshare.auth.exception;

public class AccountNotVerifiedException extends RuntimeException {
    public AccountNotVerifiedException() {
        super("Account not verified");
    }
}
