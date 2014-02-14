package de.twenty11.skysail.server.security.shiro.util;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.util.RequestPairSource;
import org.restlet.Request;
import org.restlet.Response;

/**
 * Simple utility class for operations used across multiple class hierarchies in the restlet related framework code.
 *
 */
public class RestletUtils {

    public static Request getRequest(Object requestPairSource) {
        if (requestPairSource instanceof RestletRequestPairSource) {
            return ((RestletRequestPairSource) requestPairSource).getRestletRequest();
        }
        return null;
    }

    public static Response getResponse(Object requestPairSource) {
        if (requestPairSource instanceof RestletRequestPairSource) {
            return ((RestletRequestPairSource) requestPairSource).getRestletResponse();
        }
        return null;
    }

    public static boolean isRestlet(Object requestPairSource) {
        return requestPairSource instanceof RestletRequestPairSource && isRestlet((RestletRequestPairSource) requestPairSource);
    }

    private static boolean isRestlet(RestletRequestPairSource source) {
        Request request = source.getRestletRequest();
        Response response = source.getRestletResponse();
        return request != null && response != null;
    }



}
