package de.twenty11.skysail.server.security.shiro.mgt;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.restlet.Request;
import org.restlet.Response;

import de.twenty11.skysail.server.security.shiro.subject.RestletSubjectContext;
import de.twenty11.skysail.server.security.shiro.subject.support.RestletDelegatingSubject;

public class SkysailWebSubjectFactory extends DefaultWebSubjectFactory {

    @Override
    public Subject createSubject(SubjectContext context) {
        if (!(context instanceof RestletSubjectContext)) {
            return super.createSubject(context);
        }
        RestletSubjectContext rsc = (RestletSubjectContext) context;
        SecurityManager securityManager = rsc.resolveSecurityManager();
        Session session = rsc.resolveSession();
        boolean sessionEnabled = rsc.isSessionCreationEnabled();
        PrincipalCollection principals = rsc.resolvePrincipals();
        boolean authenticated = rsc.resolveAuthenticated();
        String host = rsc.resolveHost();
        Request request = rsc.resolveRequest();
        Response response = rsc.resolveResponse();

        return new RestletDelegatingSubject(principals, authenticated, host, session, sessionEnabled, request,
                response, securityManager);

    }

}
