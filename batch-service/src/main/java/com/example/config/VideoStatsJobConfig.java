package com.example.config;

import com.example.listener.VideoStatsStepExecutionListener;
import com.example.processor.VideoStatsProcessor;
import com.example.writer.VideoStatsWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class VideoStatsJobConfig {

    @Bean
    public VideoStatsProcessor videoStatsProcessor() {
        return new VideoStatsProcessor();
    }

    @Bean
    public VideoStatsWriter videoStatsWriter(DataSource dataSource) {
        return new VideoStatsWriter(dataSource);
    }

    @Bean
    public VideoStatsStepExecutionListener videoStatsStepExecutionListener(VideoStatsProcessor processor, VideoStatsWriter writer) {
        return new VideoStatsStepExecutionListener(processor, writer);
    }
}