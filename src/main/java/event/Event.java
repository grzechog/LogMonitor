package event;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import entry.Entry;
import main.Main;

public class Event {

		private String id;
		private long duration;
		private String type;
		private String host;
		private boolean alert;
		
		final static Logger logger = LogManager.getLogger(Event.class);
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public long getDuration() {
			return duration;
		}

		public void setDuration(long duration) {
			this.duration = duration;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public boolean isAlert() {
			return alert;
		}

		public void setAlert(boolean alert) {
			this.alert = alert;
		}

		
		
		public Event(String id, int duration, String type, String host, boolean alert) 
		{
		  this.id = id;
		  this.duration = duration;
		  this.type = type;
		  this.host = host;
		  this.alert = alert; 
		}

		public Event(List<Entry> pair) throws InvalidEntriesException, InvalidListSizeException, InvalidStateException {
			int durationThreshold = 4;
			
			long duration = 0;
			String host;
			String type;
			String id;
			
			if (pair.size() != 2) {
				throw new InvalidListSizeException();
			}
			if (!pair.get(0).getId().equals(pair.get(1).getId())) {
				throw new InvalidEntriesException();
			}
			if (pair.get(0).getState() == pair.get(1).getState()) {
				throw new InvalidStateException();
			}	
			
			for (Entry entry : pair) { 
				host = entry.getHost();
				type = entry.getType();
				id = entry.getId();				
				
				if (entry.getState().equalsIgnoreCase("STARTED")) { duration = duration - entry.getTimestamp(); }
				if (entry.getState().equalsIgnoreCase("FINISHED")) { duration = duration + entry.getTimestamp(); }
				if (duration > durationThreshold) { this.alert = true; } else { this.alert = false; }
				
				if ( host != null && host.length() != 0) { this.host = host; }
				if ( type != null && type.length() != 0) { this.type = type; }
				if ( id != null && id.length() != 0 ) { this.id = id; }
				
	        } 
			this.duration = duration;
			logger.debug("Created Event with id: " + this.id);
		}

}