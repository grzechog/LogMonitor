package jsonSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import entry.Entry;
import event.Event;
import jdbcutils.JDBCUtils;

public class JsonSerializer {
	final static Logger logger = LogManager.getLogger(JsonSerializer.class);
	
	public static List<Entry> readJsonStream(InputStream in, List<Event> events) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		List<Entry> entries = new ArrayList<Entry>();
        reader.setLenient(true);
        Gson gson = new Gson();
        
        while (reader.peek() != JsonToken.END_DOCUMENT) {
            try {
            	Entry entry = gson.fromJson(reader, Entry.class);
            	entries.add(entry);
            	List<Entry> matching = entries.stream().filter(c -> c.getId().equals(entry.getId())).collect(Collectors.toList());

            	if (matching.size() == 2) { 
            		synchronized(events){
            				events.add(new Event(matching));
            		}
            		for (Entry matchingEntry : matching) { 
            			entries.remove(matchingEntry);
            		}
            	
            	}
            }
            catch (Exception e){
            	logger.error(e.getMessage());
            }
        }

        reader.close();
        return entries;
    }
	
}
