package eventRecorder;

import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import jdbcutils.JDBCUtils;

import event.Event;

public class EventRecorder {

	public static void RecordEvents(List<Event> events, Connection con) {
		
		List<Event> eventsListCopy;
		
		synchronized (events) {
			eventsListCopy = new ArrayList<Event>(events);
		    events.removeAll(events);
		}
		
		Iterator<Event> itr = eventsListCopy.listIterator();  
        while(itr.hasNext()){
        	 
        	Event toAdd = itr.next();
			JDBCUtils.insertLogMonitorEvent(con, toAdd);
			
		}
				
	}

}
