package com.orion.engineering.core.calendar;

import java.time.Duration;
import java.time.OffsetDateTime;

public class CalendarService {
	public static String formatDuration(long secondsToFormat) {
		return formatDuration(Duration.ofSeconds(secondsToFormat));
	}

	public static String formatDuration(OffsetDateTime start, OffsetDateTime end) {
		return formatDuration(Duration.between(start, end).abs());
	}

	public static String formatDuration(Duration duration) {
		long totalSeconds = duration.getSeconds();
		long days = duration.toDays();
		long hours = duration.toHoursPart();
		long minutes = duration.toMinutesPart();
		long seconds = duration.toSecondsPart();

		if (totalSeconds < 60) {
			return totalSeconds + "s";
		}

		if (totalSeconds < 3600) {
			return minutes + "m " + seconds + "s";
		}

		if (totalSeconds < 86400) {
			return hours + "h " + minutes + "m";
		}

		return days + "d " + hours + "h";
	}

	public static long getDurationInSeconds(OffsetDateTime start, OffsetDateTime end) {
		return Duration.between(start, end).abs().getSeconds();
	}
}
