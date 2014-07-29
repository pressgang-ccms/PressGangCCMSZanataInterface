/*
  Copyright 2011-2014 Red Hat

  This file is part of PressGang CCMS.

  PressGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PressGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PressGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

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
