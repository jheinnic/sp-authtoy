package de.twenty11.skysail.server.security.shiro;

import org.apache.shiro.mgt.DefaultSubjectDAO;

public class SkysailSubjectDAO extends DefaultSubjectDAO {

    public SkysailSubjectDAO() {
        System.out.println("hier");
    }
}
