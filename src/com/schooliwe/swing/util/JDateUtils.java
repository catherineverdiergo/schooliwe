package com.schooliwe.swing.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.schooliwe.resources.ResourcesManager;

public class JDateUtils {
	
	public static final SimpleDateFormat fmtDayStr = new SimpleDateFormat("EEEEEEEEEEE");
	public static final SimpleDateFormat fmtDayLongStr = new SimpleDateFormat("EEEEEEEEEE dd MMMMMMMMM yyyy");
	public static final SimpleDateFormat fmtDaySheetStr = new SimpleDateFormat("EEEEEEEEEE dd");
	public static final SimpleDateFormat fmtMonthLongStr = new SimpleDateFormat("MMMMMMMMM_yyyy");
	public static final SimpleDateFormat fmtDayFileStr = new SimpleDateFormat("yyyyMMdd");
	
	public static String getLocalDay(Date dt) {
		StringBuffer localDay = new StringBuffer(fmtDayStr.format(dt));
		if (localDay.charAt(0)>'Z')
			localDay.setCharAt(0, (char)(localDay.charAt(0)-'a'+'A'));
		return localDay.toString();
	}
	
	public static String isVacationDate(Date dt) {
		String result = null;
		if (dt != null) {
			String currentDateStr = ResourcesManager.sdfVacation.format(dt);
			Document docVacation = ResourcesManager.getInstance().getResource(ResourcesManager.VACATION_FILE_ID);
			NodeList vacList = docVacation.getElementsByTagName("vacation");
			int len = vacList.getLength();
			for (int i=0;i<len;i++) {
				Element vacation = (Element)vacList.item(i);
				String openDate = vacation.getAttribute("opendate");
				String closeDate = vacation.getAttribute("closedate");
				if (openDate.compareTo(currentDateStr) < 0 && currentDateStr.compareTo(closeDate) < 0) {
					if (vacation.getAttribute("img")!=null)
						result = vacation.getAttribute("label")+";;"+vacation.getAttribute("img");
					else
						result = vacation.getAttribute("label");
				}
			}
		}
		return result;
	}
	
}
