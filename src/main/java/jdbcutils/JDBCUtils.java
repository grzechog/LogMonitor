package jdbcutils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import event.Event;

public class JDBCUtils {
	final static Logger logger = LogManager.getLogger(JDBCUtils.class);
	
    private static String jdbcUsername = "CreditSuisseDBA";
    private static String jdbcPassword = "NobodyWouldGuess";

    public static Connection getConnection(String jdbcUrl) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
        } catch (SQLException e) {      
        	logger.error(e.getMessage());
        }
        return connection;
    }

    public static void printSQLException(SQLException ex) {
        for (Throwable e: ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                logger.error("SQLState: " + ((SQLException) e).getSQLState());
                logger.error("Error Code: " + ((SQLException) e).getErrorCode());
                logger.error("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }

    public static void createLogMonitorTable(Connection con) {
        Statement stmt = null;
        int result = 0;
        
    	try {
    		logger.debug("Creating table in the database if not existent.");
			stmt = con.createStatement();
	        result = stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Event ( " + 
	                " id VARCHAR(64) NOT NULL, host VARCHAR(64), " + 
	                " type VARCHAR(128), duration INT, alert BIT," +
	                " PRIMARY KEY (id))");
		} catch (SQLException e) {
			
			logger.error(e.getMessage());
		}

    }
    
    public static void insertLogMonitorEvent(Connection con, Event event) {
        Statement stmtObj = null;
        
        String alert = String.valueOf((event.isAlert()) ? 1 : 0);
        String id = "'" + event.getId() + "'";
        String host = (event.getHost() == null) ? "null" : "'" + event.getHost() + "'";
        String type = (event.getType() == null) ? "null" : "'" + event.getType() + "'";
        String duration = String.valueOf(event.getDuration());
        
        String statement = "INSERT INTO Event " +
				"VALUES (" +
				id + 
				", " +
				host +
				", " +
				type +
				", " + 
				duration +
				", " + 
				alert +
				")";

    	try {
    		stmtObj = con.createStatement();
    		stmtObj.executeUpdate(statement); 
    		con.commit();
    		logger.debug("Succesfully recorded event " + id + " in the database");
    		stmtObj.close();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}

    }

    public static ResultSet queryLogMonitorDatabase(Connection con, String whereClause) {
    	Statement stmtObj = null;
    	ResultSet result = null;
    	String whereClauseNotNull = (whereClause == null) ? "" : whereClause;
    	String query = "SELECT id, type, host, duration, alert FROM Event " + whereClauseNotNull;
    	
    	try {
			stmtObj = con.createStatement();
    		result = stmtObj.executeQuery(query);
		} catch (SQLException e) {
			
			logger.error(e.getMessage());
			
		}
    	return result;
    }
    
}
