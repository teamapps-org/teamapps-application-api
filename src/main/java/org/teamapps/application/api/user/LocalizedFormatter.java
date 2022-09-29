package org.teamapps.application.api.user;

import org.apache.commons.io.FileUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class LocalizedFormatter {

	private final Locale locale;
	private final ZoneId timezone;
	private final NumberFormat percentFormat;
	private final NumberFormat compactNumberFormat;
	private final NumberFormat decimalFormat;
	private final DateTimeFormatter dateTimeFormat;
	private final DateTimeFormatter dateTimeFormatLong;
	private final DateTimeFormatter timeFormatter;

	public LocalizedFormatter(Locale locale, ZoneId timezone) {
		this.locale = locale;
		this.timezone = timezone;
		percentFormat = NumberFormat.getPercentInstance(locale);
		percentFormat.setMaximumFractionDigits(2);
		compactNumberFormat = NumberFormat.getCompactNumberInstance(locale, NumberFormat.Style.LONG);
		decimalFormat = DecimalFormat.getInstance(locale);
		decimalFormat.setMaximumFractionDigits(2);
		dateTimeFormat = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).withLocale(locale);
		dateTimeFormatLong = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.MEDIUM).withLocale(locale);
		timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale);
	}

	public String formatFileSize(long length) {
		return FileUtils.byteCountToDisplaySize(length);
	}

	public String formatPercent(double value) {
		return percentFormat.format(value);
	}

	public String formatDecimalNumber(double value) {
		return decimalFormat.format(value);
	}

	public String formatCompactNumber(long value) {
		return compactNumberFormat.format(value);
	}

	public String formatTimestamp(int timestamp) {
		return ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp), timezone).format(dateTimeFormat);
	}

	public String formatTimestamp(long timestamp) {
		if (timestamp < Integer.MAX_VALUE) {
			return ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp), timezone).format(dateTimeFormat);
		} else {
			return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), timezone).format(dateTimeFormat);
		}
	}

	public String formatTimestampLong(int timestamp) {
		return ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp), timezone).format(dateTimeFormatLong);
	}

	public String formatTimestampLong(long timestamp) {
		if (timestamp < Integer.MAX_VALUE) {
			return ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp), timezone).format(dateTimeFormatLong);
		} else {
			return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), timezone).format(dateTimeFormatLong);
		}
	}

	public String formatTimeOnly(Instant instant) {
		return ZonedDateTime.ofInstant(instant, timezone).format(timeFormatter);
	}

}
