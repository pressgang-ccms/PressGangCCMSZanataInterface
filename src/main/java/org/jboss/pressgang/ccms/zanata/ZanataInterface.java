package org.jboss.pressgang.ccms.zanata;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.pressgang.ccms.utils.common.ExceptionUtilities;
import org.jboss.resteasy.client.ClientResponse;
import org.zanata.common.LocaleId;
import org.zanata.rest.client.ISourceDocResource;
import org.zanata.rest.client.ITranslatedDocResource;
import org.zanata.rest.dto.VersionInfo;
import org.zanata.rest.dto.resource.Resource;
import org.zanata.rest.dto.resource.ResourceMeta;
import org.zanata.rest.dto.resource.TranslationsResource;
import org.zanata.util.VersionUtility;

public class ZanataInterface {
    private final static ZanataDetails defaultDetails = new ZanataDetails();
    private final ZanataDetails details;
    private final ZanataProxyFactory proxyFactory;
    private final ZanataLocaleManager localeManager;

    public ZanataInterface() {
        this(defaultDetails.getProject());
    }

    /**
     * Constructs the interface with a custom project
     * 
     * @param projectOverride The name of the Zanata project to work with, which will override the default specified
     */
    public ZanataInterface(final String projectOverride) {
        details = new ZanataDetails(defaultDetails);
        details.setProject(projectOverride);

        URI URI = null;
        try {
            URI = new URI(details.getServer());
        } catch (URISyntaxException e) {
        }
        final VersionInfo versionInfo = VersionUtility.getVersionInfo(LocaleId.class);
        if (versionInfo.getVersionNo() == null || versionInfo.getVersionNo().isEmpty()
                || versionInfo.getVersionNo().equals("unknown"))
            versionInfo.setVersionNo("1.6.0");

        proxyFactory = new ZanataProxyFactory(URI, details.getUsername(), details.getToken(), versionInfo);
        localeManager = ZanataLocaleManager.getInstance(details.getProject());
    }

    /**
     * Get a specific Source Document from Zanata.
     * 
     * @param id The ID of the Document in Zanata.
     * @return The Zanata Source Document that matches id passed, or null if it doesn't exist.
     */
    public Resource getZanataResource(final String id) {
        ClientResponse<Resource> response = null;
        try {
            final ISourceDocResource client = proxyFactory.getSourceDocResource(details.getProject(), details.getVersion());
            response = client.getResource(id, null);

            final Status status = Response.Status.fromStatusCode(response.getStatus());

            if (status == Response.Status.OK) {
                final Resource entity = response.getEntity();
                return entity;
            } else {
                System.out.println("REST call to getResource() did not complete successfully. HTTP response code was "
                        + status.getStatusCode() + ". Reason was " + status.getReasonPhrase());
            }
        } catch (final Exception ex) {
            ExceptionUtilities.handleException(ex);
        } finally {
            /*
             * If you are using RESTEasy client framework, and returning a Response from your service method, you will
             * explicitly need to release the connection.
             */
            if (response != null)
                response.releaseConnection();
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
                System.out.println("REST call to get() did not complete successfully. HTTP response code was "
                        + status.getStatusCode() + ". Reason was " + status.getReasonPhrase());
            }
        } catch (final Exception ex) {
            ExceptionUtilities.handleException(ex);
        } finally {
            /*
             * If you are using RESTEasy client framework, and returning a Response from your service method, you will
             * explicitly need to release the connection.
             */
            if (response != null)
                response.releaseConnection();
        }

        return null;
    }

    /**
     * Create a Document in Zanata.
     * 
     * @param resource The resource data to be used by Zanata to create the Document.
     * @return True if the document was successfully created, otherwise false.
     */
    public boolean createFile(final Resource resource) {
        ClientResponse<String> response = null;
        try {
            final IFixedSourceDocResource client = proxyFactory.getFixedTranslationResources(details.getProject(),
                    details.getVersion());
            response = client.post(details.getUsername(), details.getToken(), resource, null, true);

            final Status status = Response.Status.fromStatusCode(response.getStatus());

            if (status == Response.Status.CREATED) {
                final String entity = response.getEntity();
                if (entity.trim().length() != 0)
                    System.out.println(entity);

                return true;
            } else {
                System.out.println("REST call to createResource() did not complete successfully. HTTP response code was "
                        + status.getStatusCode() + ". Reason was " + status.getReasonPhrase());
            }

        } catch (final Exception ex) {
            ExceptionUtilities.handleException(ex);
        } finally {
            /*
             * If you are using RESTEasy client framework, and returning a Response from your service method, you will
             * explicitly need to release the connection.
             */
            if (response != null)
                response.releaseConnection();
        }

        return false;
    }

    /**
     * Get a Translation from Zanata using the Zanata Document ID and Locale.
     * 
     * @param id The ID of the document in Zanata.
     * @param locale The locale of the translation to find.
     * @return null if the translation doesn't exist or an error occurred, otherwise the TranslationResource containing the
     *         Translation Strings (TextFlowTargets).
     */
    public TranslationsResource getTranslations(final String id, final LocaleId locale) {
        ClientResponse<TranslationsResource> response = null;
        try {
            final ITranslatedDocResource client = proxyFactory.getTranslatedDocResource(details.getProject(),
                    details.getVersion());
            response = client.getTranslations(id, locale, null);

            final Status status = Response.Status.fromStatusCode(response.getStatus());

            /* Remove the locale if it is forbidden */
            if (status == Response.Status.FORBIDDEN) {
                localeManager.removeLocale(locale);
            } else if (status == Response.Status.OK) {
                final TranslationsResource retValue = response.getEntity();
                return retValue;
            }
        } catch (final Exception ex) {
            ExceptionUtilities.handleException(ex);
        } finally {
            /*
             * If you are using RESTEasy client framework, and returning a Response from your service method, you will
             * explicitly need to release the connection.
             */
            if (response != null)
                response.releaseConnection();
        }

        return null;
    }

    /**
     * Delete a Document from Zanata.
     * 
     * Note: This method should be used with extreme care.
     * 
     * @param id The ID of the document in Zanata to be deleted.
     * @return True if the document was successfully deleted, otherwise false.
     */
    public boolean deleteResource(final String id) {
        ClientResponse<String> response = null;
        try {
            final IFixedSourceDocResource client = proxyFactory.getFixedTranslationResources(details.getProject(),
                    details.getVersion());
            response = client.deleteResource(details.getUsername(), details.getToken(), id);

            final Status status = Response.Status.fromStatusCode(response.getStatus());

            if (status == Response.Status.OK) {
                final String entity = response.getEntity();
                if (entity.trim().length() != 0)
                    System.out.println(entity);
                return true;
            } else {
                System.out.println("REST call to deleteResource() did not complete successfully. HTTP response code was "
                        + status.getStatusCode() + ". Reason was " + status.getReasonPhrase());
            }
        } catch (final Exception ex) {
            ExceptionUtilities.handleException(ex);
        } finally {
            /*
             * If you are using RESTEasy client framework, and returning a Response from your service method, you will
             * explicitly need to release the connection.
             */
            if (response != null)
                response.releaseConnection();
        }

        return false;
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
}