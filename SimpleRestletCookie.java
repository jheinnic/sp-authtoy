package de.twenty11.skysail.server.security.shiro;

import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;

public class SimpleRestletCookie extends SimpleCookie {

    public SimpleRestletCookie(Cookie template) {
        super(template);
    }

}
