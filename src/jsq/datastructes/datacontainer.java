package jsq.datastructes;

import java.util.HashMap;

public class datacontainer {

	public HashMap<String, Object> data = new HashMap<String, Object>();
	
	public String toString() {
		return data.entrySet().toString();
	}
}
