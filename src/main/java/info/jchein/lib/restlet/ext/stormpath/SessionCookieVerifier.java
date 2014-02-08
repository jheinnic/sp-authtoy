package info.jchein.lib.restlet.ext.stormpath;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ClientInfo;
import org.restlet.data.Cookie;
import org.restlet.security.User;
import org.restlet.security.Verifier;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

public class SessionCookieVerifier implements Verifier {
	// private static final Logger LOG = LoggerFactory.getLogger(StormpathVerifier.class);
	
	@Override
	public int verify(Request request, Response response) {
		Cookie atToken = request.getCookies().getFirst("authtoyToken");
		if (atToken != null) {
			if (isValid(atToken)) {
				if(isStale(atToken)) {
					System.out.println( "Got token that was valid and stale" );
					return Verifier.RESULT_STALE;
				}
				
				final ClientInfo cInfo = request.getClientInfo();
				final User user = cInfo.getUser();
				user.setIdentifier(atToken.getValue());
				refreshToken(request);
				
				System.out.println( "Got token that was valid and current" );
				return Verifier.RESULT_VALID;
			} 
		}
		
		System.out.println( "Neither token nor credentials present" );
		return Verifier.RESULT_MISSING;
	}

	private void refreshToken(Request request) {
		final Cookie cookie = new Cookie();
		cookie.setName("authtoyToken");
		cookie.setValue("VALID");
		request.getCookies().add(cookie);
	}

	private boolean isValid(final Cookie atToken) {
		if( ((Math.floor(Math.random() * 100)) % 2) == 1 ) {
			return true;
		}

		return false;	
	}

	private boolean isStale(final Cookie atToken) {
		if( ((Math.floor(Math.random() * 100)) % 2) == 1 ) {
			
			return true;
		}

		return false;	
	}
}
