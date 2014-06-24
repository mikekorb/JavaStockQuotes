package jsq.fetcher.history;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jsq.config.Config;
import jsq.datastructes.datacontainer;


public abstract class BaseFetcher {


	protected List<datacontainer> resultQuotes;
	private List<Config> options = null;
	protected Date startdate;
	protected Date stopdate;

	public abstract String getName();
	
	public abstract String getURL();
	

	public void prepare(String search, int beginYear, int beginMon, int beginDay, int stopYear, int stopMon, int stopDay) {
		reset();
		startdate = new Date(beginYear-1900, beginMon - 1, beginDay);
		stopdate = new Date(stopYear-1900, stopMon - 1, stopDay);
	}


	public boolean hasMoreConfig() {
		return options != null;
	}
	
	public List<Config> getConfigs() {
		if (options == null) {
			throw  new IllegalStateException("No Configs found!");
		}
		return options;
	}
	
	public void process(List<Config> options) {
		setConfig(null);
	}

	protected void setConfig(List<Config> options) {
		this.options = options;
	}

	
	protected void reset() {
		options = null;
		startdate = null;
		stopdate = null;
		resultQuotes = null;
	}
	
	public List<datacontainer> getResult() {
		return resultQuotes;
	}

	public void setResult(List<datacontainer> res) {
		resultQuotes = res;
	}

	public String toString() {
		return getName();
	}

	public Date getStartdate() {
		return startdate;
	}

	public Date getStopdate() {
		return stopdate;
	}
	
}
