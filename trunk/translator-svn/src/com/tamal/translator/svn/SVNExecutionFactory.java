package com.tamal.translator.svn;

import java.util.Collections;
import java.util.List;

import javax.resource.cci.ConnectionFactory;

import org.teiid.core.types.DataTypeManager;
import org.teiid.language.QueryExpression;
import org.teiid.language.Select;
import org.teiid.metadata.MetadataFactory;
import org.teiid.metadata.RuntimeMetadata;
import org.teiid.metadata.Table;
import org.teiid.translator.ExecutionContext;
import org.teiid.translator.ExecutionFactory;
import org.teiid.translator.ResultSetExecution;
import org.teiid.translator.Translator;
import org.teiid.translator.TranslatorException;

import com.tamal.translator.SVNConnection;

@Translator(name = "svn", description = "SVN Translator, reads contents of subversion repository")
public class SVNExecutionFactory extends
		ExecutionFactory<ConnectionFactory, SVNConnection> {

	public SVNExecutionFactory() {
	}

	@Override
	public ResultSetExecution createResultSetExecution(QueryExpression command,
			ExecutionContext executionContext, RuntimeMetadata metadata,
			SVNConnection connection) throws TranslatorException {
		if (command instanceof Select)
			return new SVNResultSetExecution((Select) command, metadata,
					connection);
		else
			throw new TranslatorException("Only supports select query");
	}

	@Override
	public List<String> getSupportedFunctions() {
		return Collections.emptyList();
	}

	@Override
	public void getMetadata(MetadataFactory metadataFactory, SVNConnection conn)
			throws TranslatorException {
		Table history = metadataFactory.addTable(SVNConstants.T_HISTORY); //$NON-NLS-1$
		metadataFactory.addColumn(SVNConstants.C_HISTORY_REV,
				DataTypeManager.DefaultDataTypes.INTEGER, history); //$NON-NLS-1$
		metadataFactory.addColumn(SVNConstants.C_HISTORY_AUTH,
				DataTypeManager.DefaultDataTypes.STRING, history); //$NON-NLS-1$
		metadataFactory.addColumn(SVNConstants.C_HISTORY_DATE,
				DataTypeManager.DefaultDataTypes.TIMESTAMP, history); //$NON-NLS-1$
		metadataFactory.addColumn(SVNConstants.C_HISTORY_LOG,
				DataTypeManager.DefaultDataTypes.STRING, history); //$NON-NLS-1$
		//metadataFactory.addAccessPattern("needs_symbol", Arrays.asList("symbol"), t); //$NON-NLS-1$ //$NON-NLS-2$

		Table changeSet = metadataFactory.addTable(SVNConstants.T_CHNAGESET); //$NON-NLS-1$
		metadataFactory.addColumn(SVNConstants.C_CHNAGESET_REV,
				DataTypeManager.DefaultDataTypes.INTEGER, changeSet); //$NON-NLS-1$
		metadataFactory.addColumn(SVNConstants.C_CHNAGESET_PATH,
				DataTypeManager.DefaultDataTypes.STRING, changeSet); //$NON-NLS-1$
		metadataFactory.addColumn(SVNConstants.C_CHNAGESET_CT,
				DataTypeManager.DefaultDataTypes.STRING, changeSet); //$NON-NLS-1$
		metadataFactory.addColumn(SVNConstants.C_CHNAGESET_CONTENT,
				DataTypeManager.DefaultDataTypes.STRING, changeSet); //$NON-NLS-1$
		//metadataFactory.addAccessPattern("needs_symbol", Arrays.asList("symbol"), changeSet); //$NON-NLS-1$ //$NON-NLS-2$
	}

	// capabilities

	@Override
	public boolean areLobsUsableAfterClose() {
		return true;
	}

	@Override
	public boolean isSourceRequired() {
		return false;
	}

	// assuming everything false by default
}
