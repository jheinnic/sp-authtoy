package info.jchein.lib.restlet.ext.shiro.session.mgt;

import info.jchein.lib.restlet.ext.shiro.util.RestletRequestPairSource;

import java.util.Map;

import org.apache.shiro.web.session.mgt.DefaultWebSessionContext;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.ext.servlet.ServletUtils;

public class DefaultRestletSessionContext extends DefaultWebSessionContext implements RestletRequestPairSource {
    private static final long serialVersionUID = 6107613972091312584L;
	
	private static final String RESTLET_REQUEST = DefaultWebSessionContext.class.getName() + ".RESTLET_REQUEST";
    private static final String RESTLET_RESPONSE = DefaultWebSessionContext.class.getName() + ".RESTLET_RESPONSE";
    
    public DefaultRestletSessionContext() {
        super();
    }

    public DefaultRestletSessionContext(Map<String, Object> map) {
        super(map);
    }

    public void setRestletRequest(Request request) {
        if (request != null) {
        	setServletRequest(
        		ServletUtils.getRequest(request)
        	);
        	put(RESTLET_REQUEST, request);
        }
    }

    public Request getRestletRequest() {
        return getTypedValue(RESTLET_REQUEST, Request.class);
    }

    public void setResponse(Response response) {
        if (response != null) {
        	setServletResponse(
            	ServletUtils.getResponse(response)
            );
        	put(RESTLET_RESPONSE, response);
        }
    }

    public Response getRestletResponse() {
        return getTypedValue(RESTLET_RESPONSE, Response.class);
    }
}
