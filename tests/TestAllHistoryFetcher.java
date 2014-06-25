import static org.junit.Assert.*;

import java.util.List;

import jsq.config.Config;
import jsq.datastructes.Datacontainer;
import jsq.fetch.factory.Factory;
import jsq.fetcher.history.BaseFetcher;

import org.junit.Test;


public class TestAllHistoryFetcher {

	@Test
	public void test() throws Exception {
		for (BaseFetcher x : Factory.getHistoryFetcher()) {
			System.out.println("Checking: " + x.getName());
			assertFalse(x.getName().isEmpty());
			assertFalse(x.getURL().isEmpty());
			
			x.prepare("DE0007100000", 2013, 1, 15, 2013, 1, 15);
			
			
			while (x.hasMoreConfig()) {
				List<Config> config = x.getConfigs();
				// Set always the first option
				for (Config c : config) {
					System.out.println("Setting " + c.getBeschreibung() + " to " + c.getOptions().get(0));
					c.addSelectedOptions(c.getOptions().get(0));
				}
				x.process(config);
			}
			
			//System.out.println(x.getResult());
			List<Datacontainer> results = x.getHistQuotes();
			assertTrue(results.size() == 1);
		}
	}

}
