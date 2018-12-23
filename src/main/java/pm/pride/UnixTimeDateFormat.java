package pm.pride;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Date;

public class UnixTimeDateFormat extends Format {

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
		Date date = (Date)obj;
		toAppendTo.append(date.getTime());
		return toAppendTo;
	}
	
}
