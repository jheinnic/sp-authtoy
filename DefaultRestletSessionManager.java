package de.twenty11.skysail.server.security.shiro.session.mgt;

import java.io.Serializable;

import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.apache.shiro.web.session.mgt.WebSessionManager;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CookieSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.twenty11.skysail.server.security.shiro.util.RestletUtils;

public class SkysailWebSessionManager extends DefaultSessionManager implements WebSessionManager {

    private static Logger logger = LoggerFactory.getLogger(SkysailWebSessionManager.class);

    public SkysailWebSessionManager() {
        logger.info("creating new SkysailWebSessionManager");
    }

    @Override
    public boolean isServletContainerSessions() {
        return false;
    }

    /**
     * Stores the Session's ID, usually as a Cookie, to associate with future requests.
     * 
     * @param session
     *            the session that was just {@link #createSession created}.
     */
    @Override
    protected void onStart(Session session, SessionContext context) {
        // super.onStart(session, context);

        // if (!WebUtils.isHttp(context)) {
        // log.debug("SessionContext argument is not HTTP compatible or does not have an HTTP request/response " +
        // "pair. No session ID cookie will be set.");
        // return;
        //
        // }
        Request request = RestletUtils.getRequest(context);
        Response response = RestletUtils.getResponse(context);

        Serializable sessionId = session.getId();
        storeSessionId(sessionId, request, response);
        // request.removeAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE);
        // request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_IS_NEW, Boolean.TRUE);
    }

    @Override
    public Serializable getSessionId(SessionKey key) {
        Serializable id = super.getSessionId(key);
        if (id == null && RestletUtils.isRestlet(key)) {
            Request request = RestletUtils.getRequest(key);
            Response response = RestletUtils.getResponse(key);
            id = getSessionId(request, response);
        }
        return id;
    }

    protected Serializable getSessionId(Request request, Response response) {
        //return getReferencedSessionId(request, response);
        return getSessionIdCookieValue(request, response);
    }

    @Override
    protected void onExpiration(Session s, ExpiredSessionException ese, SessionKey key) {
        super.onExpiration(s, ese, key);
        onInvalidation(key);
    }

    @Override
    protected void onInvalidation(Session session, InvalidSessionException ise, SessionKey key) {
        super.onInvalidation(session, ise, key);
        onInvalidation(key);
    }

    private void onInvalidation(SessionKey key) {
        Request request = RestletUtils.getRequest(key);
        if (request != null) {
            request.getAttributes().remove(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID);
        }
//        if (WebUtils.isHttp(key)) {
//            logger.debug("Referenced session was invalid.  Removing session ID cookie.");
//            removeSessionIdCookie(WebUtils.getHttpRequest(key), WebUtils.getHttpResponse(key));
//        } else {
//            logger.debug("SessionKey argument is not HTTP compatible or does not have an HTTP request/response "
//                    + "pair. Session ID cookie will not be removed due to invalidated session.");
//        }
    }

    @Override
    protected void onStop(Session session, SessionKey key) {
        super.onStop(session, key);
        //if (WebUtils.isHttp(key)) {
            Request request = RestletUtils.getRequest(key);
            Response response = RestletUtils.getResponse(key);
            // log.debug("Session has been stopped (subject logout or explicit stop).  Removing session ID cookie.");
            // removeSessionIdCookie(request, response);
        // } else {
        // // log.debug("SessionKey argument is not HTTP compatible or does not have an HTTP request/response " +
        // // "pair. Session ID cookie will not be removed due to stopped session.");
        // }
    }

    private void storeSessionId(Serializable currentId, Request request, Response response) {
        if (currentId == null) {
            String msg = "sessionId cannot be null when persisting for subsequent requests.";
            throw new IllegalArgumentException(msg);
        }

        CookieSetting cookie = createCookie();
        // Cookie template = getSessionIdCookie();
        // Cookie cookie = new SimpleRestletCookie(template);
        String idString = currentId.toString();
        cookie.setValue(idString);
        // cookie.saveTo(request, response);

        response.getCookieSettings().add(cookie);

        // log.trace("Set session ID cookie for session with id {}", idString);
    }

