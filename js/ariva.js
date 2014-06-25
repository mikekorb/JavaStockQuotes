try {
	load("nashorn:mozilla_compat.js");
	var prejava8 = false;
	var ArrayList = Java.type('java.util.ArrayList');

} catch(e) {
	// Rhino
	var prejava8 = true;
	var ArrayList = java.util.ArrayList;
};
var fetcher; 
var wc;


var y1,m1,d1,y2,m2,d2;

function getAPIVersion() {
	return "1";
};

function getVersion() {
	return "2014-06-25";
};

function getName() {
	return "Ariva";
};

function getURL() {
	return "http://www.ariva.de";
};

function prepare(fetch, search, startyear, startmon, startday, stopyear, stopmon, stopday) {
	fetcher = fetch;
	y1 = startyear; m1 = startmon; d1 = startday;
	y2 = stopyear; m2 = stopmon; d2 = stopday;

	webClient = fetcher.getWebClient(false);
	page = webClient.getPage("http://www.ariva.de/");

	input = Packages.jsq.tools.HtmlUnitTools.getFirstElementByXpath(page, "//input[@id='livesearch']");
	input.setValueAttribute(search);

	y = Packages.jsq.tools.HtmlUnitTools.getFirstElementByXpath(page, "//input[@id='go']");
	page = y.click();

	a = null;
	for (i = 0; i < page.getAnchors().size(); i++) {
		aa = page.getAnchors().get(i);
		if (aa.getAttribute("href").contains("/kurs")) {
			a = aa;
			break;
		}
	}
	page = a.click();


	// Link "Kurse"
	a = null;
	for (i = 0; i < page.getAnchors().size(); i++) {
		aa = page.getAnchors().get(i);
		if (aa.getAttribute("href").contains("historische_kurse")) {
			a = aa;
			break;
		}
	}
	page = a.click();

	page.getElementById("clean_split").setChecked(false);
	page.getElementById("clean_payout").setChecked(false);
	page.getElementById("clean_bezug").setChecked(false);

	page = Packages.jsq.tools.HtmlUnitTools.getFirstElementByXpath(page, "//input[@class='submitButton' and @value='Anwenden']").click();

	//Handelsplatz
	var cfgliste = new ArrayList();
	links = getLinksForSelection("Handelsplatz", page);
	if (links.size() > 0) {
		var cfg = new Packages.jsq.config.Config("Handelsplatz");
		for (i = 0; i < links.size(); i++) {
			cfg.addAuswahl(links.get(i).getTextContent().trim(), new String("Handelsplatz"));
		}
		cfgliste.add(cfg);
	}
	links = getLinksForSelection("Währung", page);
	if (links.size() > 0) {
		var cfg = new Packages.jsq.config.Config("Währung");
		for (i = 0; i < links.size(); i++) {
			cfg.addAuswahl(links.get(i).getTextContent().trim(), new String("Währung"));
		}
		cfgliste.add(cfg);
	}

	return cfgliste;
};

function process(config) {
	defaultcur = "EUR";
	for (i = 0; i < config.size(); i++) {
		var cfg = config.get(i);
		for (j = 0; j < cfg.getSelected().size(); j++) {
			var o = cfg.getSelected().get(j);
			if (o.getObj().toString().equals("Währung")) {
				defaultcur = o.toString(); 
			}
			links = getLinksForSelection(o.getObj(), page);
			for (j = 0; j < links.size(); j++) {
				var link = links.get(j);
				if (link.getTextContent().trim().equals(o.toString())) {
					page = link.click();
				}
			}
		}
	}

	page.getElementById("minTime").setText(d1 + "." + m1 + "." + y1);
	page.getElementById("maxTime").setText(d2 + "." + m2 + "." + y2);


	submit = Packages.jsq.tools.HtmlUnitTools.getFirstElementByXpath(page, "//input[@class='submitButton' and @value='Download']");
	text = submit.click();
	evalCSV(text.getContent(), defaultcur);

	extractEvents(page);
	
};


