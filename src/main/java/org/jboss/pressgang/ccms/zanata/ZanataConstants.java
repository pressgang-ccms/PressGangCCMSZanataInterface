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

public final class ZanataConstants {
    /**
     * The system property that identifies the zanata server to send files to for translation
     */
    public static final String ZANATA_SERVER_PROPERTY = "pressgang.zanataServer";

    /**
     * The system property that identifies the zanata project name
     */
    public static final String ZANATA_PROJECT_PROPERTY = "pressgang.zanataProject";

    /**
     * The system property that identifies the zanata user name
     */
    public static final String ZANATA_USERNAME_PROPERTY = "pressgang.zanataUsername";

    /**
     * The system property that identifies the zanata project version
     */
    public static final String ZANATA_PROJECT_VERSION_PROPERTY = "pressgang.zanataProjectVersion";

    /**
     * The system property that identifies the zanata API token
     */
    public static final String ZANATA_TOKEN_PROPERTY = "pressgang.zanataToken";
    
    /**
     * The system property that identifies the time that should be waited in between each zanata call.
     */
    public static final String MIN_ZANATA_CALL_INTERNAL_PROPERTY = "pressgang.minZanataCallInterval";
}
