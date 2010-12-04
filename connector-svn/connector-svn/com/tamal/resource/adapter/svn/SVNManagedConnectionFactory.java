package com.tamal.resource.adapter.svn;

import javax.resource.ResourceException;

import org.teiid.resource.spi.BasicConnectionFactory;
import org.teiid.resource.spi.BasicManagedConnectionFactory;

public class SVNManagedConnectionFactory extends BasicManagedConnectionFactory {

	private static final long serialVersionUID = -111732520431104960L;

	private String url;
	private String authPassword;
	private String authUserName;

	public BasicConnectionFactory createConnectionFactory()
			throws ResourceException {
		System.out.print("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		SVNConnectionFactory ff =  new SVNConnectionFactory(SVNManagedConnectionFactory.this);
		System.out.print("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
		return ff;
	};

	public String getAuthPassword() {
		return this.authPassword;
	}

	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}

	public String getAuthUserName() {
		return this.authUserName;
	}

	public void setAuthUserName(String authUserName) {
		this.authUserName = authUserName;
	}

	public String getURL() {
		return this.url;
	}

	public void setURL(String url) {
		this.url = url;
	}
}
