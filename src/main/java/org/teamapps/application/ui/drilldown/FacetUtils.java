package org.teamapps.application.ui.drilldown;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class FacetUtils {

	public static String getDateGroup(Instant instant, TemporalFacetGrouper grouper) {
		return switch (grouper) {
			case DAY -> getDayGroup(instant);
			case WEEK -> getWeekGroup(instant);
			case MONTH -> getMonthGroup(instant);
			case QUARTER -> getQuarterGroup(instant);
			case YEAR -> getYearGroup(instant);
			case DAYS -> getDays(instant) + " Tage";
			case WEEKS -> getWeeks(instant) + " Wochen";
			case MONTHS -> getMonths(instant) + " Monate";
			case YEARS -> getYears(instant) + " Jahre";
		};
	}

	public static String getDateGroup(Instant instant, TemporalFacetGrouper grouper, Locale locale) {
		return switch (grouper) {
			case DAY -> getDayGroup(instant);
			case WEEK -> getWeekGroup(instant);
			case MONTH -> getMonthGroup(instant, locale);
			case QUARTER -> getQuarterGroup(instant);
			case YEAR -> getYearGroup(instant);
			case DAYS -> getDays(instant) + " Tage";
			case WEEKS -> getWeeks(instant) + " Wochen";
			case MONTHS -> getMonths(instant) + " Monate";
			case YEARS -> getYears(instant) + " Jahre";
		};
	}

	public static String getDayGroup(Instant instant) {
		if (instant == null) return "-";
		return toLocalDate(instant).toString();
	}

	public static String getWeekGroup(Instant instant) {
		if (instant == null) return "-";
		LocalDate localDate = toLocalDate(instant);
		return localDate.getYear() + "-" + getWeek(localDate);
	}

	public static String getMonthGroup(Instant instant) {
		if (instant == null) return "-";
		LocalDate localDate = toLocalDate(instant);
		String month = getMonth(localDate) < 10 ? "0" + getMonth(localDate) : getMonth(localDate) + "";
		return localDate.getYear() + "-" + month;
	}

	public static String getMonthGroup(Instant instant, Locale locale) {
		if (instant == null) return "-";
		LocalDate localDate = toLocalDate(instant);
		return localDate.getYear() + "-" + getMonth(localDate, locale);
	}

	public static String getQuarterGroup(Instant instant) {
		if (instant == null) return "-";
		LocalDate localDate = toLocalDate(instant);
		return localDate.getYear() + "-" + getQuarterAsString(localDate);
	}

	public static String getYearGroup(Instant instant) {
		if (instant == null) return "-";
		LocalDate localDate = toLocalDate(instant);
		return localDate.getYear() + "";
	}

	public static int getYears(Instant instant) {
		if (instant == null) return 0;
		LocalDate localDate = toLocalDate(instant);
		return (int) Math.abs(ChronoUnit.YEARS.between(localDate, LocalDate.now()));
	}

	public static int getMonths(Instant instant) {
		if (instant == null) return 0;
		LocalDate localDate = toLocalDate(instant);
		return (int) Math.abs(ChronoUnit.MONTHS.between(localDate, LocalDate.now()));
	}

	public static int getDays(Instant instant) {
		if (instant == null) return 0;
		LocalDate localDate = toLocalDate(instant);
		return (int) Math.abs(ChronoUnit.DAYS.between(localDate, LocalDate.now()));
	}

	public static int getWeeks(Instant instant) {
		if (instant == null) return 0;
		LocalDate localDate = toLocalDate(instant);
		return (int) Math.abs(ChronoUnit.DAYS.between(localDate, LocalDate.now())) / 7;
	}

	public static LocalDate toLocalDate(Instant instant) {
		return LocalDate.ofInstant(instant, ZoneOffset.UTC);
	}

	public static LocalDateTime toLocalDateTime(Instant instant) {
		return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
	}

	public static int getQuarter(LocalDate localDate) {
		return localDate.get(IsoFields.QUARTER_OF_YEAR);
	}

	public static String getQuarterAsString(LocalDate localDate) {
		int quarter = getQuarter(localDate);
		return switch (quarter) {
			case 0 -> "-";
			case 1 -> "I";
			case 2 -> "II";
			case 3 -> "III";
			case 4 -> "IV";
			default -> "";
		};
	}

	public static int getWeek(LocalDate localDate) {
		return localDate.get(WeekFields.ISO.weekOfYear());
	}

	public static int getMonth(LocalDate localDate) {
		return localDate.getMonthValue();
	}

	public static String getMonth(LocalDate localDate, Locale locale) {
		return localDate.getMonth().getDisplayName(TextStyle.FULL, locale);
	}

	public static String getLongGroup(long value, int division) {
		if (value == 0) return "0";
		long start = value / division;
		start *= division;
		long end = start + division - 1;
		if (start == 0) {
			start = 1;
		}
		return start + " - " + end;
	}

//	public int getWeek(LocalDate localDate) {
//		return localDate.get(WeekFields.ISO.weekOfWeekBasedYear());
//	}
//
//	public int getWeekYear(LocalDate localDate) {
//		return localDate.get(WeekFields.ISO.weekBasedYear());
//	}

}
