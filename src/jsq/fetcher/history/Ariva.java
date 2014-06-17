package jsq.fetcher.history;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import jsq.config.Config;
import jsq.config.ConfigTuple;
import jsq.datastructes.datacontainer;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.ThreadedRefreshHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;


public class Ariva extends BaseFetcher {
	String[] cfg = new String[] {"Handelsplatz", "Währung"};

	private WebClient webClient;

	private String defaultCurrency;

	private HtmlPage page;
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
			page = webClient.getPage("http://www.ariva.de/");
			List<HtmlTextInput> x = (List<HtmlTextInput>) page.getByXPath("//input[@id='livesearch']");
			HtmlTextInput input = x.get(0); 
			input.setValueAttribute(search);

			List<HtmlSubmitInput> y = (List<HtmlSubmitInput>) page.getByXPath("//input[@id='go']");
			page = y.get(0).click();

			HtmlAnchor a = null;
			for (HtmlAnchor aa : page.getAnchors()) {
				if (aa.getAttribute("href").contains("/kurs")) {
					a = aa;
					break;
				}
			}
			page = a.click();


			// Link "Kurse"
			a = null;
			for (HtmlAnchor aa : page.getAnchors()) {
				if (aa.getAttribute("href").contains("historische_kurse")) {
					a = aa;
					break;
				}
			}
			page = a.click();

			HtmlCheckBoxInput inputs = (HtmlCheckBoxInput) page.getElementById("clean_split");
			inputs.setChecked(false);
			inputs = (HtmlCheckBoxInput) page.getElementById("clean_payout");
			inputs.setChecked(false);
			inputs = (HtmlCheckBoxInput) page.getElementById("clean_bezug");
			inputs.setChecked(false);

			List<HtmlSubmitInput> submit = (List<HtmlSubmitInput>) page.getByXPath("//input[@class='submitButton' and @value='Anwenden']");
			page = submit.get(0).click();

			ArrayList<Config> options = new ArrayList<Config>();
			for (String section : cfg) {
				List<HtmlElement> links = getLinksForSelection(section, page);
				if (links.size() > 0) {
					Config config = new Config(section);
					for (HtmlElement link : links) {
						config.addAuswahl(link.getTextContent().trim(), section);
					}
					options.add(config);
				}
			}
			setConfig(options);

		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
		} finally {
		}

	}

	
	public void process(List<Config> config) {
		super.process(config);
		try {
			extractStockQuotes(config);
			extractEvents();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<HtmlElement> getLinksForSelection(String s, HtmlPage page) {
		List<HtmlElement> ret = new ArrayList<HtmlElement>();  
		List<HtmlDivision> divlinks = (List<HtmlDivision>) page.getByXPath("//div[contains(@class, 'contentRight')]/div");
		for (HtmlDivision div : divlinks) {
			String content = div.getTextContent().trim();
			if (content.startsWith(s)) {
				DomNodeList<HtmlElement> links = div.getElementsByTagName("a");
				for (HtmlElement aa : links) {
					ret.add(aa);
				}
			}
		}
		return ret;
	}


	private void extractEvents() throws IOException {
		HtmlPage newpage = null;
		for (HtmlElement x:getLinksForSelection("Ansicht", page)) {
			String content = x.getTextContent().trim();
			if (content.startsWith("Hist. Ereignisse")) {
				newpage = x.click();
				break;
			}
		}
		if (newpage == null) {
			throw new IllegalStateException("Button 'Hist. Ereignisse' not found!");
		}
		List<DomElement> tablelist = newpage.getElementsByTagName("table");
		HtmlTable datatable = null;
		for (DomElement table : tablelist) {
			if (table.getTextContent().trim().startsWith("Datum")) {
				datatable = (HtmlTable) table;
				break;
			}
		}
		if (newpage == null) {
			throw new IllegalStateException("Table 'Hist. Ereignisse' not found!");
		}
//		analyse(datatable);
		
	}

	private void extractStockQuotes(List<Config> config) {
		String defaultcur = "EUR";
		try {

			// Set the selected options
			for (Config cfg : config) {
				for (ConfigTuple o : cfg.getSelected()) {
					if (o.getObj().toString().equals("Währung")) {
						defaultcur = o.toString(); 
					}
					// Search for matching link
					for (HtmlElement link : getLinksForSelection(o.getObj().toString(), page)) {
						if (link.getTextContent().trim().equals(o.toString())) {
							page = link.click();
						}
					}

				}
			}

			HtmlTextInput min = (HtmlTextInput) page.getElementById("minTime");
			HtmlTextInput max = (HtmlTextInput) page.getElementById("maxTime");
			DateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
			min.setText(df.format(getStartdate()));
			max.setText(df.format(getStopdate()));
			min = (HtmlTextInput) page.getElementById("minTime");
			max = (HtmlTextInput) page.getElementById("maxTime");
			List<HtmlSubmitInput> submit = (List<HtmlSubmitInput>) page.getByXPath("//input[@class='submitButton' and @value='Download']");
			TextPage text = submit.get(0).click();
			evalCSV(text.getContent(), defaultcur);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void evalCSV(String s, String defaultcur) throws IOException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
		CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(';').withIgnoreEmptyLines(true);
		CSVParser parser = new CSVParser(new StringReader(s), format);
		resultQuotes = new ArrayList<datacontainer>();
		for(CSVRecord record : parser){
			datacontainer dc = new datacontainer();
			try {
				dc.data.put("date", df.parse(record.get("Datum")));
				dc.data.put("first", stringToBigDecimal(record.get("Erster")));
				dc.data.put("last", stringToBigDecimal(record.get("Schlusskurs")));
				dc.data.put("low", stringToBigDecimal(record.get("Tief")));
				dc.data.put("high", stringToBigDecimal(record.get("Hoch")));
				dc.data.put("currency", defaultcur);
				resultQuotes.add(dc);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		parser.close();
	}

	public BigDecimal stringToBigDecimal(String string) {
		if (string.trim().isEmpty()) {
			return null;
		}
		return new BigDecimal(string.replace(".", "").replace(",", "."));
	}

	@Override
	public String getName() {
		return "ARIVA";
	}

	@Override
	public String getURL() {
		return "http://www.ariva.de";
	}

}
