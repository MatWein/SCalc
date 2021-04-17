package scalc.internal;

import scalc.SCalcOptions;

public class SCalcLogger {
	public static void debug(SCalcOptions<?> options, String message, Object... params) {
		if (!options.isDebug()) {
			return;
		}
		
		String messageToLog = String.format(message, params);
		options.getDebugLogger().accept(messageToLog);
	}
}
