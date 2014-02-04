package info.jchein.app.sp.authtoy.resource;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class AuthtoyResource extends ServerResource {

  @Get
  public String getResource()  {
    return "Hello World!";
  }
}
