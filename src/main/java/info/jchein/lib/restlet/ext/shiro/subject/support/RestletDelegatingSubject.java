package info.jchein.lib.restlet.ext.shiro.subject.support;

import info.jchein.lib.restlet.ext.shiro.session.mgt.DefaultRestletSessionContext;
import info.jchein.lib.restlet.ext.shiro.subject.RestletSubject;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.ext.servlet.ServletUtils;

public class RestletDelegatingSubject extends WebDelegatingSubject implements RestletSubject {

    private Request request;
    private Response response;

    public RestletDelegatingSubject(
    	PrincipalCollection principals, boolean authenticated, 
    	String host, Session session, boolean sessionEnabled, 
        Request request, Response response, 
        SecurityManager securityManager) {
        super(
        	principals, authenticated, host, session, sessionEnabled,
        	ServletUtils.getRequest(request), 
        	ServletUtils.getResponse(response), 
        	securityManager);
        this.request = request;
        this.response = response;
    }

    @Override
    public Request getRestletRequest() {
        return request;
    }

    @Override
    public Response getRestletResponse() {
        return response;
    }
    
    @Override
    protected SessionContext createSessionContext() {
    	final DefaultRestletSessionContext wsc = new DefaultRestletSessionContext();
        final String host = getHost();
        if (StringUtils.hasText(host)) {
            wsc.setHost(host);
        }
        wsc.setRestletRequest(this.request);
        wsc.setResponse(this.response);
        return wsc;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("Principal: ");
        sb.append(getPrincipal() != null ? getPrincipal().toString() : "<none>");
        sb.append(", authenticated: ").append(isAuthenticated());
        return sb.toString();
    }
}
