package org.jboss.pressgang.ccms.zanata;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.jboss.pressgang.ccms.utils.common.VersionUtilities;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.cache.BrowserCache;
import org.jboss.resteasy.client.cache.CacheInterceptor;
import org.jboss.resteasy.spi.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.common.LocaleId;
import org.zanata.rest.client.ISourceDocResource;
import org.zanata.rest.client.ITranslatedDocResource;
import org.zanata.rest.dto.CopyTransStatus;
import org.zanata.rest.dto.VersionInfo;
import org.zanata.rest.dto.resource.Resource;
import org.zanata.rest.dto.resource.ResourceMeta;
import org.zanata.rest.dto.resource.TranslationsResource;
import org.zanata.rest.service.CopyTransResource;

public class ZanataInterface {
    private static Logger log = LoggerFactory.getLogger(ZanataInterface.class);
    private final static ZanataDetails DEFAULT_DETAILS = new ZanataDetails();

    private final ZanataDetails details;
    private final ZanataProxyFactory proxyFactory;
    private final ZanataLocaleManager localeManager;
    private final Long minZanataRESTCallInterval;

    /**
     * Constructs the interface.
     */
    public ZanataInterface() {
        this(0, DEFAULT_DETAILS.getProject());
    }

    /**
     * Constructs the interface.
     *
     * @param minZanataRESTCallInterval The minimum amount of time that should be waited in between calls to Zanata. This value
     *                                  is specified in seconds.
     */
    public ZanataInterface(final double minZanataRESTCallInterval) {
        this(minZanataRESTCallInterval, DEFAULT_DETAILS.getProject());
    }

    /**
     * Constructs the interface.
     *
     * @param minZanataRESTCallInterval The minimum amount of time that should be waited in between calls to Zanata. This value
     *                                  is specified in seconds.
     * @param cache
     */
    public ZanataInterface(final double minZanataRESTCallInterval, final BrowserCache cache) {
        this(minZanataRESTCallInterval, (String) null, cache);
    }

    /**
     * Constructs the interface.
     *
     * @param minZanataRESTCallInterval The minimum amount of time that should be waited in between calls to Zanata. This value
     *                                  is specified in seconds.
     * @param disableSSLCert            Whether or not the SSL Certificate check should be performed.
     */
    public ZanataInterface(final double minZanataRESTCallInterval, final boolean disableSSLCert) {
        this(minZanataRESTCallInterval, new ZanataDetails(DEFAULT_DETAILS), disableSSLCert);
    }

    /**
     * Constructs the interface with a custom project
     *
     * @param minZanataRESTCallInterval The minimum amount of time that should be waited in between calls to Zanata. This value
     *                                  is specified in seconds.
     * @param zanataDetails             The zanata details to be used for this interface.
     * @param disableSSLCert            Whether or not the SSL Certificate check should be performed.
     */
    public ZanataInterface(final double minZanataRESTCallInterval, final ZanataDetails zanataDetails, final boolean disableSSLCert) {
        this(minZanataRESTCallInterval, zanataDetails, null, disableSSLCert);
    }


    /**
     * Constructs the interface with a custom project
     *
     * @param projectOverride The name of the Zanata project to work with, which will override the default specified.
     */
    public ZanataInterface(final String projectOverride) {
        this(0, projectOverride);
    }

    /**
     * Constructs the interface with a custom project
     *
     * @param minZanataRESTCallInterval The minimum amount of time that should be waited in between calls to Zanata. This value
     *                                  is specified in seconds.
     * @param projectOverride           The name of the Zanata project to work with, which will override the default specified.
     */
    public ZanataInterface(final double minZanataRESTCallInterval, final String projectOverride) {
        this(minZanataRESTCallInterval, new ZanataDetails(DEFAULT_DETAILS), projectOverride);
    }

    /**
     * Constructs the interface with a custom project
     *
     * @param minZanataRESTCallInterval The minimum amount of time that should be waited in between calls to Zanata. This value
     *                                  is specified in seconds.
     * @param projectOverride           The name of the Zanata project to work with, which will override the default specified.
     */
    public ZanataInterface(final double minZanataRESTCallInterval, final String projectOverride, final BrowserCache cache) {
        this(minZanataRESTCallInterval, new ZanataDetails(DEFAULT_DETAILS), projectOverride, cache);
    }

