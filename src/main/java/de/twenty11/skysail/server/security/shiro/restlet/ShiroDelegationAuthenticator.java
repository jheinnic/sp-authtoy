package de.twenty11.skysail.server.security.shiro.restlet;

import org.restlet.Context;
import org.restlet.ext.crypto.CookieAuthenticator;

public class ShiroDelegationAuthenticator extends CookieAuthenticator {

    public ShiroDelegationAuthenticator(Context context, String realm, byte[] encryptSecretKey) {
        super(context, realm, encryptSecretKey);
        setIdentifierFormName("username");
        setSecretFormName("password");
        setLoginFormPath("/login");
        setOptional(true); // we want anonymous users too
        setVerifier(new ShiroDelegatingVerifier());
    }

}
