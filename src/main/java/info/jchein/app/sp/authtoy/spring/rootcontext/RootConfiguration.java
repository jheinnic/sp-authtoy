package info.jchein.app.sp.authtoy.spring.rootcontext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource(name = "authtoyProperties", value = { "classpath:info/jchein/authtoy.properties" })
public class RootConfiguration {
	public RootConfiguration() {
		System.out.println("Constructed root context");
	}
	
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

    @Bean
    @Lazy(false)
    @Scope("application")
    public Object authtoyComponent() {
    	if( keyFilePath == null ) {
    		System.out.println( "keyFilePath is null" );
    	} else {
    		System.out.println( 
    			String.format("keyFilePath from @Value is <%s>", keyFilePath)
    		);
    	}
    	
    	if( env == null ) {
    		System.out.println("env is null");
    	} else {
    		final String spkEnv = env.getProperty("authtoy.stormpath.keyFile");
    		if( spkEnv == null ) {
    			System.out.println("Env is not null, but keyFile property still is");
    		} else {
        		System.out.println( 
            		String.format("keyFilePath from Environment is <%s>", spkEnv)
            	);
    		}
    	}
    	
    	return new StringBuilder(1024);
    }
}
