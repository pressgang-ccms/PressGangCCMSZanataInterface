package org.jboss.pressgang.ccms.zanata;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.ClientExecutionContext;
import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.util.WeightedMediaType;

@SuppressWarnings("unchecked")
public class ETagInterceptor implements ClientExecutionInterceptor, AcceptedByMethod {
    private final ETagCache cache;
    private final List<Class<?>> ignoredResources;

    public ETagInterceptor(final ETagCache cache) {
        this.cache = cache;
        ignoredResources = new ArrayList<Class<?>>();
    }

    public ETagInterceptor(final ETagCache cache, final List<Class<?>> ignoredResources) {
        this.cache = cache;
        this.ignoredResources = ignoredResources;
    }

    public boolean accept(final Class declaring, final Method method) {
        if (declaring == null || method == null) return true;
        if (method.isAnnotationPresent(GET.class)) {
            return !ignoredResources.contains(declaring);
        } else {
            return false;
        }
    }

    @Override
    public ClientResponse execute(final ClientExecutionContext ctx) throws Exception {
        final ClientRequest request = ctx.getRequest();

        // ETags can only be used on GET requests, so if it's anything else proceed normally
        if (!request.getHttpMethod().equals(HttpMethod.GET)) {
            return ctx.proceed();
        }

        // Load the etag for the request from the cache
        final String etag = getETag(request);
        if (etag != null) {
            request.header(HttpHeaders.IF_NONE_MATCH, etag);
        }

        // Get the response and save the etag value
        final ClientResponse response = ctx.proceed();
        cacheIfPossible(request, response);
        return response;
    }

    protected String getETag(final ClientRequest request) throws Exception {
        final String uri = request.getUri();
        String etag;

        // Different Media Types may have different ETags
        final String acceptHeader = request.getHeaders().getFirst(HttpHeaders.ACCEPT);
        if (acceptHeader != null) {
            final List<WeightedMediaType> waccepts = new ArrayList<WeightedMediaType>();
            final String[] split = acceptHeader.split(",");
            for (final String accept : split) {
                waccepts.add(WeightedMediaType.valueOf(accept));
            }

            // Sort the media types
            Collections.sort(waccepts);

            // Iterate through the list and see if an etag exists
            for (final MediaType accept : waccepts) {
                etag = cache.get(uri, accept.toString());
                if (etag != null) return etag;
            }
        } else {
            return cache.getAny(uri);
        }

        return null;
    }

    protected void cacheIfPossible(final ClientRequest request, final ClientResponse<?> response) throws Exception {
        if (response.getStatus() == 200) {
            final String uri = request.getUri();
            final String etag = (String) response.getHeaders().getFirst(HttpHeaders.ETAG);
            final String contentType = (String) response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);

            if (etag != null && !etag.trim().isEmpty()) {
                cache.put(uri, etag, contentType);
            }
        }
    }
}
