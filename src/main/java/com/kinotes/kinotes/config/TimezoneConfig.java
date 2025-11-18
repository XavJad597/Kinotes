package com.kinotes.kinotes.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

/**
 * Configuration class to set the default JVM timezone
 * Ensures consistency across the entire application
 */
@Configuration
public class TimezoneConfig {

    private static final Logger logger = LoggerFactory.getLogger(TimezoneConfig.class);
    // Using UTC to avoid PostgreSQL timezone compatibility issues
    // Store all timestamps in UTC, convert to local timezone in frontend
    private static final String DEFAULT_TIMEZONE = "UTC";

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
        logger.info("Application timezone set to: {}", DEFAULT_TIMEZONE);
        logger.info("Current default timezone: {}", TimeZone.getDefault().getID());
        logger.info("System default timezone was: {}", System.getProperty("user.timezone"));
    }
}
