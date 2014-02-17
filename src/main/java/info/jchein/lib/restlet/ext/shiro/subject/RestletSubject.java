package de.twenty11.skysail.server.security.shiro.subject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.subject.WebSubject;
import org.apache.shiro.web.subject.support.DefaultWebSubjectContext;
import org.restlet.Request;
import org.restlet.Response;

import de.twenty11.skysail.server.security.shiro.subject.support.SkysailWebSubjectContext;
import de.twenty11.skysail.server.security.shiro.util.RestletRequestPairSource;

/**
 * A {@code RestletSubject} represents a Subject instance that was acquired as a result of an incoming
 * {@link Request}.
 *
 */
public interface RestletSubject extends Subject, RestletRequestPairSource {

    Request getRestletRequest();

    Response getRestletResponse();

    /**
     * A {@code RestletSubject.Builder} performs the same function as a {@link Subject.Builder Subject.Builder}, but
     * additionally ensures that the Restlet request/response pair that is triggering the Subject instance's creation
     * is retained for use by internal Shiro components as necessary.
     */
    public static class Builder extends Subject.Builder {

        /**
         * Constructs a new {@code Restlet.Builder} instance using the {@link SecurityManager SecurityManager} obtained by
         * calling {@code SecurityUtils.}{@link SecurityUtils#getSecurityManager() getSecurityManager()}.  If you want
         * to specify your own SecurityManager instance, use the
         * {@link #Builder(SecurityManager, ServletRequest, ServletResponse)} constructor instead.
         *
         * @param request  the incoming ServletRequest that will be associated with the built {@code WebSubject} instance.
         * @param response the outgoing ServletRequest paired with the ServletRequest that will be associated with the
         *                 built {@code WebSubject} instance.
         */
        public Builder(Request request, Response response) {
            this(SecurityUtils.getSecurityManager(), request, response);
        }

        /**
         * Constructs a new {@code Restlet.Builder} instance using the specified {@code SecurityManager} instance to
         * create the {@link WebSubject WebSubject} instance.
         *
         * @param securityManager the {@code SecurityManager SecurityManager} instance to use to build the
         *                        {@code WebSubject} instance.
         * @param request         the incoming ServletRequest that will be associated with the built {@code WebSubject}
         *                        instance.
         * @param response        the outgoing ServletRequest paired with the ServletRequest that will be associated
         *                        with the built {@code WebSubject} instance.
         */
        public Builder(SecurityManager securityManager, Request request, Response response) {
            super(securityManager);
            if (request == null) {
                throw new IllegalArgumentException("ServletRequest argument cannot be null.");
            }
            if (response == null) {
                throw new IllegalArgumentException("ServletResponse argument cannot be null.");
            }
            setRequest(request);
            setResponse(response);
        }

        /**
         * Overrides the parent implementation to return a new instance of a
         * {@link DefaultWebSubjectContext DefaultWebSubjectContext} to account for the additional request/response
         * pair.
         *
         * @return a new instance of a {@link DefaultWebSubjectContext DefaultWebSubjectContext} to account for the
         *         additional request/response pair.
         */
        @Override
        protected SubjectContext newSubjectContextInstance() {
            return new SkysailWebSubjectContext();
        }

        /**
         * Called by the {@code WebSubject.Builder} constructor, this method places the request object in the
         * context map for later retrieval.
         *
         * @param request the incoming ServletRequest that triggered the creation of the {@code WebSubject} instance.
         * @return 'this' for method chaining.
         */
        protected Builder setRequest(Request request) {
            if (request != null) {
                ((RestletSubjectContext) getSubjectContext()).setRequest(request);
            }
            return this;
        }

        /**
         * Called by the {@code WebSubject.Builder} constructor, this method places the response object in the
         * context map for later retrieval.
         *
         * @param response the outgoing ServletRequest paired with the ServletRequest that triggered the creation of
         *                 the {@code WebSubject} instance.
         * @return 'this' for method chaining.
         */
        protected Builder setResponse(Response response) {
            if (response != null) {
                ((RestletSubjectContext) getSubjectContext()).setResponse(response);
            }
            return this;
        }

        /**
         * Returns {@link #buildSubject() super.buildSubject()}, but additionally ensures that the returned instance
         * is an {@code instanceof} {@link WebSubject WebSubject} and to support a type-safe method so a caller
         * does not have to cast.   Per the parent class's method JavaDoc, this method will return a new instance
         * each time it is called.
         *
         * @return a new {@link WebSubject WebSubject} instance built by this {@code Builder}.
         */
        public RestletSubject buildWebSubject() {
            Subject subject = super.buildSubject();
            if (!(subject instanceof RestletSubject)) {
                String msg = "Subject implementation returned from the SecurityManager was not a " +
                        RestletSubject.class.getName() + " implementation.  Please ensure a Restlet-enabled SecurityManager " +
                        "has been configured and made available to this builder.";
                throw new IllegalStateException(msg);
            }
            return (RestletSubject) subject;
        }
    }

}
