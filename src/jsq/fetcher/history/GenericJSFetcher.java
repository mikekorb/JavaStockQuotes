package jsq.fetcher.history;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.ThreadedRefreshHandler;
import com.gargoylesoftware.htmlunit.WebClient;

import jsq.config.Config;

public class GenericJSFetcher extends BaseFetcher {

	private Invocable inv;

	public GenericJSFetcher(String filename) {
		try {
			File f = new File(filename);
			ScriptEngineManager manager = new ScriptEngineManager();  
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			engine.put("fetcher", this);
			engine.eval( new InputStreamReader(new FileInputStream(f),"utf-8"));

			inv = (Invocable) engine;  

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public String getName() {
		try {
			return (String) inv.invokeFunction("getName");
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void prepare(String search, int beginYear, int beginMon,
			int beginDay, int stopYear, int stopMon, int stopDay) {
		super.prepare(search, beginYear, beginMon, beginDay, stopYear, stopMon, stopDay);
		try {
			Object x = inv.invokeFunction("prepare", this, search, beginYear, beginMon, beginDay, stopYear, stopMon, stopDay);
			setConfig((List<Config>) x);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void process(List<Config> options) {
		super.process(options);
			try {
				inv.invokeFunction("process", options);
			} catch (NoSuchMethodException | ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public WebClient getWebClient(boolean useJavaScript) {
		WebClient webClient = new WebClient();
		webClient.setCssErrorHandler(new SilentCssErrorHandler());
		webClient.setRefreshHandler(new ThreadedRefreshHandler());
		webClient.getOptions().setJavaScriptEnabled(useJavaScript);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
		return webClient;
	}
}
