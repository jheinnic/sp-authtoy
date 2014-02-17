package de.twenty11.skysail.server.security.shiro.util;

import org.restlet.Request;
import org.restlet.Response;

/**
 * A {@code RestletRequestPairSource} is a provider of a {@link Request Request} and
 * {@link Response Response} pair associated with a currently executing request.  This is 
 * a similar interface to {@link RequestPairResource} for restlet environments.
 *
 * @since 1.0
 */
public interface RestletRequestPairSource {

    Request getRestletRequest();

    Response getRestletResponse();

}
