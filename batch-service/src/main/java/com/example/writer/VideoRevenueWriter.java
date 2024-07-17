package com.example.writer;

import com.example.entity.VideoRevenue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class VideoRevenueWriter {

    private static final Logger logger = LoggerFactory.getLogger(VideoRevenueWriter.class);
    private final JdbcBatchItemWriter<VideoRevenue> jdbcWriter;

    public VideoRevenueWriter(DataSource dataSource) {
        this.jdbcWriter = new JdbcBatchItemWriter<>();
        this.jdbcWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        this.jdbcWriter.setSql("INSERT INTO video_revenue (video_id, period, total_revenue, video_revenue, ad_revenue) VALUES (:videoId, :period, :totalRevenue, :videoRevenue, :adRevenue)");
        this.jdbcWriter.setDataSource(dataSource);
        this.jdbcWriter.afterPropertiesSet();
    }

    public JdbcBatchItemWriter<VideoRevenue> videoRevenueWriter() {
        return this.jdbcWriter;
    }

    public void write(Chunk<? extends VideoRevenue> items) throws Exception {
        for (VideoRevenue item : items) {
            logger.info("Writing item: {}", item);
        }
        jdbcWriter.write(items);
    }

    public void writeSummaries(List<VideoRevenue> summaries) throws Exception {
        for (VideoRevenue summary : summaries) {
            logger.info("Writing summary: {}", summary);
        }
        Chunk<VideoRevenue> chunk = new Chunk<>(summaries);
        write(chunk);
    }
}