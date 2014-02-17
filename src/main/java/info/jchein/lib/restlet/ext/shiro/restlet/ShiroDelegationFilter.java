package info.jchein.lib.restlet.ext.shiro.restlet;

import info.jchein.lib.restlet.ext.shiro.subject.RestletSubject;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Cookie;
import org.restlet.routing.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShiroDelegationFilter extends Filter {

    private static final Logger logger = LoggerFactory.getLogger(ShiroDelegationFilter.class);
    
    public ShiroDelegationFilter(Context context) {
        super(context);
    }

    @Override
    protected int doHandle(final Request request, final Response response) {
        final int result = CONTINUE;
        
        logger.info("Starting ShiroDelegationFilter for request {}", request);
        logger.info("Submitted cookies {}:", request.getCookies().size());
        for (Cookie cookie : request.getCookies()) {
            logger.info("Name {}={}", new Object[] {cookie.getName(), cookie.getValue()});
        }
        
        final Subject subject = 
        	new RestletSubject.Builder(
        		request, response
        	).buildWebSubject();
        
        logger.info("Filter found subject '{}'", subject);
        
        ThreadContext.bind(subject);
        /*subject.execute(new Callable() {
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
        });*/

        return result;
    }

//    private Subject createSubject(Request request, Response response) {
//        return new RestletSubject.Builder(request, response).buildWebSubject();
//    }

}
