package de.twenty11.skysail.server.security.shiro.subject;

import org.apache.shiro.subject.SubjectContext;
import org.restlet.Request;
import org.restlet.Response;

import de.twenty11.skysail.server.security.shiro.util.RestletRequestPairSource;

/**
 * A {@code RestSubjectContext} is a {@link SubjectContext} that additionally provides for type-safe
 * methods to set and retrieve a (restlet) {@link Request} and {@link Response}.
 */
public interface RestletSubjectContext extends SubjectContext, RestletRequestPairSource {

    Request resolveRequest();

    Response resolveResponse();

    void setRequest(Request request);

    void setResponse(Response response);
}
