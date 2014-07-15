package org.simpleim.common.message;

public class LoginFailureResponse extends FailureResponse {

    private Cause cause;

    public Cause getCause() {
        return cause;
    }

    public LoginFailureResponse setCause(Cause cause) {
        this.cause = cause;
        return this;
    }

    public static enum Cause {
        ID_NOT_FOUND,
        PASSWORD_INCORRECT,
        IILEGAL_LOGIN
    }
}
