package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Repository
@Profile("hsqldb")
public class JdbcMealRepositoryHsqldb extends JdbcMealRepository {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public JdbcMealRepositoryHsqldb(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    public <T> T convertDateTime(LocalDateTime dateTime, Class<T> targetClass) {
        return targetClass.cast(dateTime.format(dateTimeFormatter));
    }

    @Override
    public Class<?> getDateTimeTargetClass() {
        return String.class;
    }
}