package entry;

import com.google.gson.annotations.SerializedName;

public class Entry {
	@SerializedName("id")
	private String id;
	@SerializedName("state")
	private String state;
	@SerializedName("timestamp")
	private long timestamp;
	@SerializedName("type")
	private String type;
	@SerializedName("host")
	private String host;
	
	public Entry(String id, String state, long timestamp, String type, String host) {
	  this.id = id;
	  this.state = state;
	  this.timestamp = timestamp;
	  this.type = type;
	  this.host = host;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
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
	
}