    /**
     * Constructs the interface with a custom project
     *
     * @param minZanataRESTCallInterval The minimum amount of time that should be waited in between calls to Zanata. This value
     *                                  is specified in seconds.
     * @param zanataDetails             The zanata details to be used for this interface.
     */
    public ZanataInterface(final double minZanataRESTCallInterval, final ZanataDetails zanataDetails) {
        this(minZanataRESTCallInterval, zanataDetails, (String) null);
    }

    /**
     * Constructs the interface with a custom project
     *
     * @param minZanataRESTCallInterval The minimum amount of time that should be waited in between calls to Zanata. This value
     *                                  is specified in seconds.
     * @param zanataDetails             The zanata details to be used for this interface.
     */
    public ZanataInterface(final double minZanataRESTCallInterval, final ZanataDetails zanataDetails, final BrowserCache cache) {
        this(minZanataRESTCallInterval, zanataDetails, null, cache);
    }

    /**
     * Constructs the interface with a custom project
     *
     * @param minZanataRESTCallInterval The minimum amount of time that should be waited in between calls to Zanata. This value
     *                                  is specified in seconds.
     * @param zanataDetails             The zanata details to be used for this interface.
     * @param projectOverride           The name of the Zanata project to work with, which will override the default specified.
     */
    protected ZanataInterface(final double minZanataRESTCallInterval, final ZanataDetails zanataDetails, final String projectOverride) {
        this(minZanataRESTCallInterval, zanataDetails, projectOverride, false);
    }

    /**
     * Constructs the interface with a custom project
     *
     * @param minZanataRESTCallInterval The minimum amount of time that should be waited in between calls to Zanata. This value
     *                                  is specified in seconds.
     * @param zanataDetails             The zanata details to be used for this interface.
     * @param projectOverride           The name of the Zanata project to work with, which will override the default specified.
     */
    protected ZanataInterface(final double minZanataRESTCallInterval, final ZanataDetails zanataDetails, final String projectOverride,
            final BrowserCache browserCache) {
        this(minZanataRESTCallInterval, zanataDetails, projectOverride, false, browserCache);
    }

    /**
     * Constructs the interface with a custom project
     *
     * @param minZanataRESTCallInterval The minimum amount of time that should be waited in between calls to Zanata. This value
     *                                  is specified in seconds.
     * @param zanataDetails             The zanata details to be used for this interface.
     * @param projectOverride           The name of the Zanata project to work with, which will override the default specified.
     * @param disableSSLCert            Whether or not the SSL Certificate check should be performed.
     */
    protected ZanataInterface(final double minZanataRESTCallInterval, final ZanataDetails zanataDetails, final String projectOverride,
            final boolean disableSSLCert) {
        this(minZanataRESTCallInterval, zanataDetails, projectOverride, disableSSLCert, null);
    }

    /**
     * Constructs the interface with a custom project
     *
     * @param minZanataRESTCallInterval The minimum amount of time that should be waited in between calls to Zanata. This value
     *                                  is specified in seconds.
     * @param zanataDetails             The zanata details to be used for this interface.
     * @param projectOverride           The name of the Zanata project to work with, which will override the default specified.
     * @param disableSSLCert            Whether or not the SSL Certificate check should be performed.
     */
    protected ZanataInterface(final double minZanataRESTCallInterval, final ZanataDetails zanataDetails, final String projectOverride,
            final boolean disableSSLCert, final BrowserCache cache) throws UnauthorizedException {
        details = zanataDetails;
        if (projectOverride != null) {
            details.setProject(projectOverride);
        }

        this.minZanataRESTCallInterval = (long) (minZanataRESTCallInterval * 1000);

        URI URI = null;
        try {
            URI = new URI(details.getServer());
        } catch (URISyntaxException e) {
        }

        // Get the Version Details from the Zanata Common API library. 
        final VersionInfo versionInfo = new VersionInfo();
        versionInfo.setVersionNo(VersionUtilities.getAPIVersion(LocaleId.class));
        versionInfo.setBuildTimeStamp(VersionUtilities.getAPIBuildTimestamp(LocaleId.class));

        // Deal with Zanatas painful username/password check
        try {
            proxyFactory = new ZanataProxyFactory(URI, details.getUsername(), details.getToken(), versionInfo, false, disableSSLCert);
        } catch (RuntimeException e) {
            if (e.getMessage().startsWith("Incorrect username/password")) {
                throw new UnauthorizedException(e.getMessage());
            } else {
                throw e;
            }
        }
        localeManager = ZanataLocaleManager.getInstance(details.getProject());

        if (cache != null) {
            proxyFactory.registerPrefixInterceptor(new CacheInterceptor(cache));
        }
    }

