package info.jchein.lib.restlet.ext.shiro.restlet;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.restlet.security.SecretVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShiroDelegatingVerifier extends SecretVerifier {

    private static final Logger logger = LoggerFactory.getLogger(ShiroDelegatingVerifier.class);

    @Override
    public int verify(String identifier, char[] secret) {
        // In theory, currentUser should be identified from the same information source that populated
    	// the identifier argument into this call.  Right?  Or perhaps currentUser is an identity-free
    	// yet thread-bound object that acquires its identity during login(Token).
        final Subject currentUser = 
        	SecurityUtils.getSubject();
        final UsernamePasswordToken token = 
        	new UsernamePasswordToken(identifier, secret);
        
        logger.info("login event for user '{}'", identifier);
        try {
            currentUser.login(token);
        } catch (UnknownAccountException uae) {
            logger.info("UnknownAccountException '{}' when login in {}", uae.getMessage(), identifier);
        } catch (IncorrectCredentialsException ice) {
            logger.info("IncorrectCredentialsException '{}' when login in {}", ice.getMessage(), identifier);
        } catch (LockedAccountException lae) {
            logger.info("LockedAccountException '{}' when login in {}", lae.getMessage(), identifier);
        } catch (AuthenticationException ae) {
            logger.error("AuthenticationException '{}' when login in {}", ae.getMessage(), identifier);
        }
        // valid, as user can logging as "anonymous"
        return RESULT_VALID;
    }
}
