package info.jchein.app.sp.authtoy.resource;

import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.stormpath.sdk.client.Client;

public class UsersResource extends ServerResource {

	private Client spClient = null;

	/*
	private String keyFilePath = null;
	public void setKeyFilePath( final String keyFilePath ) {
		this.keyFilePath = keyFilePath;
	}
	@PostConstruct
	void afterPropertiesSet() {
		this.client = new ClientBuilder().setApiKeyFileLocation(keyFilePath).build(); 
		
	}
	*/
	
	public void setSpClient(final Client spClient) {
		this.spClient = spClient;
	}
	
	@Post()
	void createUser() {
		if( spClient == null ) {
			throw new NullPointerException();
		}
	}

}
