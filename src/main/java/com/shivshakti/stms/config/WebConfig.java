package com.shivshakti.stms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.format.Formatter;
import java.util.Locale;
import java.text.ParseException;

/**
 * Web Configuration Class
 * Configures web-related settings including formatters and view controllers
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/dashboard");
        registry.addViewController("/dashboard").setViewName("dashboard/index");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new LocalDateFormatter());
        registry.addFormatter(new LocalDateTimeFormatter());
    }

    /**
     * Custom formatter for LocalDate
     */
    public static class LocalDateFormatter implements Formatter<LocalDate> {
        
        @Override
        public LocalDate parse(String text, Locale locale) throws ParseException {
            if (text == null || text.trim().isEmpty()) {
                return null;
            }
            return LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        @Override
        public String print(LocalDate object, Locale locale) {
            return object != null ? object.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
        }
    }

    /**
     * Custom formatter for LocalDateTime
     */
    public static class LocalDateTimeFormatter implements Formatter<LocalDateTime> {
        
        @Override
        public LocalDateTime parse(String text, Locale locale) throws ParseException {
            if (text == null || text.trim().isEmpty()) {
                return null;
            }
            return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        }

        @Override
        public String print(LocalDateTime object, Locale locale) {
            return object != null ? object.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")) : "";
        }
    }
}

