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
