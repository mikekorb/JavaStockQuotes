import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jsq.config.Config;
import jsq.fetch.factory.Factory;
import jsq.fetcher.history.GenericJSFetcher;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class SimpleAllTests {

	/**
	 * Liefert aus der XML-Datei alle <held>-Nodes zurück, die getestet werden sollen
	 * @return Liste mit Nodes
	 * @throws Throwable Diverse Fehler 
	 */
	@Parameters
	public static Collection<Object[]> data() throws Throwable {
		System.out.println("Working Directory: " + System.getProperty("user.dir"));
		Collection<Object[]> params = new ArrayList<Object[]>();
		File dir = new File("js");
		if (dir.exists()) {
			for (final File fileEntry : dir.listFiles()) {
		        if (!fileEntry.isDirectory() && fileEntry.getName().toLowerCase().endsWith(".js")) {
		        	try {
		    			Object[] arr = new Object[] { fileEntry.getAbsolutePath() };
		    			params.add(arr);
		        	} catch (Exception e) {
		        		e.printStackTrace();
		        		throw new IllegalStateException("Fehler beim Laden von " + fileEntry.getName());
		        	}
		        }
		    }
		}
		return params;
	}

	private String x;

	/**
	 * Init
	 * @param h Held
	 */
	public SimpleAllTests(String s) {
		x = s;
	}
	
	/**
	 * Der eigentliche Test
	 * @throws Exception 
	 */
	@Test
	public void runit() throws Exception {
		System.out.println("===================================================================");
		System.out.println(x);
		GenericJSFetcher fetcher = new GenericJSFetcher(x);
		System.out.println("	Name: " + fetcher.getName());
		System.out.println("	URL: " + fetcher.getURL());
		System.out.println("	Api: " + fetcher.getAPIVersion());
		System.out.println("	Version: " + fetcher.getVersion());
		assertNotNull(fetcher.getName());
		assertNotNull(fetcher.getURL());
		assertNotNull(fetcher.getAPIVersion());
		assertNotNull(fetcher.getVersion());
		assertTrue(fetcher.getAPIVersion().equals("1"));
		
		fetcher.prepare("DE0007236101", 2012, 5, 29, 2014, 6, 1); // Siemens
		while (fetcher.hasMoreConfig()) {
			List<Config> config = fetcher.getConfigs();
			// Set always the first option
			for (Config c : config) {
				System.out.println("	Config " + c.toString());
				System.out.println("	Setting " + c.getBeschreibung() + " to " + c.getOptions().get(0));
				c.addSelectedOptions(c.getOptions().get(0));
			}
			fetcher.process(config);
		}
		System.out.println("	Quotes:" + fetcher.getHistQuotes());
		System.out.println("	Events:" + fetcher.getHistEvents());
	}
}




