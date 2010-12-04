package com.tamal.translator;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;

import org.tmatesoft.svn.core.io.SVNRepository;

public interface SVNConnection extends Connection {

	public SVNRepository getRepository() throws ResourceException;
}
