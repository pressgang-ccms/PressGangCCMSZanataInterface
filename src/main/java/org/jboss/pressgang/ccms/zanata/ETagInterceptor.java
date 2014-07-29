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
    private final List<Class<?>> allowedResources;

    public ETagInterceptor(final ETagCache cache) {
        this.cache = cache;
        allowedResources = null;
    }

    public ETagInterceptor(final ETagCache cache, final List<Class<?>> allowedResources) {
        this.cache = cache;
        this.allowedResources = allowedResources;
    }

    public boolean accept(final Class declaring, final Method method) {
        if (declaring == null || method == null) return true;
        if (method.isAnnotationPresent(GET.class)) {
            if (allowedResources == null) {
                return true;
            } else {
                return allowedResources.contains(declaring);
            }
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
