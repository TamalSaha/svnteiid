package com.tamal.translator.svn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.teiid.language.ColumnReference;
import org.teiid.language.DerivedColumn;
import org.teiid.language.Expression;
import org.teiid.language.Select;
import org.teiid.metadata.Column;
import org.teiid.metadata.RuntimeMetadata;
import org.teiid.translator.DataNotAvailableException;
import org.teiid.translator.TranslatorException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.tamal.translator.BasicSelectResultSetExecution;
import com.tamal.translator.SVNConnection;

public class HistoryResultSetExecution implements
		BasicSelectResultSetExecution<SVNConnection> {

	private ArrayList<Column> cols = new ArrayList<Column>();
	private Collection logEntries = null;
	private Iterator entries = null;

	private void reset() {
		cols.clear();
		logEntries = null;
		entries = null;
	}

	@Override
	public void cancel() throws TranslatorException {
		reset();
	}

	@Override
	public void close() {
		reset();
	}

	@Override
	public void execute(Select command, RuntimeMetadata metadata,
			SVNConnection connection) throws TranslatorException {

		reset();

		// check for column names
		for (DerivedColumn symbol : command.getDerivedColumns()) {
			Expression expr = symbol.getExpression();
			if (expr instanceof ColumnReference) {
				cols.add(((ColumnReference) expr).getMetadataObject());
			} else {
				throw new TranslatorException(
						"Invalid select symbol: " + expr.toString()); //$NON-NLS-1$
			}
		}

		try {
			SVNRepository repository = connection.getRepository();
			long startRevision = 0;
			long endRevision = repository.getLatestRevision();

			logEntries = repository.log(new String[] { "" }, null,
					startRevision, endRevision, true, true);
			entries = logEntries.iterator();
		} catch (Exception e) {
			throw new TranslatorException(e);
		}
	}

	@Override
	public List<?> next() throws TranslatorException, DataNotAvailableException {
		if (entries == null || !entries.hasNext())
			return null;

		ArrayList<Object> row = new ArrayList<Object>();
		SVNLogEntry logEntry = (SVNLogEntry) entries.next();

		for (Column col : cols) {
			String colName = col.getName();
			if (colName.equalsIgnoreCase(SVNConstants.C_HISTORY_REV)) {
				row.add(logEntry.getRevision());
			} else if (colName.equalsIgnoreCase(SVNConstants.C_HISTORY_AUTH)) {
				row.add(logEntry.getAuthor());
			} else if (colName.equalsIgnoreCase(SVNConstants.C_HISTORY_DATE)) {
				row.add(logEntry.getDate());
			} else if (colName.equalsIgnoreCase(SVNConstants.C_HISTORY_LOG)) {
				row.add(logEntry.getMessage());
			}
		}
		return row;
	}
}