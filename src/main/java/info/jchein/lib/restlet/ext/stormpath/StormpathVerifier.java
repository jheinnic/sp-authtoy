package info.jchein.lib.restlet.ext.stormpath;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.security.Verifier;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.tenant.Tenant;

public class StormpathVerifier implements Verifier {
	// private static final Logger LOG = LoggerFactory.getLogger(StormpathVerifier.class);
	
	@Override
	public int verify(Request request, Response response) {
		final int retVal;
		if(requestHasCredentials(request)) {
			if(validateCredentials(request)) {
				// Modularity and separation of concerns.  Let the downstream resource deal with creation of
				// a session token.  That way, this component is reusable for gating other side effects with an
				// authentication step.
				// generateToken(request);
				
				System.out.println( "Valid credentials present" );
				retVal = Verifier.RESULT_VALID;
			} else {
				System.out.println( "Invalid credentials present" );
				retVal = Verifier.RESULT_INVALID;
			}
		} else {
			System.out.println( "No credentials present" );
			retVal = Verifier.RESULT_MISSING;
		}
		
		return retVal;
	}

	/*
	private void generateToken(Request request) {
		final Cookie cookie = new Cookie();
		cookie.setName("authtoyToken");
		cookie.setValue("VALID");
		request.getCookies().add(cookie);
	}

	private void refreshToken(Request request) {
		final Cookie cookie = new Cookie();
		cookie.setName("authtoyToken");
		cookie.setValue("VALID");
		request.getCookies().add(cookie);
	}

	private boolean isTokenValid(final Cookie atToken) {
		if( ((Math.floor(Math.random() * 100)) % 2) == 1 ) {
			return true;
		}

		return false;	
	}

	private boolean isTokenStale(final Cookie atToken) {
		if( ((Math.floor(Math.random() * 100)) % 2) == 1 ) {
			
			return true;
		}

		return false;	
	}
	*/
	
	private boolean requestHasCredentials(final Request request) {
		if( ((Math.floor(Math.random() * 100)) % 2) == 1 ) {
			return true;
		}
		
		return false;	
	}

	private boolean validateCredentials(final Request request) {
		if( ((Math.floor(Math.random() * 100)) % 2) == 1 ) {
			return true;
		}
		
		Tenant t = spClient.getCurrentTenant();
		System.out.println(t);

		return false;	
	}


	/*
	public void setKeyFilePath( final String keyFilePath ) {
		this.keyFilePath = keyFilePath;
	}
	
	@PostConstruct
	void afterPropertiesSet() {
		this.client = new ClientBuilder().setApiKeyFileLocation(keyFilePath).build(); 
		
	}
	*/

	private Client spClient = null;
	
	@Required
	public void setStormpathClient(Client spClient) {
		this.spClient = spClient;
	}
}
