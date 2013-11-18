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
