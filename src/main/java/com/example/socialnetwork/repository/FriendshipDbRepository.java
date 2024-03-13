package com.example.socialnetwork.repository;

import com.example.socialnetwork.domain.FriendRequest;
import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.validators.Validator;
import com.example.socialnetwork.dto.FriendshipFilterDTO;
import com.example.socialnetwork.utils.Pair;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class FriendshipDbRepository implements FriendshipRepository {
    protected String url;
    protected String username;
    protected String password;
    private final Validator<Friendship> validator;

    public FriendshipDbRepository(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    private Pair<String, List<Object>> toSQL(FriendshipFilterDTO filter)
    {
        if(filter == null)
        {
            return new Pair<>("", Collections.emptyList());
        }
        List<String> conditions= new ArrayList<>();
        List<Object> params = new ArrayList<>();

        filter.getId1().ifPresent(idFilter -> {
            conditions.add("user1_id = ?");
            params.add(idFilter);
        });
        filter.getId2().ifPresent(idFilter -> {
            conditions.add("user2_id = ?");
            params.add(idFilter);
        });
        filter.getFriendsFrom().ifPresent(dateFilter -> {
            conditions.add("friends_from = ?");
            params.add(dateFilter);
        });
        filter.getFriendRequestStatus().ifPresent(requestFilter -> {
            conditions.add("friendRequestStatus LIKE ?");
            params.add("%" + requestFilter + "%");
        });

        String sql = String.join(" and ",conditions);
        return new Pair<>(sql,params);

    }

    @Override
    public Page<Friendship> findAll(Pageable pageable, FriendshipFilterDTO filter) {
        List<Friendship> friendshipList = new ArrayList<>();
        String countSQL = "SELECT COUNT(*) AS count FROM friendships";
        String pageSQL = "SELECT * FROM friendships";
        Pair<String,List<Object>> pair = toSQL(filter);
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
                    Long user1_id = pageResultSet.getLong("user1_id");
                    Long user2_id = pageResultSet.getLong("user2_id");
                    LocalDateTime friends_from = pageResultSet.getTimestamp("friends_from").toLocalDateTime();
                    FriendRequest friendRequestStatus = FriendRequest.valueOf(pageResultSet.getString("friendRequestStatus"));

                    Friendship fr = new Friendship(user1_id, user2_id, friends_from, friendRequestStatus);

                    fr.setId(id);
                    friendshipList.add(fr);
                }
                int totalCount = 0;
                if (countResultSet.next()) {
                    totalCount = countResultSet.getInt("count");
                }

                return new Page<>(friendshipList, totalCount);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Friendship findOne(Long id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships WHERE id=?")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long user1_id = resultSet.getLong("user1_id");
                Long user2_id = resultSet.getLong("user2_id");
                LocalDateTime friends_from = resultSet.getTimestamp("friends_from").toLocalDateTime();
                FriendRequest friendRequestStatus = FriendRequest.valueOf(resultSet.getString("friendRequestStatus"));

                Friendship f = new Friendship(user1_id, user2_id, friends_from);
                f.setId(id);
                return f;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendships");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long user1_id = resultSet.getLong("user1_id");
                Long user2_id = resultSet.getLong("user2_id");
                LocalDateTime friends_from = resultSet.getTimestamp("friends_from").toLocalDateTime();
                FriendRequest friendRequestStatus = FriendRequest.valueOf(resultSet.getString("friendRequestStatus"));

                Friendship fr = new Friendship(user1_id, user2_id, friends_from, friendRequestStatus);

                fr.setId(id);
                friendships.add(fr);
            }
            return friendships;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        validator.validate(entity);
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement  = connection.prepareStatement("INSERT INTO friendships(user1_id,user2_id,friends_from, friendRequestStatus) VALUES (?,?,?,?)"))
        {
            statement.setLong(1,entity.getUser1_id());
            statement.setLong(2,entity.getUser2_id());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getFriendsFrom()));
            statement.setString(4, entity.getFriendRequestStatus().toString());
            int affectedRows = statement.executeUpdate();
            return affectedRows!=0? Optional.empty():Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> delete(Long id) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement  = connection.prepareStatement("DELETE FROM friendships WHERE ID = ?");)
        {
            Optional<Friendship> cv = Optional.ofNullable(findOne(id));
            statement.setLong(1,id);
            int affectedRows = statement.executeUpdate();
            return affectedRows==0? Optional.empty():cv;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement  = connection.prepareStatement("UPDATE friendships SET user1_id = ?, user2_id = ?, friends_from = ?, friendRequestStatus = ? WHERE id = ?"))
        {
            statement.setLong(1,entity.getUser1_id());
            statement.setLong(2,entity.getUser2_id());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getFriendsFrom()));
            statement.setString(4, entity.getFriendRequestStatus().toString());
            statement.setLong(5,entity.getId());
            int affectedRows = statement.executeUpdate();
            return affectedRows!=0? Optional.empty():Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<Friendship> findAll(Pageable pageable) {
        return findAll(pageable, null);
    }
}
