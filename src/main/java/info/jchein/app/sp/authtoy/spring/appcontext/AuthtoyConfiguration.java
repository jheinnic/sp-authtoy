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
@Lazy(true)
@PropertySource(name = "authtoyProperties", value = { "classpath:info/jchein/authtoy.properties" })
public class AuthtoyConfiguration {
	private static final String SP_KEY_FILE_PATH_NAME = "authtoy.stormpath.keyFile";
	private static final String SP_KEY_FILE_PATH_EXPR = "${authtoy.stormpath.keyFile}";

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
//		Resource[] resources = new Resource[] {
//				new FileSystemResource(
//						"/Users/jheinnic/Dropbox/sp-authtoy/src/main/resources/authtoy.properties"),
//				new ClassPathResource("authtoy.properties"),
//				new ClassPathResource("info/jchein/authtoy.properties"),
//				new ClassPathResource("/info/jchein/authtoy.properties") };
//
//		pspc.setLocations(resources);
//		pspc.setIgnoreUnresolvablePlaceholders(false);//
//		return pspc;
	}

	@Value("${authtoy.stormpath.keyFile}")
	private String keyFilePath;

    @Autowired
    private Environment env;

	public AuthtoyConfiguration() {
		System.out.println("Constructed app context");
	}

	@Bean
	@Scope("application")
	SpringComponent authtoyComponent() {
		SpringComponent bean = new SpringComponent();

		bean.setDefaultTarget(authtoyApplication());
		bean.setClient("file");

		return bean;
	}

	@Bean
	@Scope("application")
	Client spClient() {
		String keyFilePathVal = keyFilePath != null ? keyFilePath : env.getProperty("authtoy.stormpath.keyFile");
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
	AuthtoyApplication authtoyApplication() {
		final AuthtoyApplication bean = new AuthtoyApplication();

		bean.setInboundRoot(router());

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

	@Bean
	@Scope("application")
	SpringBeanRouter router() {
		final SpringBeanRouter bean = new SpringBeanRouter();

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

	@Bean(name = "/assets", autowire = Autowire.BY_NAME)
	@Scope("application")
	Directory assetsDirectory(final AuthtoyApplication authtoyApplication) {
		System.out.println("assetsDir");
		final Directory bean = 
			new Directory(
				authtoyApplication.getContext(),
				"file:///d:/DevProj/Git/sp-authtoy/src/main/webapp/assets"
			);

		System.out.println("...returning");
		return bean;
	}
}
