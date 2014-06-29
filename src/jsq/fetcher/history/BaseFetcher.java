package jsq.fetcher.history;
import java.util.Date;
import java.util.List;

import jsq.config.Config;
import jsq.datastructes.Datacontainer;


public abstract class BaseFetcher {


	private List<Datacontainer> resultEvents;
	private List<Datacontainer> resultQuotes;
	private Datacontainer stockDetails;
	
	public Datacontainer getStockDetails() {
		return stockDetails;
	}

	public void setStockDetails(Datacontainer stockDetails) {
		this.stockDetails = stockDetails;
	}

	private List<Config> options = null;
	protected Date startdate;
	protected Date stopdate;

	public abstract String getName();
	
	public abstract String getURL();
	

	@SuppressWarnings("deprecation")
	public void prepare(String search, int beginYear, int beginMon, int beginDay, int stopYear, int stopMon, int stopDay) throws Exception {
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
		resultEvents = null;
		stockDetails = null;
	}
	
	public List<Datacontainer> getHistQuotes() {
		return resultQuotes;
	}

	public void setHistQuotes(List<Datacontainer> res) {
		resultQuotes = res;
	}
	
	

	public List<Datacontainer> getHistEvents() {
		return resultEvents;
	}

	public void setHistEvents(List<Datacontainer> resultEvents) {
		this.resultEvents = resultEvents;
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
