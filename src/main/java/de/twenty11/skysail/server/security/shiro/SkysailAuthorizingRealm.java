package de.twenty11.skysail.server.security.shiro;

import org.apache.shiro.realm.jdbc.JdbcRealm;

public class SkysailAuthorizingRealm extends JdbcRealm {

    public SkysailAuthorizingRealm() {
        setAuthenticationQuery("select password from um_users where username = ?");
        setUserRolesQuery("select r.NAME  FROM um_users_um_roles ur, um_roles r, um_users u WHERE u.username = ? AND ur.SkysailUser_ID=u.ID AND r.ID=ur.roles_ID;");
    }

}
