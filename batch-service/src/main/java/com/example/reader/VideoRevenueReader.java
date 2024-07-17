package com.example.reader;

import com.example.entity.VideoDailyViewCount;
import com.example.entity.VideoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class VideoRevenueReader {

    private static final Logger logger = LoggerFactory.getLogger(VideoRevenueReader.class);

    private final DataSource dataSource;

    public VideoRevenueReader(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean("videoRevenueJdbcReader")
    @StepScope
    public JdbcCursorItemReader<VideoDailyViewCount> videoRevenueJdbcReader(@Value("#{jobParameters['startDate']}") String startDate,
                                                                            @Value("#{jobParameters['endDate']}") String endDate) {
        String sql = "SELECT * FROM video_daily_view_counts WHERE date BETWEEN ? AND ?";
        logger.info("Executing query: {} with startDate: {} and endDate: {}", sql, startDate, endDate);

        return new JdbcCursorItemReaderBuilder<VideoDailyViewCount>()
                .name("videoRevenueReader")
                .dataSource(dataSource)
                .sql(sql)
                .preparedStatementSetter((ps) -> {
                    ps.setString(1, startDate);
                    ps.setString(2, endDate);
                })
                .rowMapper((rs, rowNum) -> {
                    VideoDailyViewCount viewCount = new VideoDailyViewCount();
                    viewCount.setId(rs.getLong("id"));

                    VideoEntity videoEntity = new VideoEntity();
                    videoEntity.setId(rs.getLong("video_id"));
                    viewCount.setVideo(videoEntity);

                    viewCount.setDate(rs.getDate("date").toLocalDate());
                    viewCount.setViewCount(rs.getInt("view_count"));
                    return viewCount;
                })
                .build();
    }
}