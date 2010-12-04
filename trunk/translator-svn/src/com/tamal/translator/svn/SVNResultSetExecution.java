package com.tamal.translator.svn;

import java.util.List;

import org.teiid.language.NamedTable;
import org.teiid.language.Select;
import org.teiid.logging.LogConstants;
import org.teiid.logging.LogManager;
import org.teiid.metadata.RuntimeMetadata;
import org.teiid.translator.DataNotAvailableException;
import org.teiid.translator.ResultSetExecution;
import org.teiid.translator.TranslatorException;

import com.tamal.translator.BasicSelectResultSetExecution;
import com.tamal.translator.SVNConnection;

public class SVNResultSetExecution implements ResultSetExecution {

	private BasicSelectResultSetExecution<SVNConnection> handler = null;
	private HistoryResultSetExecution historyHandler = new HistoryResultSetExecution();
	private ChangeSetResultSetExecution changeSetHandler = new ChangeSetResultSetExecution();

	// Connector resources
	private RuntimeMetadata metadata;
	private Select command;
	private SVNConnection connection;

	public SVNResultSetExecution(Select command, RuntimeMetadata metadata,
			SVNConnection connection) {
		this.command = command;
		this.metadata = metadata;
		this.connection = connection;
	}

	@Override
	public void cancel() throws TranslatorException {
		if (handler != null)
			handler.cancel();
	}

	@Override
	public void close() {
		if (handler != null)
			handler.close();
	}

	@Override
	public void execute() throws TranslatorException {
		// Log our command
		LogManager.logTrace(LogConstants.CTX_CONNECTOR,
				"SVN executing command: " + command); //$NON-NLS-1$

		// check whether we can handle command like this
		// No where, group by, having, order by

		if (command.getWhere() != null || command.getGroupBy() != null
				|| command.getHaving() != null || command.getOrderBy() != null)
			throw new TranslatorException(
					"Unsupported where/group by/having/order by clause");

		// Only one Namedtable, no join or derived tables
		NamedTable table = null;
		if (command.getFrom() != null && command.getFrom().size() == 1
				&& command.getFrom().get(0) instanceof NamedTable)
			table = (NamedTable) command.getFrom().get(0);
		else
			throw new TranslatorException("Unsupported From clause");

		// set correct handler
		if (table.getName().equalsIgnoreCase(SVNConstants.T_HISTORY)) {
			handler = historyHandler;
		} else if (table.getName().equalsIgnoreCase(SVNConstants.T_CHNAGESET)) {
			System.out.println("changeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandlerchangeSetHandler");
			handler = changeSetHandler;
		} else {
			handler = null;
			throw new TranslatorException("unknown Table: " + table.getName());
		}
		handler.execute(command, metadata, connection);
	}

	@Override
	public List<?> next() throws TranslatorException, DataNotAvailableException {
		return handler.next();
	}
}
