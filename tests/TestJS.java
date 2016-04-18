import java.util.List;

import jsq.config.Config;
import jsq.fetcher.history.GenericJSFetcher;

import org.junit.Test;


public class TestJS {

	@Test
	public void test() throws Exception {
		//GenericJSFetcher fetcher = new GenericJSFetcher("js/finanzennet.js");
		GenericJSFetcher fetcher = new GenericJSFetcher("js/ariva.js");
		System.out.println(fetcher);
//		fetcher.prepare("DE0007100000", 2013, 1, 15, 2013, 1, 31);
//		fetcher.prepare("DE0007236101", 2013, 1, 15, 2013, 1, 31); 
		//fetcher.prepare("LU0119124781", 1995, 1, 1, 2014, 6, 25);
//		fetcher.prepare("LU0635178014", 1995, 1, 1, 2014, 6, 25); /
		fetcher.prepare("603474", 1995, 1, 1, 2014, 6, 25); 
		
		//LU0274211217 db x-tr.EO STOXX 50
		//fetcher.prepare("LU0274211217", 1995, 1, 1, 2014, 6, 25); // Fond
		while (fetcher.hasMoreConfig()) {
			List<Config> config = fetcher.getConfigs();
			// Set always the first option
			for (Config c : config) {
				System.out.println("Config " + c.toString());
				System.out.println("Setting " + c.getBeschreibung() + " to " + c.getOptions().get(0));
				c.addSelectedOptions(c.getOptions().get(0));
			}
			fetcher.process(config);
		}
		System.out.println("Quotes:");
		System.out.println(fetcher.getHistQuotes());
		System.out.println("HistEvents:");
		System.out.println(fetcher.getHistEvents());
		System.out.println("StockDetials");
		System.out.println(fetcher.getStockDetails());
	}

	public static void main(String [] args) throws Exception {
		TestJS js = new TestJS();
		js.test();
	}

}
