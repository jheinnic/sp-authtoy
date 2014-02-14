package de.twenty11.skysail.server.security.shiro.restlet;

import java.util.concurrent.Callable;

import org.apache.shiro.subject.Subject;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Cookie;
import org.restlet.data.Status;
import org.restlet.routing.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.twenty11.skysail.server.security.shiro.subject.RestletSubject;

public class ShiroDelegationFilter extends Filter {

    private static final Logger logger = LoggerFactory.getLogger(ShiroDelegationFilter.class);
    
    public ShiroDelegationFilter(Context context) {
        super(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected int doHandle(final Request request, final Response response) {
        final int result = CONTINUE;
        
        logger.info("Starting ShiroDelegationFilter for request {}", request);
        logger.info("Submitted cookies {}:", request.getCookies().size());
        for (Cookie cookie : request.getCookies()) {
            logger.info("Name {}={}", new Object[] {cookie.getName(), cookie.getValue()});
        }
        
        final Subject subject = createSubject(request, response);
        
        logger.info("Filter found subject '{}'", subject);
        
        subject.execute(new Callable() {
            public Object call() throws Exception {
                //updateSessionLastAccessTime(request, response);
                if (getNext() != null) {
                    getNext().handle(request, response);

                    // Re-associate the response to the current thread
                    Response.setCurrent(response);

                    // Associate the context to the current thread
                    if (getContext() != null) {
                        Context.setCurrent(getContext());
                    }
                } else {
                    response.setStatus(Status.SERVER_ERROR_INTERNAL);
                    getLogger().warning("The filter " + getName() + " was executed without a next Restlet attached to it.");
                }
                
                return null;
            }
        });

        return result;
    }

    private Subject createSubject(Request request, Response response) {
        return new RestletSubject.Builder(request, response).buildWebSubject();
    }

}
