package de.twenty11.skysail.server.security.shiro.session.mgt;

import org.apache.shiro.session.mgt.SessionContext;
import org.restlet.Request;
import org.restlet.Response;

import de.twenty11.skysail.server.security.shiro.util.RestletRequestPairSource;

public interface RestletSessionContext extends SessionContext, RestletRequestPairSource {

    Request getRestletRequest();

    void setRequest(Request request);

    Response getRestletResponse();

    void setResponse(Response response);
}
