package info.jchein.lib.restlet.ext.shiro.session.mgt;

import info.jchein.lib.restlet.ext.shiro.util.RestletRequestPairSource;

import java.io.Serializable;

import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.ext.servlet.ServletUtils;

public class RestletSessionKey extends WebSessionKey implements RestletRequestPairSource {
	private static final long serialVersionUID = 7348601448317741409L;
	
	private final transient Request request;
    private final transient Response response;

    public RestletSessionKey(final Request request, final Response response) {
    	super( ServletUtils.getRequest(request), ServletUtils.getResponse(response) );
    	// No need to null-check as the superclass preconditions would not have been met otherwise.
        this.request = request;
        this.response = response;
    }

    public RestletSessionKey(final Serializable sessionId, final Request request, final Response response) {
        this(request, response);
        setSessionId(sessionId);
    }

    public Request getRestletRequest() {
        return request;
    }

    public Response getRestletResponse() {
        return response;
	}
}
