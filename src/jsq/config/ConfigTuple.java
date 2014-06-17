package jsq.config;

public class ConfigTuple { 
	private final String description; 
	private final Object obj;
	
	public String getDescription() {
		return description;
	}

	public Object getObj() {
		return obj;
	}

	public ConfigTuple(String desc, Object obj) { 
		this.description = desc;
		this.obj = obj;
	} 
	
	@Override
	public String toString() {
		return description;
	}

} 