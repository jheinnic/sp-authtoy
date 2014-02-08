package info.jchein.app.sp.authtoy.spring.rootcontext;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RootConfiguration {
	public RootConfiguration() {
		System.out.println("Constructed app context");
	}
}
