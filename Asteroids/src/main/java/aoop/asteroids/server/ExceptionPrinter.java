package aoop.asteroids.server;

/**
 * Class used to print exception messages
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExceptionPrinter {
	
	private static final Logger logger = LoggerFactory.getLogger(ExceptionPrinter.class); 
	
	private ExceptionPrinter() {}
	
	public static void print(String msg, Exception e) {
		logger.warn(msg);
		logger.info("Because: " + e.getCause());
		logger.info("More information: " + e.getMessage() + "\n" + e.fillInStackTrace());
		System.out.println();
	}
}
