package com.example.socialnetwork.repository;

import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.domain.validators.Validator;
import com.example.socialnetwork.dto.UserFilterDTO;
import com.example.socialnetwork.utils.Pair;

import java.sql.*;
import java.util.*;

public class UserDbRepository implements UserRepository{

    protected String url;
    protected String username;
    protected String password;
    private final Validator<User> validator;

    public UserDbRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    private Pair<String, List<Object>> toSQL(UserFilterDTO filter) {
        if(filter == null) {
            return new Pair<>("", Collections.emptyList());
        }

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        filter.getFirstName().ifPresent(firstNameFilter -> {
            conditions.add("first_name LIKE ?");
            params.add("%" + firstNameFilter + "%");
        });

        filter.getLastName().ifPresent(lastNameFilter -> {
            conditions.add("last_name LIKE ?");
            params.add("%" + lastNameFilter + "%");
        });

        filter.getEmail().ifPresent(emailFilter -> {
            conditions.add("email LIKE ?");
            params.add("%" + emailFilter + "%");
        });

        String sql = String.join("and", conditions);
        return new Pair<>(sql, params);
    }

    @Override
    public Page<User> findAll(Pageable pageable, UserFilterDTO filter) {
        List<User> userList = new ArrayList<>();
        String countSQL = "SELECT COUNT(*) AS count FROM users";
        String pageSQL = "SELECT * FROM users";
        Pair<String, List<Object>> pair = toSQL(filter);

        if(!pair.getFirst().isEmpty())
        {
            countSQL += " WHERE " + pair.getFirst();
            pageSQL += " WHERE "+ pair.getFirst();
        }

        pageSQL += " LIMIT ? OFFSET ?";
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement pagePreparedStatement = connection.prepareStatement(pageSQL);
             PreparedStatement countPreparedStatement = connection.prepareStatement(countSQL);

        ) {
            int paramIndex = 0;
            for(Object param : pair.getSecond())
            {
                paramIndex++;
                countPreparedStatement.setObject(paramIndex,param);
                pagePreparedStatement.setObject(paramIndex,param);
            }
            pagePreparedStatement.setInt(++paramIndex, pageable.getPageSize());
            pagePreparedStatement.setInt(++paramIndex, pageable.getPageSize() * pageable.getPageNumber());
            try (ResultSet pageResultSet = pagePreparedStatement.executeQuery();
                 ResultSet countResultSet = countPreparedStatement.executeQuery();) {
                while (pageResultSet.next()) {
                    Long id = pageResultSet.getLong("id");
                    String firstName = pageResultSet.getString("first_name");
                    String lastName = pageResultSet.getString("last_name");
                    String email = pageResultSet.getString("email");
                    String password = pageResultSet.getString("password");
                    User user = new User(firstName, lastName, email, password);
                    user.setId(id);
                    userList.add(user);
                }
                int totalCount = 0;
                if (countResultSet.next()) {
                    totalCount = countResultSet.getInt("count");
                }

                return new Page<>(userList, totalCount);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User findOne(Long id) {
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id=?");)
        {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                User u = new User(firstName, lastName, email);
                u.setId(id);
                return u;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement("select * from users");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User user = new User(firstName, lastName, email, password);
                user.setId(id);
                user.setPassword(user.decryptPassword());
                users.add(user);
            }
            return users;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> save(User entity) {
        validator.validate(entity);
        try(Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement statement  = connection.prepareStatement("INSERT INTO users(first_name,last_name,email, password) VALUES (?,?,?,?)");)
        {
            statement.setString(1,entity.getFirstName());
            statement.setString(2,entity.getLastName());
            statement.setString(3,entity.getEmail());
            statement.setString(4, entity.encryptPassword());
            int affectedRows = statement.executeUpdate();
            return affectedRows!=0? Optional.empty():Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> delete(Long id) {
        try(Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement statement  = connection.prepareStatement("DELETE FROM users WHERE ID = ?");)
        {
            Optional<User> cv = Optional.ofNullable(findOne(id));
            statement.setLong(1,id);
            int affectedRows = statement.executeUpdate();
            return affectedRows==0? Optional.empty():cv;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> update(User entity) {
        try(Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement statement  = connection.prepareStatement("UPDATE users SET first_name = ?, last_name = ?, email = ? WHERE id = ?");)
        {
            statement.setString(1,entity.getFirstName());
            statement.setString(2,entity.getLastName());
            statement.setString(3,entity.getEmail());
            statement.setLong(4,entity.getId());
            int affectedRows = statement.executeUpdate();
            return affectedRows!=0? Optional.empty():Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return findAll(pageable, null);
    }
}
