package com.tamal.translator;

import java.util.List;

import org.teiid.language.Select;
import org.teiid.metadata.RuntimeMetadata;
import org.teiid.translator.DataNotAvailableException;
import org.teiid.translator.TranslatorException;

public interface BasicSelectResultSetExecution<Connection> {

	public void cancel() throws TranslatorException;

	public void close();

	public void execute(Select command, RuntimeMetadata metadata,
			Connection connection) throws TranslatorException;

	public List<?> next() throws TranslatorException, DataNotAvailableException;
}
