package org.example.service.impl;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.utils.UserStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    @Resource(name = "userRepository")
    UserRepository userRepository;
    @Qualifier("namedParameterJdbcTemplate")
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
            return null;
        }

        SqlParameterSource namedParams = new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("username", user.getUsername())
                .addValue("email", user.getEmail())
                .addValue("timeofcreation", user.getTimeofcreation())
                .addValue("status", user.getStatus())
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
                        .classification(user.getClassification())
                        .precisionofanswers(user.getPrecisionofanswers())
                        .build()
                );
            }

            return userList;
        });

    }

    private String constructQueryByOwnObject(User user) {
        StringBuilder query = new StringBuilder(" SELECT * FROM \"user\" ");

        if (user.getId() != null) {
            query.append(" WHERE \"id\" = :id ");
        }

        if (user.getUsername() != null) {
            query.append(" WHERE \"username\" = :username ");
        }

        if (user.getEmail() != null) {
            query.append(" WHERE \"email\" = :email ");
        }

        if (user.getTimeofcreation() != null) {
            query.append(" WHERE \"timeofcreation\" = :timeofcreation ");
        }

        if (user.getStatus() != null) {
            query.append(" WHERE \"status\" = :status ");
        }

        if (user.getClassification() != null) {
            query.append(" WHERE \"classification\" = :classification ");
        }

        if (user.getPrecisionofanswers() != null) {
            query.append(" WHERE \"precisionofanswers\" = :precisionofanswers ");
        }

        return query.toString();

    }

    @Override
    public User saveUser(User user) {
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

    @Override
    public User populateUser() {
        String[] randomNames = new String[]{"Léna", "Rebeka", "Valentin", "Laci", "Ádám", "Bálint", "Keve", "Zsolt", "Panni", "Erzsók", "Gergő", "Milán"};

        Random random = new Random();

        return User.builder()
                .username(randomNames[random.nextInt(0, randomNames.length)])
                .email("asd@asd.asd")
                .timeofcreation(LocalDate.now())
                .status(UserStatusEnum.AKTIV)
                .password("asd")
                .classification(random.nextDouble())
                .precisionofanswers(random.nextDouble())
                .build();
    }

}
