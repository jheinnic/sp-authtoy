package de.twenty11.skysail.server.security.shiro.session.mgt;

import java.util.Map;

import org.apache.shiro.session.mgt.DefaultSessionContext;
import org.apache.shiro.web.session.mgt.DefaultWebSessionContext;
import org.restlet.Request;
import org.restlet.Response;

public class SkysailWebSessionContext extends DefaultSessionContext implements RestletSessionContext {

    private static final String RESTLET_REQUEST = DefaultWebSessionContext.class.getName() + ".RESTLET_REQUEST";
    private static final String RESTLET_RESPONSE = DefaultWebSessionContext.class.getName() + ".RESTLET_RESPONSE";

    public SkysailWebSessionContext() {
        super();
    }

    public SkysailWebSessionContext(Map<String, Object> map) {
        super(map);
    }

    public void setRequest(Request request) {
        if (request != null) {
            put(RESTLET_REQUEST, request);
        }
    }

    public Request getRestletRequest() {
        return getTypedValue(RESTLET_REQUEST, Request.class);
    }

    public void setResponse(Response response) {
        if (response != null) {
            put(RESTLET_RESPONSE, response);
        }
    }

    public Response getRestletResponse() {
        return getTypedValue(RESTLET_RESPONSE, Response.class);
    }

}
