/*
  Copyright 2011-2014 Red Hat

  This file is part of PresGang CCMS.

  PresGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PresGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PresGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.zanata;

import java.io.Serializable;

/**
 * A utility class to pull out the Zanata details from the system properties
 */
public class ZanataDetails implements Serializable {
    private static final long serialVersionUID = -7973647940523261634L;
    private String server;
    private String project;
    private String version;
    private String username;
    private String token;

    public ZanataDetails() {
        this.server = System.getProperty(ZanataConstants.ZANATA_SERVER_PROPERTY);
        this.project = System.getProperty(ZanataConstants.ZANATA_PROJECT_PROPERTY);
        this.version = System.getProperty(ZanataConstants.ZANATA_PROJECT_VERSION_PROPERTY);
        this.username = System.getProperty(ZanataConstants.ZANATA_USERNAME_PROPERTY);
        this.token = System.getProperty(ZanataConstants.ZANATA_TOKEN_PROPERTY);
    }

    public ZanataDetails(final ZanataDetails zanataDetails) {
        this.server = zanataDetails.server;
        this.project = zanataDetails.project;
        this.version = zanataDetails.version;
        this.username = zanataDetails.username;
        this.token = zanataDetails.token;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String returnUrl() {
        if (server == null) {
            return "";
        } else {
            return server + (server.endsWith("/") ? "" : "/") + "seam/resource/restv1/projects/p/" + project + "/iterations/i/" + version + "/r";
        }
    }
}
