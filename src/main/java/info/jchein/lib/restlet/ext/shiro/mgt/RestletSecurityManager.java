package info.jchein.lib.restlet.ext.shiro.mgt;

import info.jchein.lib.restlet.ext.shiro.session.mgt.RestletSessionKey;
import info.jchein.lib.restlet.ext.shiro.subject.RestletSubjectContext;
import info.jchein.lib.restlet.ext.shiro.subject.support.DefaultRestletSubjectContext;
import info.jchein.lib.restlet.ext.shiro.util.RestletUtils;

import java.io.Serializable;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.restlet.Request;
import org.restlet.Response;

// import de.twenty11.skysail.server.security.shiro.session.mgt.RestletSessionKey;



public class RestletSecurityManager extends DefaultWebSecurityManager {
    public RestletSecurityManager() {
        super();
        setSubjectFactory(new DefaultRestletSubjectFactory());
        setSessionManager(new DefaultWebSessionManager());
    }

    public RestletSecurityManager(final Realm singleRealm) {
        this();
        setRealm(singleRealm);
    }

    @Override
    protected SubjectContext createSubjectContext() {
        return new DefaultRestletSubjectContext();
    }

    @Override
    protected SubjectContext copy(final SubjectContext subjectContext) {
        if (subjectContext instanceof RestletSubjectContext) {
            return new DefaultRestletSubjectContext((RestletSubjectContext) subjectContext);
        }
        return super.copy(subjectContext);
    }

    @Override
    protected SessionKey getSessionKey(final SubjectContext context) {
        if (RestletUtils.isRestlet(context)) {
            Serializable sessionId = context.getSessionId();
            Request request = RestletUtils.getRequest(context);
            Response response = RestletUtils.getResponse(context);
            return new RestletSessionKey(sessionId, request, response);
        } else {
            return super.getSessionKey(context);

        }
    }

    /*
    // TODO: Why disable Subject save?
    @Override
    public Subject createSubject(SubjectContext subjectContext) {
        // create a copy so we don't modify the argument's backing map:
        SubjectContext context = copy(subjectContext);

        // ensure that the context has a SecurityManager instance, and if not, add one:
        context = ensureSecurityManager(context);

        // Resolve an associated Session (usually based on a referenced session ID), and place it in the context before
        // sending to the SubjectFactory. The SubjectFactory should not need to know how to acquire sessions as the
        // process is often environment specific - better to shield the SF from these details:
        context = resolveSession(context);

        // Similarly, the SubjectFactory should not require any concept of RememberMe - translate that here first
        // if possible before handing off to the SubjectFactory:
        context = resolvePrincipals(context);

        Subject subject = doCreateSubject(context);

        // save this subject for future reference if necessary:
        // (this is needed here in case rememberMe principals were resolved and they need to be stored in the
        // session, so we don't constantly rehydrate the rememberMe PrincipalCollection on every operation).
        // Added in 1.2:
        // save(subject);

        return subject;
    }
    */
}
