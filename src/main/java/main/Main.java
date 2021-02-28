package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import jdbcutils.JDBCUtils;

import entry.Entry;
import event.Event;
import eventRecorder.EventRecorder;
import jsonSerializer.JsonSerializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
	final static Logger logger = LogManager.getLogger(Main.class);
	static String jdbcURL = "jdbc:hsqldb:file:db-creditsuisse-logmonitor";
	
	static List<Event> events;
	
	
	public static void main(String[] args) {
		events = Collections.synchronizedList(new ArrayList<Event>());
		String path = (args.length == 0) ? "logfile.txt" : args[0];	
		
		Thread Serialize = new Thread(new Runnable(){
			public void run(){
		
				try {
					logger.info("Beginning to read log file at " + path);
					
					InputStream inputStream = new FileInputStream(path);
					List<Entry> notMatched = JsonSerializer.readJsonStream(inputStream, events);
					logger.info("Finished parsing a log file. Left " + String.valueOf(notMatched.size()) + " entries unmatched.");
					
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		});
		Serialize.start();
		

		Thread Record = new Thread(new Runnable(){
		        public void run(){
		        	Connection c = JDBCUtils.getConnection(jdbcURL);
		    		JDBCUtils.createLogMonitorTable(c);
		    		
		    		logger.info("Beginning to add Events to the database");
		        	do {
		        		EventRecorder.RecordEvents(events, c);
		        		try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							logger.error(e.getMessage());
						}
		        	} while (Serialize.isAlive());
		        	logger.info("Finished adding Events to the database");
		        	
		        	try {
		    			c.close();
		    		} catch (SQLException e) {
		    			logger.error(e.getMessage());
		    		}
		        }
		     }
		 );
		 Record.start();
		
		
		
		
	}

}
