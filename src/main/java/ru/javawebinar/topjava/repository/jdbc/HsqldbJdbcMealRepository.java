package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Repository
@Profile("hsqldb")
public class HsqldbJdbcMealRepository extends AbstractJdbcMealRepository {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public HsqldbJdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    public <T> T convertDateTime(LocalDateTime dateTime, Class<T> targetClass) {
        return targetClass.cast(Timestamp.valueOf(dateTime));
    }

    @Override
    protected Class<?> getDateTimeTargetClass() {
        return Timestamp.class;
    }
}