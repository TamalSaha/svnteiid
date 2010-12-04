package com.tamal.resource.adapter.svn;

import javax.resource.ResourceException;

import org.teiid.resource.spi.BasicConnection;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.tamal.translator.SVNConnection;

/**
 * Subversion connection implementation.
 */
public class SVNConnectionImpl extends BasicConnection implements SVNConnection {
	private String url;
	private String authPassword;
	private String authUserName;

	private SVNRepository repository = null;

	static {
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
		FSRepositoryFactory.setup();
	}

	public SVNConnectionImpl() {
		System.out
				.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
	}

	public SVNConnectionImpl(String url, String authPassword,
			String authUserName) {
		this.url = url;
		this.authUserName = authUserName;
		this.authPassword = authPassword;
	}

	@Override
	public void close() throws ResourceException {

	}

	@Override
	public SVNRepository getRepository() throws ResourceException {
		if (repository == null) {
			try {
				repository = SVNRepositoryFactory.create(SVNURL
						.parseURIEncoded(url));
			} catch (SVNException svne) {
				String msg = "error while creating an SVNRepository for the location '"
						+ url + "': " + svne.getMessage();
				System.err.println(msg);
				throw new ResourceException(msg);
			}

			ISVNAuthenticationManager authManager = SVNWCUtil
					.createDefaultAuthenticationManager(authUserName,
							authPassword);
			repository.setAuthenticationManager(authManager);
		}
		return repository;
	}
}