package org.springframework.web.servlet.view.mustache;

public class EncodingUtil {

	public static String getEncoding() {
		String encoding = System.getProperty("mustache.template.encoding");
		if( encoding!=null )
			return encoding;
		
		return "UTF-8";
	}
}
