import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.*;



import event.Event;
import jdbcutils.JDBCUtils;
import eventRecorder.EventRecorder;

public class RecordTest {
	
	public static Connection con;
	public static List<Event> testEvents;
	
    public static List<Event> generateData() {
        List<Event> events = new ArrayList<Event>();
        events.add(new Event("1000000", 3, "DB", "127.0.0.1", false));
        events.add(new Event("1000001", 9, "DB", "CreditSuisse.db", true));
        events.add(new Event("1000002", 7, "APP", "CreditSuisse.billing", true));
        events.add(new Event("1000003", 12, "APP", "CreditSuisse.crmproxy", true));
        
        return events;
    }
	
	
	public static Connection getConnection() {
		String jdbcUrl = "jdbc:hsqldb:mem:testDB";
		Connection connection = JDBCUtils.getConnection(jdbcUrl);
		return connection;
	}
	
	@BeforeClass
	public static void prepareDatabase() {
		testEvents = generateData();
		con = getConnection();
	}
	
	@Test
	public void RecordEventsTestCase() {
		
		JDBCUtils.createLogMonitorTable(con);
		EventRecorder.RecordEvents(testEvents, con);
		
	}
	
	@AfterClass
	public static void showEntries() {
		ResultSet queryResult = JDBCUtils.queryLogMonitorDatabase(con, "");
		
		try {
			while(queryResult.next()) {
			    System.out.println("Event ID: " + queryResult.getString("id") + " | host: " + queryResult.getString("host") + 
			    		" | type: " + queryResult.getString("type") + " | duration: " + queryResult.getString("duration"));
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}
	
	@AfterClass
	public static void closeConnection() {
		try {
			con.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
}
