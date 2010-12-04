package com.tamal.resource.adapter.svn;

import javax.resource.ResourceException;

import org.teiid.resource.spi.BasicConnection;
import org.teiid.resource.spi.BasicConnectionFactory;

public class SVNConnectionFactory extends BasicConnectionFactory {

	private static final long serialVersionUID = 6984723868547902176L;

	private SVNManagedConnectionFactory mcf;

	SVNConnectionFactory(SVNManagedConnectionFactory mcf) {
		this.mcf = mcf;
		System.out
		.println("yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
	}

	@Override
	public BasicConnection getConnection() throws ResourceException {
		return new SVNConnectionImpl(mcf.getURL(), mcf.getAuthUserName(),
				mcf.getAuthPassword());
	}
}