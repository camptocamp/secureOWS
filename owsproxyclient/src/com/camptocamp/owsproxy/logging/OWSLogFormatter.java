/**
 * 
 */
package com.camptocamp.owsproxy.logging;

import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * A formatter that formats information from the different logs in different ways.
 * 
 * <p>For example User logs are formatted very simply, just message - time.  But the DEV logs are formatted using the {@link SimpleFormatter}
 * 
 * @author jeichar
 */
public class OWSLogFormatter extends Formatter {

	Formatter defaultFormatter = new SimpleFormatter();
	
	@Override
	public String format(LogRecord record) {
		OWSLogger log = OWSLogger.lookup(record.getLoggerName());
		if(log==null){
			return record.toString();
		} else {
			return format(log, record);
		}
	}

	private String format(OWSLogger log, LogRecord record) {
		switch (log) {
		case USER:
			Date date = new Date(record.getMillis());
			String time = DateFormat.getTimeInstance().format(date);
			return record.getMessage()+" - "+ time +"\n" ; //$NON-NLS-1$ //$NON-NLS-2$

		default:
			return defaultFormatter.format(record);
		}
	}

}
