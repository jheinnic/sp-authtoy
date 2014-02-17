package info.jchein.lib.restlet.ext.shiro.subject.support;

import info.jchein.lib.restlet.ext.shiro.subject.RestletSubject;
import info.jchein.lib.restlet.ext.shiro.subject.RestletSubjectContext;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.subject.support.DefaultWebSubjectContext;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.ext.servlet.ServletUtils;

public class DefaultRestletSubjectContext extends DefaultWebSubjectContext implements RestletSubjectContext {
	private static final long serialVersionUID = -8572623447129358435L;
	
    private static final String RESTLET_REQUEST = DefaultRestletSubjectContext.class.getName() + ".RESTLET_REQUEST";
    private static final String RESTLET_RESPONSE = DefaultRestletSubjectContext.class.getName() + ".RESTLET_RESPONSE";

    public DefaultRestletSubjectContext() {
    }
    
    public DefaultRestletSubjectContext(RestletSubjectContext context) {
        super(context);
    }

    @Override
    public String resolveHost() {
        String host = super.resolveHost();
        if (host == null) {
            Request request = resolveRestletRequest();
            if (request != null) {
                host = request.getHostRef().toString();
            }
        }
        return host;
    }

    @Override
    public Request getRestletRequest() {
        return getTypedValue(RESTLET_REQUEST, Request.class);
    }

    @Override
    public Response getRestletResponse() {
        return getTypedValue(RESTLET_RESPONSE, Response.class);
    }

    @Override
    public Request resolveRestletRequest() {
        Request request = getRestletRequest();

        //fall back on existing subject instance if it exists:
        if (request == null) {
            Subject existing = getSubject();
            if (existing instanceof RestletSubject) {
                request = ((RestletSubject) existing).getRestletRequest();
            }
        }

        return request;
    }

    @Override
    public Response resolveRestletResponse() {
        Response response = getRestletResponse();

        //fall back on existing subject instance if it exists:
        if (response == null) {
        	Subject existing = getSubject();
            if (existing instanceof RestletSubject) {
                response = ((RestletSubject) existing).getRestletResponse();
            }
        }

        return response;
    }

    @Override
    public void setRestletRequest(Request request) {
        if (request != null) {
        	setServletRequest(
        		ServletUtils.getRequest(request)
        	);
            put(RESTLET_REQUEST, request);
        }
    }

    @Override
    public void setRestletResponse(Response response) {
        if (response != null) {
        	setServletResponse(
        		ServletUtils.getResponse(response)
        	);
            put(RESTLET_RESPONSE, response);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Object key : keySet()) {
            sb.append(key.toString()).append(": ").append(get(key).toString()).append("\n");
        }
        return sb.toString();
    }

}
