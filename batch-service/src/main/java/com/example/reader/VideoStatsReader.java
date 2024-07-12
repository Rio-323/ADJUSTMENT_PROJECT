package com.example.reader;

import com.example.entity.VideoDailyViewCount;
import com.example.entity.VideoEntity;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

@Component
public class VideoStatsReader {
    private static final Logger logger = LoggerFactory.getLogger(VideoStatsReader.class);

    public JdbcCursorItemReader<VideoDailyViewCount> reader(DataSource dataSource, String startDate, String endDate) {
        String sql = "SELECT * FROM video_daily_view_counts WHERE date BETWEEN ? AND ?";
        logger.info("Executing query: {} with startDate: {} and endDate: {}", sql, startDate, endDate);

        return new JdbcCursorItemReaderBuilder<VideoDailyViewCount>()
                .name("videoStatsReader")
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