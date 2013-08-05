package org.jboss.pressgang.ccms.zanata;

import java.net.URI;

import org.jboss.resteasy.client.ClientExecutor;
import org.zanata.rest.dto.VersionInfo;

/**
 * A class to override the functionality of the org.zanata.rest.client.ZanataProxyFactory and provide a method to get access to
 * the custom FixedTranslationResources endpoints.
 *
 * @author lnewson
 */
public class ZanataProxyFactory extends org.zanata.rest.client.ZanataProxyFactory {

    public ZanataProxyFactory(String username, String apiKey, VersionInfo clientApiVersion) {
        super(null, username, apiKey, clientApiVersion);
    }

    public ZanataProxyFactory(URI base, String username, String apiKey, VersionInfo clientApiVersion) {
        super(base, username, apiKey, null, clientApiVersion, false);
    }

    public ZanataProxyFactory(URI base, String username, String apiKey, VersionInfo clientApiVersion, boolean logHttp) {
        super(base, username, apiKey, null, clientApiVersion, logHttp);
    }

    public ZanataProxyFactory(URI base, String username, String apiKey, ClientExecutor executor, VersionInfo clientApiVersion,
            boolean logHttp) {
        super(base, username, apiKey, executor, clientApiVersion, logHttp);
    }

    public IFixedSourceDocResource getFixedSourceDocResources(String projectSlug, String versionSlug) {
        return createProxy(IFixedSourceDocResource.class, getFixedSourceDocResourcesURI(projectSlug, versionSlug));
    }

    public URI getFixedSourceDocResourcesURI(String projectSlug, String versionSlug) {
        return super.getResourceURI(projectSlug, versionSlug);
    }

    public IFixedCopyTransResource getFixedCopyTransResource() {
        return createProxy(IFixedCopyTransResource.class);
    }
}
