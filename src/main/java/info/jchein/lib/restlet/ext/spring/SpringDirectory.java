package info.jchein.lib.restlet.ext.spring;

import org.restlet.Restlet;
import org.restlet.resource.Directory;

public class SpringDirectory extends Directory {
	public SpringDirectory( final Restlet restlet, final String rootUri ) {
	    super( restlet.getContext(), rootUri );
	}
}
