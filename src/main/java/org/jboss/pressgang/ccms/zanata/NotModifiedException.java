package org.jboss.pressgang.ccms.zanata;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.Failure;

public class NotModifiedException extends Failure {
    private static final long serialVersionUID = 8020643536650722595L;

    public NotModifiedException() {
        super(304);
    }
    public NotModifiedException(String s) {
        super(s, 304);
    }

    public NotModifiedException(String s, Response response) {
        super(s, response);
    }

    public NotModifiedException(String s, Throwable throwable, Response response) {
        super(s, throwable, response);
    }

    public NotModifiedException(String s, Throwable throwable) {
        super(s, throwable, 304);
    }

    public NotModifiedException(Throwable throwable) {
        super(throwable, 304);
    }

    public NotModifiedException(Throwable throwable, Response response) {
        super(throwable, response);
    }
}
