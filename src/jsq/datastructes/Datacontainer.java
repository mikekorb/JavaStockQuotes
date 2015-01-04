package jsq.datastructes;

import java.util.HashMap;
import java.util.Map;

public class Datacontainer {

	public Map<String, Object> data;
	
	public Datacontainer() {
		data = new HashMap<String, Object>();
	}
	
	public Datacontainer(Map<String, Object> data) {
		this.data = data;
	}
	
	public void put(String key, Object value) {
		data.put(key, value);
	}
	
	public String toString() {
		return data.entrySet().toString();
	}
	
	public Map<String, Object> getMap() {
		return data;
	}
}
