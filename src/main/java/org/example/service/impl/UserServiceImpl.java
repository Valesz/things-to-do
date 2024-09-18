package org.example.service.impl;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.utils.UserStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource(name = "userRepository")
    UserRepository userRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public Iterable<User> getByUsersObject(User user) {

        if (user == null) {
            return userRepository.findAll();
        }

        SqlParameterSource namedParams = new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("username", user.getUsername())
                .addValue("email", user.getEmail())
                .addValue("timeofcreation", user.getTimeofcreation() == null ? null : user.getTimeofcreation().toString())
                .addValue("status", user.getStatus() == null ? null : user.getStatus().toString())
                .addValue("password", user.getPassword())
                .addValue("classification", user.getClassification())
                .addValue("precisionofanswers", user.getPrecisionofanswers());

        String query = constructQueryByOwnObject(user);

        return namedParameterJdbcTemplate.query(query, namedParams, rs -> {
            List<User> userList = new ArrayList<>();

            while (rs.next()) {
                userList.add(User.builder()
                        .id(rs.getLong("id"))
                        .username(rs.getString("username"))
                        .email(rs.getString("email"))
                        .timeofcreation(LocalDate.parse(rs.getString("timeofcreation")))
                        .status(UserStatusEnum.valueOf(rs.getString("status")))
                        .password(rs.getString("password"))
                        .classification(rs.getDouble("classification"))
                        .precisionofanswers(rs.getDouble("precisionofanswers"))
                        .build()
                );
            }

            return userList;
        });

    }

    private String constructQueryByOwnObject(User user) {
        StringBuilder query = new StringBuilder(" SELECT * FROM \"user\" ");

        query.append(" WHERE 1 = 1 ");

        if (user.getId() != null) {
            query.append(" AND id = :id ");
        }

        if (user.getUsername() != null) {
            query.append(" AND username = :username ");
        }

        if (user.getEmail() != null) {
            query.append(" AND email = :email ");
        }

        if (user.getTimeofcreation() != null) {
            query.append(" AND timeofcreation = :timeofcreation ");
        }

        if (user.getStatus() != null) {
            query.append(" AND status = :status ");
        }

        if (user.getClassification() != null) {
            query.append(" AND classification = :classification ");
        }

        if (user.getPrecisionofanswers() != null) {
            query.append(" AND precisionofanswers = :precisionofanswers ");
        }

        return query.toString();

    }

    @Override
    public User saveUser(User user) {
        if (user.getId() != null) {
            throw new IllegalArgumentException("Remove id property, or use Update instead of Save.");
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User with id " + user.getId() + " doesn't exist. Please use save to save this instance.");
        }

        return userRepository.save(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Deletion of " + id + " user failed: " + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public boolean deleteAll() {
        try {
            userRepository.deleteAll();
        } catch (Exception e) {
            System.out.println("Deletion of all users failed: " + e.getMessage());
            return false;
        }

        return true;
    }

}
