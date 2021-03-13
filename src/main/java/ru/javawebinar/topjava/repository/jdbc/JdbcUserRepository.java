package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.util.ValidationUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private final TransactionTemplate transactionTemplate;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate,
                              NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                              TransactionTemplate transactionTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public User save(User user) {
        ValidationUtil.validateEntity(user);
        return transactionTemplate.execute(status -> {
            BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

            if (user.isNew()) {
                Number newKey = insertUser.executeAndReturnKey(parameterSource);
                user.setId(newKey.intValue());
                insertUserRoles(user);
            } else {
                if (namedParameterJdbcTemplate.update("""
               UPDATE users SET name=:name, email=:email, password=:password,
               registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
            """, parameterSource) == 0) {
                    return null;
                } else {
                    jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", user.getId());
                    insertUserRoles(user);
                }
            }
            return user;
        });
    }

    @Override
    public boolean delete(int id) {
        return transactionTemplate.execute(status -> jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0);
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id=?", ROW_MAPPER, id);
        User user = DataAccessUtils.singleResult(users);
        if (user != null) {
            List<Role> roles = jdbcTemplate.query("SELECT role FROM user_roles WHERE user_id=?", (rs, rowNum) -> Role.valueOf(rs.getString("role")), id);
            user.setRoles(roles);
        }
        return user;
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        User user = DataAccessUtils.singleResult(users);
        if (user != null) {
            List<Role> roles = jdbcTemplate.query("SELECT role FROM user_roles WHERE user_id=?", (rs, rowNum) -> Role.valueOf(rs.getString("role")), user.id());
            user.setRoles(roles);
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        List<User> users = jdbcTemplate.query("SELECT * FROM users ORDER BY name, email", ROW_MAPPER);

        class UserRole {
            private final int userId;
            private final Role role;

            public UserRole(int userId, Role role) {
                this.userId = userId;
                this.role = role;
            }
        }

        List<UserRole> userRoles = jdbcTemplate.query("SELECT * FROM user_roles", (rs, rowNum) -> new UserRole(rs.getInt("user_id"), Role.valueOf(rs.getString("role"))));
        Map<Integer, List<UserRole>> groupedUserRoles = userRoles.stream()
                .collect(Collectors.groupingBy(userRole -> userRole.userId));
        users.forEach(user -> user.setRoles(groupedUserRoles.get(user.getId()).stream().map(userRole -> userRole.role).collect(Collectors.toList())));
        return users;
    }

    private void insertUserRoles(User user) {
        jdbcTemplate.batchUpdate("INSERT INTO user_roles (user_id, role) VALUES (?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                List<Role> roles = new ArrayList<>(user.getRoles());
                ps.setInt(1, user.getId());
                ps.setString(2, roles.get(i).name());
            }

            @Override
            public int getBatchSize() {
                return user.getRoles().size();
            }
        });
    }
}