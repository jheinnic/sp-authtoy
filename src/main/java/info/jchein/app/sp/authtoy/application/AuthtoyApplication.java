package info.jchein.app.sp.authtoy.application;

import org.restlet.Application;
import org.restlet.Context;
import org.springframework.stereotype.Component;

@Component
public class AuthtoyApplication extends Application {
	public AuthtoyApplication() {
		super();
	}

	public AuthtoyApplication(Context context) {
		super(context);
	}

}
