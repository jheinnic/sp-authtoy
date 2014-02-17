package info.jchein.lib.restlet.ext.shiro.restlet;

import org.restlet.Context;
import org.restlet.ext.crypto.CookieAuthenticator;

public class ShiroDelegationAuthenticator extends CookieAuthenticator {

    public ShiroDelegationAuthenticator(Context context, String realm, byte[] encryptSecretKey) {
        super(context, realm, encryptSecretKey);
        setIdentifierFormName("username");
        setSecretFormName("password");
        setLoginFormPath("/login");
        setRedirectQueryName("nextURI");
        setLoginPath("/login");
        setInterceptingLogin(true);
        setLogoutPath("/logout");
        setInterceptingLogout(true);
        setOptional(false); // we do not want anonymous users
        setVerifier(new ShiroDelegatingVerifier());
        setEnroler(null);
    }

}
