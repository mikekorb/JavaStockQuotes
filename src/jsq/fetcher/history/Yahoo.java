package jsq.fetcher.history;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jsq.config.Config;
import jsq.datastructes.datacontainer;
import jsq.tools.HtmlUnitTableTools;
import jsq.tools.NumberTools;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.ThreadedRefreshHandler;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

public class Yahoo extends BaseFetcher  {

	private String history = "http://ichart.finance.yahoo.com/table.csv?ignore=.csv"
			+ "&s=%1$s" 
			+ "&a=%2$s&b=%3$s&c=%4$s&d=%5$s&e=%6$s&f=%7$s&g=d"; 

	private WebClient webClient;

	
	@Override
	public String getName() {
		return "Yahoo Finance!";
	}	
	
	@Override
	public String getURL() {
		return "http://www.yahoo.de";
	}	
	
	/**
	 * 
	 * @param wkn
	 * @return
	 */
	@Override
	public void prepare(String search, int beginYear, int beginMon, int beginDay, int stopYear, int stopMon, int stopDay) {
		super.prepare(search, beginYear, beginMon, beginDay, stopYear, stopMon, stopDay);
		webClient = new WebClient();
		webClient.setCssErrorHandler(new SilentCssErrorHandler());
		webClient.setRefreshHandler(new ThreadedRefreshHandler());
		webClient.getOptions().setJavaScriptEnabled(false); 
		try {
			HtmlPage page = webClient.getPage("https://de.finance.yahoo.com/q?s=" +  URLEncoder.encode(search, "UTF-8") + "&ql=1");

			HtmlTable datatable = HtmlUnitTableTools.getTableByPartContent(page, "Ticker");
			if (datatable == null) {
				throw new IllegalStateException("Table 'Hist. Ereignisse' not found!");
			}

			List<? extends Map<String, String>> liste = HtmlUnitTableTools.analyse(datatable);

			List<Config> configs = new ArrayList<Config>();
			Config config = new Config("Handelsplatz");
			for (Map<String, String> x : liste) {
				config.addAuswahl(x.get("Börsenplatz") + " [" + x.get("Ticker") + "]",
						x.get("Ticker"));
			}
			configs.add(config);
			setConfig(configs);

		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
		} finally {
		}

	}
	@Override
	public void process(List<Config> config) {
		super.process(config);
		try {
			String ticker = URLEncoder.encode((String) config.get(0).getSelected().get(0).getObj(), "UTF-8");
			String currency = getCurrency(ticker);


			Date start = getStartdate();
			Date stop = getStopdate();
			String url = String.format(history, ticker, 
					start.getMonth(), start.getDate(), start.getYear() + 1900,
					stop.getMonth(), stop.getDate(), stop.getYear() + 1900);
			TextPage page = webClient.getPage(url);
			evalCSV(page.getContent(), currency);
		} catch (FailingHttpStatusCodeException  | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void evalCSV(String s, String defaultcur) throws IOException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
		CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',').withIgnoreEmptyLines(true);
		CSVParser parser = new CSVParser(new StringReader(s), format);
		resultQuotes = new ArrayList<datacontainer>();
		for(CSVRecord record : parser){
			datacontainer dc = new datacontainer();
			try {
				dc.data.put("date", df.parse(record.get("Date")));
				dc.data.put("first", NumberTools.stringToBigDecimal(record.get("Open")));
				dc.data.put("last", NumberTools.stringToBigDecimal(record.get("Close")));
				dc.data.put("low", NumberTools.stringToBigDecimal(record.get("Low")));
				dc.data.put("high", NumberTools.stringToBigDecimal(record.get("High")));
				dc.data.put("currency", defaultcur);
				resultQuotes.add(dc);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		parser.close();
	}


	private String getCurrency(String ticker) throws FailingHttpStatusCodeException, IOException {
		UnexpectedPage page = webClient.getPage("http://de.finance.yahoo.com/d/quotes.csv?s=" + ticker + "&f=c4");
		CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(';').withIgnoreEmptyLines(true);
		CSVParser parser = new CSVParser(new StringReader("c4\n" + page.getWebResponse().getContentAsString()), format);
		List<CSVRecord> records = parser.getRecords();
		if (records.size() == 0) {
			parser.close();
			return null;
		}
		String value = records.get(0).get("c4");
		parser.close();
		return value;
	}

	
}
