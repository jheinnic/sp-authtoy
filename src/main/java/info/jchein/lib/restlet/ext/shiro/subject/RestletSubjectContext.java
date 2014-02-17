package info.jchein.lib.restlet.ext.shiro.subject;

import info.jchein.lib.restlet.ext.shiro.util.RestletRequestPairSource;

import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.subject.WebSubjectContext;
import org.restlet.Request;
import org.restlet.Response;

/**
 * A {@code RestSubjectContext} is a {@link SubjectContext} that additionally provides for type-safe
 * methods to set and retrieve a (restlet) {@link Request} and {@link Response}.
 */
public interface RestletSubjectContext extends WebSubjectContext, RestletRequestPairSource {

    Request resolveRestletRequest();

    Response resolveRestletResponse();

    void setRestletRequest(Request request);

    void setRestletResponse(Response response);
}