    private String getSessionIdCookieValue(Request request, Response response) {
        if (!(request instanceof Request)) {
            logger.debug("Current request is not an RestletRequest - cannot get session ID cookie.  Returning null.");
            return null;
        }
        //return getSessionIdCookie().readValue(httpRequest, WebUtils.toHttp(response));
        if (request.getCookies().size() == 0) {
            return null;
        }
        org.restlet.data.Cookie sessionCookie = request.getCookies().getFirst(ShiroHttpSession.DEFAULT_SESSION_ID_NAME);
        return sessionCookie != null ? sessionCookie.getValue() : null;
    }

//    private Serializable getReferencedSessionId(Request request, Response response) {
//
//        String id = getSessionIdCookieValue(request, response);
//        if (id != null) {
//            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE,
//                    ShiroHttpServletRequest.COOKIE_SESSION_ID_SOURCE);
//        } else {
//            // not in a cookie, or cookie is disabled - try the request URI as a fallback (i.e. due to URL rewriting):
//
//            // try the URI path segment parameters first:
//            id = getUriPathSegmentParamValue(request, ShiroHttpSession.DEFAULT_SESSION_ID_NAME);
//
//            if (id == null) {
//                // not a URI path segment parameter, try the query parameters:
//                String name = getSessionIdName();
//                id = request.getParameter(name);
//                if (id == null) {
//                    // try lowercase:
//                    id = request.getParameter(name.toLowerCase());
//                }
//            }
//            if (id != null) {
//                request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE,
//                        ShiroHttpServletRequest.URL_SESSION_ID_SOURCE);
//            }
//        }
//        if (id != null) {
//            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
//            // automatically mark it valid here. If it is invalid, the
//            // onUnknownSession method below will be invoked and we'll remove the attribute at that time.
//            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
//        }
//        return id;
//    }

    // SHIRO-351
    // also see http://cdivilly.wordpress.com/2011/04/22/java-servlets-uri-parameters/
    // since 1.2.2
//    private String getUriPathSegmentParamValue(Request servletRequest, String paramName) {
//
//        if (!(servletRequest instanceof HttpServletRequest)) {
//            return null;
//        }
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        String uri = request.getRequestURI();
//        if (uri == null) {
//            return null;
//        }
//
//        int queryStartIndex = uri.indexOf('?');
//        if (queryStartIndex >= 0) { // get rid of the query string
//            uri = uri.substring(0, queryStartIndex);
//        }
//
//        int index = uri.indexOf(';'); // now check for path segment parameters:
//        if (index < 0) {
//            // no path segment params - return:
//            return null;
//        }
//
//        // there are path segment params, let's get the last one that may exist:
//
//        final String TOKEN = paramName + "=";
//
//        uri = uri.substring(index + 1); // uri now contains only the path segment params
//
//        // we only care about the last JSESSIONID param:
//        index = uri.lastIndexOf(TOKEN);
//        if (index < 0) {
//            // no segment param:
//            return null;
//        }
//
//        uri = uri.substring(index + TOKEN.length());
//
//        index = uri.indexOf(';'); // strip off any remaining segment params:
//        if (index >= 0) {
//            uri = uri.substring(0, index);
//        }
//
//        return uri; // what remains is the value
//    }

    private CookieSetting createCookie() {
        CookieSetting cookieSetting = new CookieSetting(ShiroHttpSession.DEFAULT_SESSION_ID_NAME, null);
        cookieSetting.setAccessRestricted(true);
        cookieSetting.setPath("/");
        cookieSetting.setComment("Skysail cookie-based authentication");
        cookieSetting.setMaxAge(300);
        return cookieSetting;
    }

    private void removeSessionIdCookie(Request request, Response response) {
        //getSessionIdCookie().removeFrom(request, response);
    }

//    private String getSessionIdName() {
//        String name = this.sessionIdCookie != null ? this.sessionIdCookie.getName() : null;
//        if (name == null) {
//            name = ShiroHttpSession.DEFAULT_SESSION_ID_NAME;
//        }
//        return name;
//    }



}
