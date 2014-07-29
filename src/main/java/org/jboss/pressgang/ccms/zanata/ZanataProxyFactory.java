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

import java.net.URI;

import org.zanata.rest.dto.VersionInfo;

/**
 * A class to override the functionality of the org.zanata.rest.client.ZanataProxyFactory and provide a method to get access to
 * the custom FixedTranslationResources endpoints.
 *
 * @author lnewson
 */
public class ZanataProxyFactory extends org.zanata.rest.client.ZanataProxyFactory {

    public ZanataProxyFactory(String username, String apiKey, VersionInfo clientApiVersion) {
        this(null, username, apiKey, clientApiVersion);
    }

    public ZanataProxyFactory(URI base, String username, String apiKey, VersionInfo clientApiVersion) {
        this(base, username, apiKey, clientApiVersion, false);
    }

    public ZanataProxyFactory(URI base, String username, String apiKey, VersionInfo clientApiVersion, boolean logHttp) {
        this(base, username, apiKey, clientApiVersion, logHttp, false);
    }

    public ZanataProxyFactory(URI base, String username, String apiKey, VersionInfo clientApiVersion, boolean logHttp,
            boolean disableSSLCert) {
        super(base, username, apiKey, clientApiVersion, logHttp, disableSSLCert);
    }
}
