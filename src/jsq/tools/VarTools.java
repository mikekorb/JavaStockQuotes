package jsq.tools;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VarTools {
	
	public static BigDecimal stringToBigDecimalGermanFormat(String string) {
		return stringToBigDecimal(string.replace(".", "").replace(",", "."));
	}

	public static BigDecimal stringToBigDecimal(String string) {
		if (string.trim().isEmpty()) {
			return null;
		}
		return new BigDecimal(string);
	}
	
	public static Date parseDate(String date, String format) {
		DateFormat df = new SimpleDateFormat(format);
		try {
			return df.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	public static String date2String(String date, String format) {
		DateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}
}
