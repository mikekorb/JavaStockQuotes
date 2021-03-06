import static org.junit.Assert.*;

import java.util.List;

import jsq.config.Config;
import jsq.fetcher.history.Yahoo;

import org.junit.Test;


public class TestYahoo {

	@Test
	public void test() throws Exception {
		Yahoo x = new Yahoo();
		x.prepare("DE0007236101", 2012, 1, 1, 2013, 1, 1); // Siemens
		while (x.hasMoreConfig()) {
			List<Config> config = x.getConfigs();
			// Set always the first option
			for (Config c : config) {
				System.out.println("Setting " + c.getBeschreibung() + " to " + c.getOptions().get(0));
				c.addSelectedOptions(c.getOptions().get(0));
			}
			x.process(config);
		}
		System.out.println(x.getHistQuotes());
		System.out.println(x.getHistEvents());
		System.out.println(x.getStockDetails());
		assertNotNull(x.getHistQuotes());
		assertNotNull(x.getHistEvents());
		assertNotNull(x.getStockDetails());
	}

}