    /**
     * Get a specific Source Document from Zanata.
     *
     * @param id The ID of the Document in Zanata.
     * @return The Zanata Source Document that matches the id passed, or null if it doesn't exist.
     */
    public Resource getZanataResource(final String id) throws NotModifiedException {
        ClientResponse<Resource> response = null;
        try {
            final ISourceDocResource client = proxyFactory.getSourceDocResource(details.getProject(), details.getVersion());
            response = client.getResource(id, null);

            final Status status = Response.Status.fromStatusCode(response.getStatus());

            if (status == Response.Status.OK) {
                final Resource entity = response.getEntity();
                return entity;
            } else if (status == Status.NOT_MODIFIED) {
                throw new NotModifiedException();
            }
        } catch (final Exception ex) {
            if (ex instanceof NotModifiedException) {
                throw (NotModifiedException) ex;
            } else {
                log.error("Failed to retrieve the Zanata Source Document", ex);
            }
        } finally {
            /*
             * If you are using RESTEasy client framework, and returning a Response from your service method, you will
             * explicitly need to release the connection.
             */
            if (response != null) response.releaseConnection();
            
            /* Perform a small wait to ensure zanata isn't overloaded */
            performZanataRESTCallWaiting();
        }

        return null;
    }

    /**
     * Get all of the Document ID's available from Zanata for the configured project.
     *
     * @return A List of Resource Objects that contain information such as Document ID's.
     */
    public List<ResourceMeta> getZanataResources() {
        ClientResponse<List<ResourceMeta>> response = null;
        try {
            final ISourceDocResource client = proxyFactory.getSourceDocResource(details.getProject(), details.getVersion());
            response = client.get(null);

            final Status status = Response.Status.fromStatusCode(response.getStatus());

            if (status == Response.Status.OK) {
                final List<ResourceMeta> entities = response.getEntity();
                return entities;
            } else {
                log.error(
                        "REST call to get() did not complete successfully. HTTP response code was " + status.getStatusCode() + ". Reason " +
                                "was " + status.getReasonPhrase());
            }
        } catch (final Exception ex) {
            log.error("Failed to retrieve the list of Zanata Source Documents", ex);
        } finally {
            /*
             * If you are using RESTEasy client framework, and returning a Response from your service method, you will
             * explicitly need to release the connection.
             */
            if (response != null) response.releaseConnection();
            
            /* Perform a small wait to ensure zanata isn't overloaded */
            performZanataRESTCallWaiting();
        }

        return null;
    }

    /**
     * Create a Document in Zanata and run CopyTrans.
     *
     * @param resource The resource data to be used by Zanata to create the Document.
     * @return True if the document was successfully created, otherwise false.
     */
    public boolean createFile(final Resource resource) throws UnauthorizedException {
        return createFile(resource, true);
    }

