package com.example.config;

import com.example.processor.VideoRevenueProcessor;
import com.example.reader.AdStatsReader;
import com.example.writer.VideoRevenueWriter;
import com.example.listener.VideoRevenueStepExecutionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class VideoRevenueJobConfig {

    @Bean
    public VideoRevenueProcessor videoRevenueProcessor() {
        return new VideoRevenueProcessor();
    }

    @Bean
    public VideoRevenueWriter videoRevenueWriter(DataSource dataSource) {
        return new VideoRevenueWriter(dataSource);
    }

    @Bean
    public VideoRevenueStepExecutionListener videoRevenueStepExecutionListener(VideoRevenueProcessor processor, VideoRevenueWriter writer, AdStatsReader adStatsReader) {
        return new VideoRevenueStepExecutionListener(processor, writer, adStatsReader);
    }

    @Bean
    public AdStatsReader adStatsReader(DataSource dataSource) {
        return new AdStatsReader(new JdbcTemplate(dataSource));
    }
}