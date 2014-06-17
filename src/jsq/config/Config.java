package jsq.config;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class Config {
	private List<ConfigTuple> auswahlen;
	private String beschreibung;
	
	private List<ConfigTuple> selectedOptions;
	
	public Config(String beschreibung) {
		auswahlen = new ArrayList<ConfigTuple>();
		selectedOptions = new ArrayList<ConfigTuple>();
		this.beschreibung = beschreibung;
	}
	
	public String getBeschreibung() {
		return beschreibung;
	}

	public void addAuswahl(ConfigTuple s) {
		auswahlen.add(s);
	};
	
	public void addAuswahl(String description, Object s) {
		auswahlen.add(new ConfigTuple(description, s));
	};
	
	public void addSelectedOptions(ConfigTuple opt) {
		selectedOptions.add(opt);
	}
	

	public List<ConfigTuple> getOptions() {
		return Collections.unmodifiableList(auswahlen);
	}
	
	public List<ConfigTuple> getSelected() {
		return Collections.unmodifiableList(selectedOptions);
	}

	public String toString() {
		return beschreibung + ": " + auswahlen + " / " + selectedOptions;
	}
	
}
