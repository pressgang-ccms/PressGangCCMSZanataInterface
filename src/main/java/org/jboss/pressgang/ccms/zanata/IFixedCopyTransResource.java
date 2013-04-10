package org.jboss.pressgang.ccms.zanata;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.zanata.rest.RestConstant;
import org.zanata.rest.dto.CopyTransStatus;
import org.zanata.rest.service.CopyTransResource;

@Path("/copytrans")
public interface IFixedCopyTransResource extends CopyTransResource {

    @POST
    @Path("/proj/{projectSlug}/iter/{iterationSlug}/doc/{docId}")
    public void startCopyTrans(@PathParam("projectSlug") String projectSlug, @PathParam("iterationSlug") String iterationSlug,
            @PathParam("docId") String docId, @HeaderParam(
            RestConstant.HEADER_USERNAME) String username, @HeaderParam(RestConstant.HEADER_API_KEY) String apikey);

    @GET
    @Path("/proj/{projectSlug}/iter/{iterationSlug}/doc/{docId}")
    public CopyTransStatus getCopyTransStatus(@PathParam("projectSlug") String projectSlug,
            @PathParam("iterationSlug") String iterationSlug, @PathParam("docId") String docId, @HeaderParam(
            RestConstant.HEADER_USERNAME) String username, @HeaderParam(RestConstant.HEADER_API_KEY) String apikey);
}
