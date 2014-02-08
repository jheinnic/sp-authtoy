package info.jchein.app.sp.authtoy.spring.appcontext;

import info.jchein.app.sp.authtoy.application.AuthtoyApplication;
import info.jchein.app.sp.authtoy.resource.AuthtoyResource;
import info.jchein.lib.restlet.ext.stormpath.StormpathVerifier;

import org.restlet.data.ChallengeScheme;
import org.restlet.ext.spring.SpringBeanRouter;
import org.restlet.ext.spring.SpringComponent;
import org.restlet.resource.Directory;
import org.restlet.resource.Finder;
import org.restlet.security.ChallengeAuthenticator;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;

@Configuration
@PropertySource( name="authtoyProperties", value={ "classpath:authtoy.properties" })
public class AuthtoyConfiguration {
	private static final String SP_KEY_FILE_PATH = "authtoy.stormpath.keyFile";

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer pspc =
			    new PropertySourcesPlaceholderConfigurer();
			  Resource[] resources = new ClassPathResource[ ]
			    { new ClassPathResource( "authtoy.properties" ) };
			  pspc.setLocations( resources );
			  pspc.setIgnoreUnresolvablePlaceholders( true );
			  return pspc;
	}
 	
	@Autowired
	private Environment env;

	/*
	@Inject 
	@Resource
	public void setEnvironment( Environment env ) {
		this.env = env;
	}
	*/
	
	@Bean(autowire=Autowire.BY_TYPE)
	@Autowired
	Client spClient(Environment env) {
		final String keyFilePath = env.getProperty(SP_KEY_FILE_PATH);
		final ClientBuilder clientBuilder = new ClientBuilder();
		final Client bean = 
			clientBuilder.setApiKeyFileLocation(keyFilePath).build();
		
		return bean;
	}

	@Bean(autowire=Autowire.BY_TYPE)
	@Autowired
	StormpathVerifier spVerifier(Client spClient) { // Environment env) {
		final StormpathVerifier bean = new StormpathVerifier();
		
		bean.setStormpathClient(spClient);
		
		return bean;
	}
	
    @Bean
	SpringComponent authtoyComponent() {
    	SpringComponent bean = new SpringComponent();
    	
    	bean.setDefaultTarget(
    		authtoyApplication()
    	);
    	
    	return bean;
    }
    
    @Bean
	AuthtoyApplication authtoyApplication() {
    	final AuthtoyApplication bean = new AuthtoyApplication();
    	
    	bean.setInboundRoot(router());
    	
    	return bean;
    }
    
    @Bean
	SpringBeanRouter router() {
    	final SpringBeanRouter bean = new SpringBeanRouter();
    	
    	return bean;
    }
    
	@Bean
	Finder finder() {
		final Finder bean = 
			new Finder(
				authtoyApplication().getContext()
			);

		return bean;
	}

	@Bean
	Finder helloFinder() {
		final Finder bean = 
			finder().createFinder(AuthtoyResource.class);

		return bean;
	}

	@Bean(name={"/hello"}, autowire=Autowire.BY_TYPE)
	@Scope("prototype")
	@Autowired
	ChallengeAuthenticator helloGuard(StormpathVerifier spVerifier) { // Environment env) {
		System.out.println("helloGuard");
		
		final ChallengeAuthenticator bean = 
			new ChallengeAuthenticator(
				authtoyApplication().getContext(),
				ChallengeScheme.HTTP_COOKIE,
				"dooRealm"
			);
		
		bean.setVerifier(
			spVerifier
		);
		bean.setNext(
			helloFinder()
		);
		
		System.out.println("...returning");
		return bean;
	}
	
	@Bean(name={"/assets"}, autowire=Autowire.BY_TYPE)
	@Scope("prototype")
	Directory assetsDirectory() {
		System.out.println("assetsDir");
		Directory bean = 
			new Directory(
				authtoyApplication().getContext(),
				"file:///d:/DevProj/Git/sp-authtoy/src/main/webapp/assets"
			);
		
		System.out.println("...returning");
		return bean;
	}
}
