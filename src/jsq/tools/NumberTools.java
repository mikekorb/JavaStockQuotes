package jsq.tools;

import java.math.BigDecimal;

public class NumberTools {

	public static BigDecimal stringToBigDecimal(String string) {
		if (string.trim().isEmpty()) {
			return null;
		}
		return new BigDecimal(string);
	}

}