    /**
     * Create a Document in Zanata.
     *
     * @param resource  The resource data to be used by Zanata to create the Document.
     * @param copyTrans Run copytrans on the server
     * @return True if the document was successfully created, otherwise false.
     */
    public boolean createFile(final Resource resource, boolean copyTrans) throws UnauthorizedException {
        ClientResponse<String> response = null;
        try {
            final ISourceDocResource client = proxyFactory.getSourceDocResource(details.getProject(), details.getVersion());
            response = client.post(resource, null, false);

            final Status status = Response.Status.fromStatusCode(response.getStatus());

            if (status == Response.Status.CREATED) {
                final String entity = response.getEntity();
                if (entity.trim().length() != 0) log.info(entity);

                // Run copytrans if requested
                if (copyTrans) {
                    runCopyTrans(resource.getName(), true);
                }

                return true;
            } else if (status == Status.UNAUTHORIZED) {
                throw new UnauthorizedException();
            } else {
                log.error("REST call to createResource() did not complete successfully. HTTP response code was " + status.getStatusCode() +
                        ". Reason was " + status.getReasonPhrase());
            }

        } catch (final Exception ex) {
            log.error("Failed to create the Zanata Document", ex);
            if (ex instanceof UnauthorizedException) {
                throw (UnauthorizedException) ex;
            }
        } finally {
            /*
             * If you are using RESTEasy client framework, and returning a Response from your service method, you will
             * explicitly need to release the connection.
             */
            if (response != null) response.releaseConnection();

            /* Perform a small wait to ensure zanata isn't overloaded */
            performZanataRESTCallWaiting();
        }

        return false;
    }

    /**
     * Get a Translation from Zanata using the Zanata Document ID and Locale.
     *
     * @param id     The ID of the document in Zanata.
     * @param locale The locale of the translation to find.
     * @return null if the translation doesn't exist or an error occurred, otherwise the TranslationResource containing the
     *         Translation Strings (TextFlowTargets).
     * @throws NotModifiedException Thrown if the translation has not been modified since it was last retrieved.
     */
    public TranslationsResource getTranslations(final String id, final LocaleId locale) throws NotModifiedException {
        ClientResponse<TranslationsResource> response = null;
        try {
            final ITranslatedDocResource client = proxyFactory.getTranslatedDocResource(details.getProject(), details.getVersion());
            response = client.getTranslations(id, locale, null);

            final Status status = Response.Status.fromStatusCode(response.getStatus());

            /* Remove the locale if it is forbidden */
            if (status == Response.Status.FORBIDDEN) {
                localeManager.removeLocale(locale);
            } else if (status == Status.NOT_MODIFIED) {
                throw new NotModifiedException();
            } else if (status == Response.Status.OK) {
                final TranslationsResource retValue = response.getEntity();
                return retValue;
            }
        } catch (final Exception ex) {
            if (ex instanceof NotModifiedException) {
                throw (NotModifiedException) ex;
            } else {
                log.error("Failed to retrieve the Zanata Translated Document", ex);
            }
        } finally {
            /*
             * If you are using RESTEasy client framework, and returning a Response from your service method, you will
             * explicitly need to release the connection.
             */
            if (response != null) response.releaseConnection();
            
            /* Perform a small wait to ensure zanata isn't overloaded */
            performZanataRESTCallWaiting();
        }

        return null;
    }

    /**
     * Delete a Document from Zanata.
     * <p/>
     * Note: This method should be used with extreme care.
     *
     * @param id The ID of the document in Zanata to be deleted.
     * @return True if the document was successfully deleted, otherwise false.
     */
    public boolean deleteResource(final String id) {
        performZanataRESTCallWaiting();
        ClientResponse<String> response = null;
        try {
            final ISourceDocResource client = proxyFactory.getSourceDocResource(details.getProject(), details.getVersion());
            response = client.deleteResource(id);

            final Status status = Response.Status.fromStatusCode(response.getStatus());

            if (status == Response.Status.OK) {
                final String entity = response.getEntity();
                if (entity.trim().length() != 0) log.info(entity);
                return true;
            } else {
                log.error("REST call to deleteResource() did not complete successfully. HTTP response code was " + status.getStatusCode() +
                        ". Reason was " + status.getReasonPhrase());
            }
        } catch (final Exception ex) {
            log.error("Failed to delete the Zanata Source Document", ex);
        } finally {
            /*
             * If you are using RESTEasy client framework, and returning a Response from your service method, you will
             * explicitly need to release the connection.
             */
            if (response != null) response.releaseConnection();
        }

        return false;
    }

