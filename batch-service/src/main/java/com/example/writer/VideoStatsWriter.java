package com.example.writer;

import com.example.entity.VideoStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class VideoStatsWriter {

    private final JdbcBatchItemWriter<VideoStatistics> jdbcWriter;
    private static final Logger logger = LoggerFactory.getLogger(VideoStatsWriter.class);

    public VideoStatsWriter(DataSource dataSource) {
        jdbcWriter = new JdbcBatchItemWriter<>();
        jdbcWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        jdbcWriter.setSql("INSERT INTO video_statistics (video_id, period, video_rank, view_count, play_time, type) VALUES (:videoId, :period, :videoRank, :viewCount, :playTime, :type)");
        jdbcWriter.setDataSource(dataSource);
        jdbcWriter.afterPropertiesSet();
    }

    public JdbcBatchItemWriter<VideoStatistics> videoStatsWriter() {
        return jdbcWriter;
    }

    public void writeSummaries(List<VideoStatistics> summaries) {
        for (int i = 0; i < summaries.size(); i++) {
            VideoStatistics summary = summaries.get(i);
            summary.setVideoRank(i + 1);
            try {
                jdbcWriter.write(new Chunk<>(List.of(summary)));
            } catch (Exception e) {
                logger.error("Error writing summary: {}", summary, e);
            }
        }
    }
}