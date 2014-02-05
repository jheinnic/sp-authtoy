package info.jchein.lib.restlet.ext.spring;

import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.security.ChallengeAuthenticator;

public class SpringChallengeAuthenticator extends ChallengeAuthenticator {

	public SpringChallengeAuthenticator(Restlet restlet, String challengeScheme, String realm) {
		super(restlet.getContext(), ChallengeScheme.valueOf(challengeScheme), realm);
		// TODO Auto-generated constructor stub
	}

	public SpringChallengeAuthenticator(Restlet restlet, ChallengeScheme challengeScheme, String realm) {
		super(restlet.getContext(), challengeScheme, realm);
		// TODO Auto-generated constructor stub
	}

}
