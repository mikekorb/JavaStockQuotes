package jsq.datastructes;

import java.util.HashMap;

public class Datacontainer {

	public HashMap<String, Object> data = new HashMap<String, Object>();
	
	public void put(String key, Object value) {
		data.put(key, value);
	}
	
	public String toString() {
		return data.entrySet().toString();
	}
}
