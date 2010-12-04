package com.tamal.translator.svn;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.tamal.translator.BasicSelectResultSetExecution;
import com.tamal.translator.SVNConnection;

public class ChangeSetResultSetExecution implements
		BasicSelectResultSetExecution<SVNConnection> {

	private ArrayList<Column> cols = new ArrayList<Column>();
	private Collection logEntries = null;
	private Iterator entries = null;
	private SVNLogEntry logEntry;
	private Iterator changedPaths = null;
	private SVNRepository repository;

	private void reset() {
		cols.clear();
		logEntries = null;
		entries = null;
		logEntry = null;
		changedPaths = null;
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
			repository = connection.getRepository();
			long startRevision = 0;
			long endRevision = repository.getLatestRevision();

			logEntries = repository.log(new String[] { "" }, null,
					startRevision, endRevision, true, true);
			entries = logEntries.iterator();
			if (entries.hasNext()) {
				logEntry = (SVNLogEntry) entries.next();
				changedPaths = logEntry.getChangedPaths().keySet().iterator();
			} else {
				changedPaths = Collections.EMPTY_LIST.iterator();
			}
		} catch (Exception e) {
			throw new TranslatorException(e);
		}
	}

	@Override
	public List<?> next() throws TranslatorException, DataNotAvailableException {
		if (entries == null || changedPaths == null)
			return null;
		while (!changedPaths.hasNext()) {
			if (entries.hasNext()) {
				logEntry = (SVNLogEntry) entries.next();
				changedPaths = logEntry.getChangedPaths().keySet().iterator();
			} else
				return null;
		}

		ArrayList<Object> row = new ArrayList<Object>();
		SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry
				.getChangedPaths().get(changedPaths.next());

		for (Column col : cols) {
			String colName = col.getName();
			if (colName.equalsIgnoreCase(SVNConstants.C_CHNAGESET_REV)) {
				row.add(logEntry.getRevision());
			} else if (colName.equalsIgnoreCase(SVNConstants.C_CHNAGESET_PATH)) {
				row.add(entryPath.getPath());
			} else if (colName.equalsIgnoreCase(SVNConstants.C_CHNAGESET_CT)) {
				row.add("" + entryPath.getType());
			} else if (colName
					.equalsIgnoreCase(SVNConstants.C_CHNAGESET_CONTENT)) {
				row.add(getContent(entryPath));
			}
		}
		return row;
	}

	private String getContent(SVNLogEntryPath entryPath)
			throws TranslatorException {
		SVNProperties fileProperties = new SVNProperties();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			SVNNodeKind nodeKind = entryPath.getKind();
			if (nodeKind == SVNNodeKind.NONE) {
				return "";
			} else if (nodeKind == SVNNodeKind.DIR) {
				return "";
			}
			repository.getFile(entryPath.getPath(), -1, fileProperties, baos);
		} catch (SVNException svne) {
			throw new TranslatorException(svne);
		}
		String mimeType = fileProperties.getStringValue(SVNProperty.MIME_TYPE);
		if (SVNProperty.isTextMimeType(mimeType)) {
			return baos.toString();
		} else
			return mimeType;
	}
}