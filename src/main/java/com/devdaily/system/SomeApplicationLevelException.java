package com.devdaily.system;

/**
 * Created by vgoryachev on 14.04.2016.
 * Package: com.devdaily.system.
 */
public class SomeApplicationLevelException extends Exception {

    public SomeApplicationLevelException() {
        super();
    }

    public SomeApplicationLevelException(String message) {
        super(message);
    }

    public SomeApplicationLevelException(String message, Throwable cause) {
        super(message, cause);
    }

    public SomeApplicationLevelException(Throwable cause) {
        super(cause);
    }
}
