/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2024 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.application.api.user;

import org.apache.commons.io.FileUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LocalizedFormatter {

	private final Locale locale;
	private final ZoneId timezone;
	private final NumberFormat percentFormat;
	private final NumberFormat compactNumberFormat;
	private final NumberFormat decimalFormat;
	private final DateTimeFormatter dateFormat;
	private final DateTimeFormatter dateTimeFormat;
	private final DateTimeFormatter dateTimeFormatLong;
	private final DateTimeFormatter timeFormatter;
	private final DateTimeFormatter durationFormatter;

	public LocalizedFormatter(Locale locale, ZoneId timezone) {
		this.locale = locale;
		this.timezone = timezone;
		percentFormat = NumberFormat.getPercentInstance(locale);
		percentFormat.setMaximumFractionDigits(2);
		compactNumberFormat = NumberFormat.getCompactNumberInstance(locale, NumberFormat.Style.LONG);
		decimalFormat = DecimalFormat.getInstance(locale);
		decimalFormat.setMaximumFractionDigits(2);
		dateTimeFormat = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).withLocale(locale);
		dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale);
		dateTimeFormatLong = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.SHORT).withLocale(locale);
		timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale);
		durationFormatter = DateTimeFormatter.ofPattern("mm:ss");
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
		if (instant == null) return null;
		return ZonedDateTime.ofInstant(instant, timezone).format(timeFormatter);
	}

	public String formatDateOnlyLong(Instant instant) {
		if (instant == null) return null;
		return ZonedDateTime.ofInstant(instant, timezone).format(dateFormat);
	}

	public String formatDateTime(Instant instant) {
		if (instant == null) return null;
		return ZonedDateTime.ofInstant(instant, timezone).format(dateTimeFormat);
	}

	public String formatDurationInSeconds(int seconds) {
		LocalTime timeOfDay = LocalTime.ofSecondOfDay(Math.abs(seconds));
		return durationFormatter.format(timeOfDay);
	}

}
