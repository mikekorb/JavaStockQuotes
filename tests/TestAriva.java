import static org.junit.Assert.*;

import java.util.List;

import jsq.config.Config;
import jsq.config.ConfigTuple;
import jsq.fetcher.history.Ariva;

import org.junit.Test;


public class TestAriva {

	@Test
	public void test() {
		Ariva x = new Ariva();
//		assertTrue(x.prepare("DE0008488214"));
		x.prepare("US0846707026", 2000, 1, 1, 2013, 1, 1);
		while (x.hasMoreConfig()) {
			List<Config> config = x.getConfigs();
			// Set always the first option
			for (Config c : config) {
				System.out.println("Setting " + c.getBeschreibung() + " to " + c.getOptions().get(0));
				c.addSelectedOptions(c.getOptions().get(0));
			}
			x.process(config);
		}
		System.out.println(x.getResult());
	}

}
