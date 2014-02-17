package info.jchein.lib.restlet.ext.shiro.mgt;

import info.jchein.lib.restlet.ext.shiro.subject.RestletSubjectContext;
import info.jchein.lib.restlet.ext.shiro.subject.support.RestletDelegatingSubject;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.restlet.Request;
import org.restlet.Response;

public class DefaultRestletSubjectFactory extends DefaultWebSubjectFactory {

    @Override
    public Subject createSubject(SubjectContext context) {
        if (!(context instanceof RestletSubjectContext)) {
            return super.createSubject(context);
        }
        final RestletSubjectContext rsc = (RestletSubjectContext) context;
        final PrincipalCollection principals = rsc.resolvePrincipals();
        final boolean authenticated = rsc.resolveAuthenticated();
        final String host = rsc.resolveHost();
        final Session session = rsc.resolveSession();
        final boolean sessionEnabled = rsc.isSessionCreationEnabled();
        final Request request = rsc.resolveRestletRequest();
        final Response response = rsc.resolveRestletResponse();
        final SecurityManager securityManager = rsc.resolveSecurityManager();

        return 
        	new RestletDelegatingSubject(
        		principals, authenticated, 
        		host, session, sessionEnabled, 
        		request, response, securityManager
    		);
    }
}
