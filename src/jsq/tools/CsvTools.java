package jsq.tools;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CsvTools {

	public static List<CSVRecord> getRecordsFromCsv(char sep, String content) throws IOException {
		CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(sep).withIgnoreEmptyLines(true);
		CSVParser parser = new CSVParser(new StringReader(content), format);
		List<CSVRecord> liste = parser.getRecords();
		parser.close();
		return liste;
	}
}
