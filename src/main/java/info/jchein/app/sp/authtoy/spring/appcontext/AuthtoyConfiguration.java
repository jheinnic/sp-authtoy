package info.jchein.app.sp.authtoy.spring.appcontext;

import info.jchein.app.sp.authtoy.application.AuthtoyApplication;
import info.jchein.app.sp.authtoy.resource.AuthtoyResource;
import info.jchein.lib.restlet.ext.stormpath.StormpathVerifier;

import java.util.ArrayList;
import java.util.HashMap;

import org.restlet.data.ChallengeScheme;
import org.restlet.ext.spring.SpringComponent;
import org.restlet.ext.spring.SpringRouter;
import org.restlet.resource.Directory;
import org.restlet.resource.Finder;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;

@Configuration
@PropertySource(name = "authtoyProperties", value = { "classpath:info/jchein/authtoy.properties" })
public class AuthtoyConfiguration {
	private static final String SP_KEY_FILE_PATH_NAME = "authtoy.stormpath.keyFile";
	private static final String SP_KEY_FILE_PATH_EXPR = "${authtoy.stormpath.keyFile}";

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Value("${authtoy.stormpath.keyFile}")
	private String keyFilePath;

    @Autowired
    private Environment env;

	public AuthtoyConfiguration() {
		System.out.println("Constructed app context");
	}

	@Bean( name= {"authtoyComponent"} )
	@Scope("application")
	public SpringComponent authtoyComponent() {
		System.out.print("Constructing \"authtoyComponent\" bean" );
		
		final SpringComponent bean = new SpringComponent();
		final AuthtoyApplication authtoyApplication = authtoyApplication();

		final ArrayList<Object> clients = new ArrayList<Object>();
		clients.add("FILE");
		bean.setClientsList(clients);
		bean.setDefaultTarget(authtoyApplication);

		// Wire the router to the application here to avoid circular dependencies
		// between router and application that otherwise can only be resolved by 
		// pushing the circular dependencies further along the route chain towards
		// resource prototype leaves.
		final Router router = router();
		authtoyApplication.setInboundRoot(router);

		System.out.println( "..." );
		return bean;
	}

	@Bean
	@Scope("application")
	Client spClient() {
		String keyFilePathVal = env.getProperty("authtoy.stormpath.keyFile");
		final ClientBuilder clientBuilder = new ClientBuilder();
		final Client bean = clientBuilder.setApiKeyFileLocation(keyFilePathVal).build();

		return bean;
	}

	@Bean
	@Scope("application")
	StormpathVerifier spVerifier() { // Environment env) {
		final StormpathVerifier bean = new StormpathVerifier();

		bean.setStormpathClient(spClient());

		return bean;
	}

	@Bean
	@Scope("application")
	@Autowired
	AuthtoyApplication authtoyApplication() {
		final AuthtoyApplication bean = new AuthtoyApplication();

		return bean;
	}

	@Bean
	@Scope("application")
	Finder finder() {
		final Finder bean = new Finder(authtoyApplication().getContext());

		return bean;
	}

	@Bean
	@Scope("application")
	Finder helloFinder() {
		final Finder bean = finder().createFinder(AuthtoyResource.class);

		return bean;
	}

	// Be explicit about this not being lazy since not instantiating it yields a 
	// non-functional, but Component-attached Application object.
	@Bean
	@Lazy(false)
	@Scope("application")
	Router router() {
		final AuthtoyApplication authtoyApplication = authtoyApplication();
		final Router bean = 
			new Router(authtoyApplication.getContext()
		);
		
		HashMap<String, Object> routes = new HashMap<String, Object>(5);
		routes.put("/hello", helloGuard());
		routes.put("/assets", assetsDirectory());
		SpringRouter.setAttachments(bean, routes);

		return bean;
	}

	@Bean(name = "/hello", autowire = Autowire.BY_NAME)
	@Scope("application")
	ChallengeAuthenticator helloGuard( ) {
		System.out.println("helloGuard");

		final ChallengeAuthenticator bean = new ChallengeAuthenticator(
				authtoyApplication().getContext(), ChallengeScheme.HTTP_COOKIE,
				"dooRealm");

		// The generic finder could be auto-wired, but its HelloResource-specific
		// augmentation cannot because that would yield two auto-wire candidates for 
		// bean class "Finder".
		bean.setVerifier(spVerifier());
		bean.setNext(helloFinder());

		System.out.println("...returning");
		return bean;
	}

	@Bean(name = "/assets", autowire = Autowire.BY_TYPE)
	@Scope("application")
	Directory assetsDirectory() {
		System.out.println("assetsDir");
		final Directory bean = 
			new Directory(
				authtoyApplication().getContext(),
				"cpath:/assets"
				// "file:///d:/Dropbox/sp-authtoy/src/main/webapp/assets"
			);

		System.out.println("...returning");
		return bean;
	}
}