function extractEvents(page) {
	
	var dict = {};
	dict["Gratisaktien"] = Packages.jsq.datastructes.Const.STOCKDIVIDEND;
	dict["Dividende"] = Packages.jsq.datastructes.Const.CASHDIVIDEND;
	dict["Split"] = Packages.jsq.datastructes.Const.STOCKSPLIT;
	dict["Bezugsrecht"] = Packages.jsq.datastructes.Const.SUBSCRIPTIONRIGHTS;
	
 
//	{Datum=30.04.01, Verhältnis=2:1, Betrag=, Ereignis=Gratisaktien}
//	{Datum=23.02.01, Verhältnis= , Betrag=0,82 EUR, Ereignis=Dividende}
//	{Datum=04.01.99, Verhältnis=0,51129, Betrag=, Ereignis=Euro-Umstellung}
//	{Datum=02.05.96, Verhältnis=1:10, Betrag=, Ereignis=Split}
//  {Datum=29.07.91, Verhältnis=6:1, Betrag=20,45 EUR, Ereignis=Bezugsrecht} 
	link = Packages.jsq.tools.HtmlUnitTools.getElementByPartContent(page, "Hist. Ereignisse", "a");
	if (link ==  null) {
		print("Hist. Ereignisse nicht gefunden");
		return;
	}
	page.getElementById("clean_split").setChecked(false);
	page = link.click();
	tab = Packages.jsq.tools.HtmlUnitTools.getElementByPartContent(page, "Datum", "table");
	list = Packages.jsq.tools.HtmlUnitTools.analyse(tab);
	
	var res = new ArrayList();
	for (i = 0; i < list.size(); i++) {
		hashmap = list.get(i);
		if (hashmap.get("Ereignis") == "Euro-Umstellung") {
			continue;
		}
		
		// filter date range
		d = Packages.jsq.tools.VarTools.parseDate(hashmap.get("Datum"), "dd.MM.yy");
		if (!fetcher.within(d)) {
			continue;
		}

		var dc = new Packages.jsq.datastructes.Datacontainer();
		dc.put("date", d);
		dc.put("ratio", hashmap.get("Verhältnis"));
		dc.put("action", dict[hashmap.get("Ereignis")]);
		cur = "";
		amount = "";
		if (hashmap.get("Betrag") != "") {
			betrag = hashmap.get("Betrag").split(" ");
			amount = Packages.jsq.tools.VarTools.stringToBigDecimalGermanFormat(betrag[0]);
			cur = betrag[1];
		}
		dc.put("value", amount);
		dc.put("currency", cur);
		res.add(dc);
	}
	fetcher.setHistEvents(res);
	
}



function evalCSV(content, defaultcur)  {
	var records = Packages.jsq.tools.CsvTools.getRecordsFromCsv(";", content);
	var res = new ArrayList();
	for (i = 0; i < records.size(); i++) {
		var record = records.get(i);
		var dc = new Packages.jsq.datastructes.Datacontainer();
		dc.put("date", Packages.jsq.tools.VarTools.parseDate(record.get("Datum"), "yyyy-MM-dd"));
		dc.put("first", Packages.jsq.tools.VarTools.stringToBigDecimalGermanFormat(record.get("Erster")));
		dc.put("last", Packages.jsq.tools.VarTools.stringToBigDecimalGermanFormat(record.get("Schlusskurs")));
		dc.put("low", Packages.jsq.tools.VarTools.stringToBigDecimalGermanFormat(record.get("Tief")));
		dc.put("high", Packages.jsq.tools.VarTools.stringToBigDecimalGermanFormat(record.get("Hoch")));
		dc.put("currency", defaultcur);
		res.add(dc);
	}
	fetcher.setHistQuotes(res);
}

function getLinksForSelection(search,  page) {
	var ret = new ArrayList();
	divlinks = page.getByXPath("//div[contains(@class, 'contentRight')]/div");
	for (i = 0; i < divlinks.size(); i++) {
		var div = divlinks.get(i);
		content = div.getTextContent().trim();
		if (content.substring(0, search.length) == search) {
			links = div.getElementsByTagName("a");
			for (j = 0; j < links.size(); j++) {
				ret.add(links.get(j));
			}
		}
	}
	return ret;
}
