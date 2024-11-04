package na.kondratev.security_oauth2.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GlobalLogger {
    private static final Logger logger = LoggerFactory.getLogger(GlobalLogger.class);

    public void logInfo(String message) {
        logger.info(message);
    }

    public void logError(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

}
