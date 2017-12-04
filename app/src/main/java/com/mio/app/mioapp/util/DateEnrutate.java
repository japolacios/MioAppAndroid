package com.mio.app.mioapp.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SuppressLint("SimpleDateFormat")
public class DateEnrutate {

	public static String formatearFecha(String formato, long dateTime) {
		Date date = new Date(dateTime);
		SimpleDateFormat format = new SimpleDateFormat(formato);

		String fechaFormato = format.format(date);

		return fechaFormato;
	}

	public static String getUpdate(String fecha) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = format.parse(fecha);

			Map<TimeUnit, Long> result = computeDiff(date, new Date());

			String horaPublicada = "";
			if (result.get(TimeUnit.DAYS) == 0
					&& result.get(TimeUnit.HOURS) == 0
					&& result.get(TimeUnit.MINUTES) == 0
					&& result.get(TimeUnit.SECONDS) != 0)
				horaPublicada = result.get(TimeUnit.SECONDS) + " s";
			else if (result.get(TimeUnit.DAYS) == 0
					&& result.get(TimeUnit.HOURS) == 0
					&& result.get(TimeUnit.MINUTES) != 0)
				horaPublicada = result.get(TimeUnit.MINUTES) + " m";
			else if (result.get(TimeUnit.DAYS) == 0
					&& result.get(TimeUnit.HOURS) != 0)
				horaPublicada = result.get(TimeUnit.HOURS) + " hora(s)";
			else if (result.get(TimeUnit.DAYS) > 0
					&& result.get(TimeUnit.DAYS) < 30)
				horaPublicada = result.get(TimeUnit.DAYS) + " día(s)";
			else if (result.get(TimeUnit.DAYS) >= 30)
				horaPublicada = (result.get(TimeUnit.DAYS) / 30) + " mes(es)";

			return " Hace " + horaPublicada;

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	public static Map<TimeUnit, Long> computeDiff(Date date1, Date date2) {
		long diffInMillies = date2.getTime() - date1.getTime();
		List<TimeUnit> units = new ArrayList<TimeUnit>(
				EnumSet.allOf(TimeUnit.class));
		Collections.reverse(units);

		Map<TimeUnit, Long> result = new LinkedHashMap<TimeUnit, Long>();
		long milliesRest = diffInMillies;
		for (TimeUnit unit : units) {
			long diff = unit.convert(milliesRest, TimeUnit.MILLISECONDS);
			long diffInMilliesForUnit = unit.toMillis(diff);
			milliesRest = milliesRest - diffInMilliesForUnit;
			result.put(unit, diff);
		}
		return result;
	}

}
