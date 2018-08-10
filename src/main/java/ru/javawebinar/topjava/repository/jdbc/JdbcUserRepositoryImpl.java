package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepositoryImpl implements UserRepository {

//    private static final BeanPropertyRowMapper<User> USER_ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private static final ResultSetExtractor<List<User>> RESULT_SET_EXTRACTOR = rs -> {
        Map<Integer, User> usersById = new HashMap<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String name = rs.getString("name");
            String email = rs.getString("email");
            String password = rs.getString("password");
            int caloriesPerDay = rs.getInt("calories_per_day");
            boolean enabled = rs.getBoolean("enabled");
            Date registered = rs.getDate("registered");
            Role role = Role.valueOf(rs.getString("role"));
            User user = usersById.getOrDefault(id,
                    new User(id, name, email, password, caloriesPerDay, enabled, registered, Collections.singleton(role)));
            user.getRoles().add(role);
            usersById.putIfAbsent(id, user);
        }
        return usersById.values().stream().sorted(Comparator.comparing(User::getName)).collect(Collectors.toList());
    };

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;
    private BatchPreparedStatementSetter statementSetter;

    @Autowired
    public JdbcUserRepositoryImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Transactional
    @Override
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
            List<Role> userRoles = new ArrayList<>(user.getRoles());
            statementSetter = new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    //noinspection ConstantConditions
                    ps.setInt(1, user.getId());
                    ps.setString(2, userRoles.get(i).name());
                }

                @Override
                public int getBatchSize() {
                    return userRoles.size();
                }
            };
            jdbcTemplate.batchUpdate("INSERT INTO user_roles VALUES (?, ?)", statementSetter);
        } else if (namedParameterJdbcTemplate.update(
                "UPDATE users SET name=:name, email=:email, password=:password, " +
                        "registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id", parameterSource) == 0) {
            return null;
        }


        return user;
    }

    @Transactional
    @Override
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users" +
                " JOIN user_roles ON users.id = user_roles.user_id  WHERE id=?", RESULT_SET_EXTRACTOR, id);
        User user = DataAccessUtils.singleResult(users);
        return user;
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", USER_ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query("SELECT * FROM users" +
                " JOIN user_roles ON users.id = user_roles.user_id  WHERE email=?", RESULT_SET_EXTRACTOR, email);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("SELECT * FROM users" +
                " JOIN user_roles ON users.id = user_roles.user_id  ORDER BY name, email", RESULT_SET_EXTRACTOR);
    }
}
