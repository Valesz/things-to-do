package org.example.service.impl;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.utils.UserStatusEnum;
import org.example.utils.exceptions.NullValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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
    public User saveUser(User user) throws NullValueException {
        if (user.getId() != null) {
            throw new IllegalArgumentException("Remove id property, or use Update instead of Save.");
        }

        validateUserProperties(user);

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) throws NullValueException {
        if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User with id " + user.getId() + " doesn't exist. Please use save to save this instance.");
        }

        User newUser = setNullProperties(user);

        validateUserProperties(newUser);

        return userRepository.save(newUser);
    }

    private User setNullProperties(User user) {
        User userInDb = userRepository.findById(user.getId()).orElse(new User());

        user.setUsername(user.getUsername() == null ? userInDb.getUsername() : user.getUsername());

        user.setEmail(user.getEmail() == null ? userInDb.getEmail() : user.getEmail());

        user.setTimeofcreation(user.getTimeofcreation() == null ? userInDb.getTimeofcreation() : user.getTimeofcreation());

        user.setStatus(user.getStatus() == null ? userInDb.getStatus() : user.getStatus());

        user.setPassword(user.getPassword() == null ? userInDb.getPassword() : bCryptPasswordEncoder.encode(user.getPassword()));

        user.setClassification(user.getClassification() == null ? userInDb.getClassification() : user.getClassification());

        user.setPrecisionofanswers(user.getPrecisionofanswers() == null ? userInDb.getPrecisionofanswers() : user.getPrecisionofanswers());

        return user;

    }

    private void validateUserProperties(User user) throws NullValueException {
        String errorMessage = checkForNullProperties(user);

        if (!errorMessage.isEmpty()) {
            throw new NullValueException(errorMessage);
        }
    }

    private String checkForNullProperties(User user) {
        StringBuilder errorMessage = new StringBuilder();

        if (user.getUsername() == null) {
            errorMessage.append("username property not set, ");
        }

        if (user.getEmail() == null) {
            errorMessage.append("email property not set, ");
        }

        if (user.getTimeofcreation() == null) {
            errorMessage.append("timeofcreation property not set, ");
        }

        if (user.getStatus() == null) {
            errorMessage.append("status property not set, ");
        }

        if (user.getPassword() == null) {
            errorMessage.append("password property not set, ");
        }

        if (user.getClassification() == null) {
            errorMessage.append("classification property not set, ");
        }

        if (user.getPrecisionofanswers() == null) {
            errorMessage.append("precisionofanswers property not set, ");
        }

        return errorMessage.toString();
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
