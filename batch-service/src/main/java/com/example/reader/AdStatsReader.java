package com.example.reader;

import com.example.entity.AdDailyViewCount;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdStatsReader {

    private final JdbcTemplate jdbcTemplate;

    public AdStatsReader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AdDailyViewCount> readAdDailyViewCounts() {
        String sql = "SELECT * FROM ad_daily_view_counts";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            AdDailyViewCount adDailyViewCount = new AdDailyViewCount();
            adDailyViewCount.setId(rs.getLong("id"));
            adDailyViewCount.setDate(rs.getDate("date").toLocalDate());
            adDailyViewCount.setViewCount(rs.getInt("view_count"));
            // Set the AdEntity here if needed
            return adDailyViewCount;
        });
    }
}