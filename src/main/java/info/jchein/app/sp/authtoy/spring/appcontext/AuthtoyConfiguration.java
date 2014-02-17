package info.jchein.app.sp.authtoy.spring.appcontext;

import info.jchein.app.sp.authtoy.application.AuthtoyApplication;
import info.jchein.app.sp.authtoy.resource.AuthtoyResource;
import info.jchein.lib.restlet.ext.shiro.restlet.ShiroDelegationAuthenticator;
import info.jchein.lib.restlet.ext.stormpath.StormpathVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.restlet.Restlet;
import org.restlet.ext.spring.SpringComponent;
import org.restlet.ext.spring.SpringRouter;
import org.restlet.resource.Directory;
import org.restlet.resource.Finder;
import org.restlet.routing.Router;
import org.restlet.routing.VirtualHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;

@Configuration
@PropertySource(name = "authtoyProperties", value = { "classpath:info/jchein/authtoy.properties" })
public class AuthtoyConfiguration {
	// Temporary placeholder--this should come from configuration.
	private static final String AUTH_REALM_NAME = "Authtoy Realm";
	private static final byte[] ENCRYPT_SECRET_KEY = new byte[] { 84, -24, 48, -25, 53, -43, -65, -4, 34, 9, 0, 0, 127, -128, 35, 95, -39, -52, 93, 92, 34, -4, -29};
	
	private static final String SP_KEY_FILE_PATH_NAME = "authtoy.stormpath.keyFile";

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	// private static final String SP_KEY_FILE_PATH_EXPR = "${authtoy.stormpath.keyFile}";
	// @Value(SP_KEY_FILE_PATH_EXPR)
	// private String keyFilePath;

    @Autowired
    private Environment env;

	public AuthtoyConfiguration() {
		System.out.println("Constructed app context");
	}

	@Bean // (name= {"authtoyComponent"})
	@Scope("application")
	public SpringComponent authtoyComponent( ) {
		System.out.print("Constructing \"authtoyComponent\" bean" );
		
		SpringComponent bean = new SpringComponent();

		final ArrayList<Object> clients = new ArrayList<Object>(3);
		clients.add("FILE");
		clients.add("CLAP");
		bean.setClientsList(clients);
		
		System.out.println( "..." );
		return bean;
	}

	@Autowired
	private AuthtoyApplication authtoyApplication;
	
	@Bean // ( name= {"authtoyApplication"} )
	@Lazy(false)
	@Scope("application")
	public AuthtoyApplication authtoyApplication() {
		System.out.println("Constructing \"authtoyApplication\" bean");
		final SpringComponent authtoyComponent = authtoyComponent();
		final AuthtoyApplication bean =
			new AuthtoyApplication(
				authtoyComponent.getContext()
			);
		final VirtualHost host = new VirtualHost( authtoyComponent.getContext() );
//		host.setHostPort("8080");
//		host.setHostDomain("localhost");
//		host.setResourceDomain("kropy");
		host.setServerAddress("127.0.0.1");
		host.setServerPort("8080");
		
		// Application wires both parent-to-child and child-to-parent relationships.  It must do the former, because its
		// parent is a universal dependency.  It opts to do the latter, in order to invert convention.  The
		// container makes itself available to receive applications and hosts, but an application is more
		// intentional about how it is composed of parts.
		bean.setInboundRoot(
			authtoyRouter()
		);
		//authtoyComponent.setDefaultTarget(bean);
		
		// Read, modify, then re-insert host list.  Copy-on-write means get->add without a subsequent set is
		// inadequate.
		final List<VirtualHost> hostList = authtoyComponent.getHosts();
		hostList.add(host);
		authtoyComponent.setHosts(hostList);
		host.attach(bean);
		
		System.out.println( "..." );
		return bean;
	}

	@Bean(name= {"authtoyFinder"})
	@Scope("application")
	@Primary
	Finder authtoyFinder() {
		final Finder bean = 
			new Finder(
				authtoyComponent().getContext()
			);

		return bean;
	}

	// TODO: Candidate factory element
	/*
	@Bean(autowire=Autowire.BY_TYPE)
	@Scope("application")
	Finder helloFinder(final Finder finder) {
		final Finder bean = finder.createFinder(AuthtoyResource.class);

		return bean;
	}
	*/

	@Bean( name= {"authtoyRouter"} )
	@Scope("application")
	Router authtoyRouter(/*final @Routable Collection<Restlet> routePaths*/) {
		final Router bean = 
			new Router(
				authtoyComponent().getContext()
			);
		
		final HashMap<String, Object> routes = new HashMap<String, Object>(5);
		routes.put("/hello", helloResourcePath());
		routes.put("/assets", assetsDirectory());
		
		SpringRouter.setAttachments(bean, routes);

		return bean;
	}

	@Bean(name = {"helloResourcePath","/hello"})
	@Lazy(false)
	@Scope("application")
	Restlet helloResourcePath() {
		/*final ShiroDelegationFilter loadContextBean = 
			new ShiroDelegationFilter(
				authtoyComponent().getContext()
			);*/
		final SpringComponent authtoyComponent = authtoyComponent();
		final StormpathVerifier spVerifier = spVerifier();
		final Finder helloFinder =
				authtoyFinder().createFinder(AuthtoyResource.class);
		final ShiroDelegationAuthenticator authBean = 
			new ShiroDelegationAuthenticator(
				authtoyComponent.getContext(), AUTH_REALM_NAME, ENCRYPT_SECRET_KEY
			);

		// The generic finder could be auto-wired, but its HelloResource-specific
		// augmentation cannot because that would yield two auto-wire candidates for 
		// bean class "Finder".
		authBean.setVerifier(spVerifier);
		authBean.setNext(helloFinder);
		// loadContextBean.setNext(authBean);
		
		return authBean;
	}

	@Bean(name = {"/assets","assetsDirectory"})
	@Scope("application")
	Directory assetsDirectory(/*final authtoyComponent() authtoyComponent()*/) {
		final SpringComponent authtoyComponent = authtoyComponent();
		final Directory bean = 
			new Directory(
				authtoyComponent.getContext(), "clap:/context/assets"
			);

		return bean;
	}

	@Bean(name= {"spVerifier"})
	@Scope("application")
	StormpathVerifier spVerifier() { 
		final StormpathVerifier bean = new StormpathVerifier();

		bean.setStormpathClient(
			spClient()
		);

		return bean;
	}
	
	@Bean(name= {"spClient"})
	@Scope("application")
	Client spClient() {
		final String keyFilePathVal = env.getProperty(SP_KEY_FILE_PATH_NAME);
		final ClientBuilder clientBuilder = new ClientBuilder();
		final Client bean = clientBuilder.setApiKeyFileLocation(keyFilePathVal).build();

		return bean;
	}
}