    /**
     * Delete the translations for a Document from Zanata.
     * <p/>
     * Note: This method should be used with extreme care.
     *
     * @param id     The ID of the document in Zanata to be deleted.
     * @param locale The locale of the translation to be deleted.
     * @return True if the document was successfully deleted, otherwise false.
     */
    public boolean deleteTranslation(final String id, final LocaleId locale) {
        performZanataRESTCallWaiting();
        ClientResponse<String> response = null;
        try {
            final ITranslatedDocResource client = proxyFactory.getTranslatedDocResource(details.getProject(), details.getVersion());
            response = client.deleteTranslations(id, locale);

            final Status status = Response.Status.fromStatusCode(response.getStatus());

            if (status == Response.Status.OK) {
                final String entity = response.getEntity();
                if (entity.trim().length() != 0) log.info(entity);
                return true;
            } else {
                log.error("REST call to deleteResource() did not complete successfully. HTTP response code was " + status.getStatusCode() +
                        ". Reason was " + status.getReasonPhrase());
            }
        } catch (final Exception ex) {
            log.error("Failed to delete the Zanata Translation", ex);
        } finally {
            /*
             * If you are using RESTEasy client framework, and returning a Response from your service method, you will
             * explicitly need to release the connection.
             */
            if (response != null) response.releaseConnection();
        }

        return false;
    }

    /**
     * Run copy trans against a Source Document in zanata and then wait for it to complete
     *
     * @param zanataId      The id of the document to run copytrans for.
     * @param waitForFinish Wait for copytrans to finish running.
     * @return True if copytrans was run successfully, otherwise false.
     */
    public boolean runCopyTrans(final String zanataId, boolean waitForFinish) {
        log.debug("Running Zanata CopyTrans for " + zanataId);

        try {
            final CopyTransResource copyTransResource = proxyFactory.getCopyTransResource();
            copyTransResource.startCopyTrans(details.getProject(), details.getVersion(), zanataId);
            performZanataRESTCallWaiting();

            if (waitForFinish) {
                while (!isCopyTransCompleteForSourceDocument(copyTransResource, zanataId)) {
                    // Sleep for 3/4 of a second
                    Thread.sleep(750);
                }
            }

            return true;
        } catch (Exception e) {
            log.error("Failed to run copyTrans for " + zanataId, e);
        } finally {
            performZanataRESTCallWaiting();
        }

        return false;
    }

    /**
     * Check if copy trans has finished processing a source document.
     *
     * @param zanataId The Source Document id.
     * @return True if the source document has finished processing otherwise false.
     */
    protected boolean isCopyTransCompleteForSourceDocument(final CopyTransResource copyTransResource, final String zanataId) {
        final CopyTransStatus status = copyTransResource.getCopyTransStatus(details.getProject(), details.getVersion(), zanataId);
        performZanataRESTCallWaiting();
        return !status.isInProgress();
    }

    /**
     * Get a list of locales that can be synced to Zanata.
     *
     * @return A List of LocaleId objects that can be used to syn with Zanata.
     */
    public List<LocaleId> getZanataLocales() {
        return localeManager.getLocales();
    }

    /**
     * Get the Manager that handles what locales can be synced against in Zanata.
     *
     * @return The ZanataLocaleManager object that manages the available locales.
     */
    public ZanataLocaleManager getLocaleManager() {
        return localeManager;
    }

    /**
     * Get the underlying ZanataProxyFactory that handles creating the proxies to the resources in Zanata.
     *
     * @return The ZanataProxyFactory object that creates the proxies.
     */
    public ZanataProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    /**
     * Sleep for a small amount of time to allow zanata to process other data between requests if the time between calls is less
     * than the wait interval specified.
     */
    private void performZanataRESTCallWaiting() {
        /* No need to wait when the call interval is nothing */
        if (minZanataRESTCallInterval <= 0) return;

        try {
            Thread.sleep(minZanataRESTCallInterval);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}