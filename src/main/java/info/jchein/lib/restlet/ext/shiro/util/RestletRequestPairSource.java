package info.jchein.lib.restlet.ext.shiro.util;

import org.apache.shiro.web.util.RequestPairSource;
import org.restlet.Request;
import org.restlet.Response;

/**
 * A {@code RestletRequestPairSource} is a provider of a {@link Request Request} and
 * {@link Response Response} pair associated with a currently executing request.  This is 
 * a similar interface to {@link RequestPairResource} for restlet environments.
 *
 * @since 1.0
 */
public interface RestletRequestPairSource extends RequestPairSource {

    Request getRestletRequest();

    Response getRestletResponse();

}
