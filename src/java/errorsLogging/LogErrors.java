
package errorsLogging;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class allows to log errors into a file.
 * @author durban
 */
public class LogErrors {

    private static LogErrors instance;
    private static String LOG_DIR_PATH = "";
    private static final String LOG_FILE_NAME = "projectManagementLog.txt";
    private static Logger logger;

    /**
     * Returns the unique instance.
     * @return the unique instance.
     */
    public static LogErrors getInstance() {
        if(instance == null) {
            instance = new LogErrors();
        }
        return instance;
    }

    /**
     * Append an info message to the logger.
     * @param msg
     */
    public void appendInfoMessage(String msg) {
        logger.info(msg);
    }

    /**
     * Append a log message to the logger with the given level.
     * @param msg
     */
    public void appendLogMessage(String msg, Level level) {
        logger.logp(level, getCallingClassName(2), getCallingMethodName(2), msg);
    }

    /**
     * Append a log message to the logger. Use {x} to specify the parameters
     * location (x being the parameter number).
     * @param msg
     */
    public void appendLogMessage(String msg, Level level, Object param) {
        logger.logp(level, getCallingClassName(2), getCallingMethodName(2), msg, param);
    }

    /**
     * Append a log message to the logger. Use {x} to specify the parameters
     * location (x being the parameter number).
     * @param msg
     */
    public void appendLogMessage(String msg, Level level, Object[] params) {
        logger.logp(level, getCallingClassName(2), getCallingMethodName(2) ,msg, params );
    }

    /**
     * Basic constructor building the logger and adding the file header.
     */
    private LogErrors() {
        LOG_DIR_PATH = getRootPath();
        prepareLogFile();
        logger = Logger.getLogger("projectManagement");
        try {
            Handler handler = new FileHandler(LOG_DIR_PATH+File.separator+LOG_FILE_NAME, true);
            logger.addHandler(handler);
        } catch (IOException ex) {
            Logger.getLogger(LogErrors.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(LogErrors.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Gives the current root path of the attachments files,
     * based on the properties file.
     * @return the current root path of the attachments files
     */
    private static String getRootPath() {
        Properties prop = new Properties();
        String path;
        try {
    		prop.load(LogErrors.class.getResourceAsStream("logProperties.properties"));
                path = prop.getProperty("root_path");
    	} catch (Exception ex) {
    		path = "";
        }
        return path;
    }

    /**
     * Creates the log directory and file, if needed.
     */
    private static void prepareLogFile() {
        File dir = new File(LOG_DIR_PATH);
        if(!dir.exists() || !dir.isDirectory()) {
            dir.mkdir();
        }
        dir = new File(LOG_DIR_PATH+File.separator+LOG_FILE_NAME);
        if(!dir.exists() || !dir.isFile()) {
            try {
                dir.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(LogErrors.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Returns the name of the method calling the logger.
     * @param parentsCount number of methods before (already count this one).
     * @return the name of the method calling the logger
     */
    private static String getCallingMethodName(int parentsCount) {
        return Thread.currentThread().getStackTrace()[1+parentsCount].getMethodName();
    }

    /**
     * Returns the name of the class calling the logger.
     * @param parentsCount number of methods before (already count this one).
     * @return the name of the class calling the logger
     */
    private static String getCallingClassName(int parentCounts) {
        return Thread.currentThread().getStackTrace()[1+parentCounts].getClassName();
    }
}
