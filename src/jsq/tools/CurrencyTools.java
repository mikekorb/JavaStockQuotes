package jsq.tools;

public class CurrencyTools {

	public static String correctCurrency(String cur) {
		String t = cur.toLowerCase();
		if (t.equals("euro") ||  t.equals("€")) {
			return "EUR";
		} if (t.equals("$") || t.equals("us-dollar")) {
			return "USD";
		}
		return cur.toUpperCase();
	}
}
