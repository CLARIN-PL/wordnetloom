package pl.edu.pwr.wordnetloom.server;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ForbiddenMapper implements ExceptionMapper<ForbiddenException> {

   public Response toResponse(ForbiddenException e) {
      System.out.println(e);
      return Response.status(Response.Status.FORBIDDEN).build();
   }
}