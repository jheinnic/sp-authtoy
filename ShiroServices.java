package de.twenty11.skysail.server.security.shiro;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.realm.AuthorizingRealm;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.restlet.Context;
import org.restlet.security.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.twenty11.skysail.server.Constants;
import de.twenty11.skysail.server.config.ServerConfiguration;
import de.twenty11.skysail.server.security.AuthenticationService;
import de.twenty11.skysail.server.security.shiro.mgt.SkysailWebSecurityManager;
import de.twenty11.skysail.server.security.shiro.restlet.ShiroDelegationAuthenticator;

/**
 * Default AuthenticationService Implementation shipped with skysail
 * 
 */
public abstract class ShiroServices implements AuthenticationService {

    public static final String SKYSAIL_SHIRO_DB_REALM = "skysail.shiro.db.realm";
    private static final Logger logger = LoggerFactory.getLogger(ShiroServices.class);

    private ServerConfiguration serverConfig;
    private BundleContext bundleContext;
    private DataSource dataSource;
    private final List<DataSourceFactory> dataSourceFactories = new ArrayList<DataSourceFactory>();

    private AuthorizingRealm authorizingRealm;

    // used by OSGi
    public ShiroServices() {
    }

    public ShiroServices(AuthorizingRealm authorizingRealm) {
        this.authorizingRealm = authorizingRealm;
        init();
    }

    public void init() {
        logger.info("initializing {}", this.getClass().getSimpleName());
        if (authorizingRealm == null) {
            authorizingRealm = new SkysailAuthorizingRealm();
            ((SkysailAuthorizingRealm) authorizingRealm).setDataSource(getDataSourceFromConfig());
        }

        logger.info("Creating new SkysailWebSecurityManager...");
        SkysailWebSecurityManager securityManager = new SkysailWebSecurityManager(authorizingRealm);

        logger.info("Setting new SkysailWebSecurityManager as Shiros SecurityManager");
        SecurityUtils.setSecurityManager(securityManager);

    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private DataSource getDataSourceFromConfig() {
        if (serverConfig == null) {
            return null;
        }
        String driver = serverConfig.getConfigForKey(Constants.SKYSAIL_JDBC_DRIVER);

        Properties props = new Properties();
        props.put(DataSourceFactory.JDBC_PASSWORD, serverConfig.getConfigForKey(Constants.SKYSAIL_JDBC_PASSWORD));
        props.put(DataSourceFactory.JDBC_URL, serverConfig.getConfigForKey(Constants.SKYSAIL_JDBC_URL));
        props.put(DataSourceFactory.JDBC_USER, serverConfig.getConfigForKey(Constants.SKYSAIL_JDBC_USER));

        ServiceReference<?>[] allDatasourceFactoryServiceReferences;
        try {
            allDatasourceFactoryServiceReferences = bundleContext.getAllServiceReferences(
                    "org.osgi.service.jdbc.DataSourceFactory", null);
            if (allDatasourceFactoryServiceReferences == null) {
                return null;
            }
            for (ServiceReference<?> sr : allDatasourceFactoryServiceReferences) {
                String driverProperty = (String) sr.getProperty("osgi.jdbc.driver.class");
                if (driver.equals(driverProperty)) {
                    DataSourceFactory dsf = (DataSourceFactory) bundleContext.getService(sr);
                    return dsf.createDataSource(props);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Authenticator getAuthenticator(Context context) {
        // https://github.com/qwerky/DataVault/blob/master/src/qwerky/tools/datavault/DataVault.java
        return new ShiroDelegationAuthenticator(context, SKYSAIL_SHIRO_DB_REALM, "thisHasToBecomeM".getBytes());
    }

    public void setServerConfig(ServerConfiguration serverConfig) {
        this.serverConfig = serverConfig;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void registerDSF(DataSourceFactory dsf) {
        this.dataSourceFactories.add(dsf);
        if (dataSource != null) {
            return;
        }
        init();
    }

    public void unregisterDSF(DataSourceFactory dsf) {

    }

}
