package org.example.repository;

import org.example.AbstractTest;
import org.example.model.User;
import org.example.utils.UserStatusEnum;
import org.junit.After;
import org.junit.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.StreamSupport;


public class UserRepositoryTest extends AbstractTest {

    @Autowired
    private UserRepository userRepository;

    @After
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void addAUserToDbTest() {
        User user = User.builder()
                .username("teszt elek")
                .email("teszt@teszt.teszt")
                .timeofcreation(LocalDate.now())
                .status(UserStatusEnum.AKTIV)
                .password("teszt")
                .classification(0.5)
                .precisionofanswers(0.8)
                .build();

        userRepository.save(user);

        User gotUser = userRepository.findById(1L).orElse(null);

        Assert.assertNotNull(gotUser);

        Assert.assertEquals(user, gotUser);
    }

    @Test
    public void getUsersFromDatabaseTest() {
        User user1 = User.builder()
                .username("teszt elek")
                .email("teszt@teszt.teszt")
                .timeofcreation(LocalDate.now())
                .status(UserStatusEnum.AKTIV)
                .password("teszt")
                .classification(0.5)
                .precisionofanswers(0.8)
                .build();

        User user2 = User.builder()
                .username("Kukor Ica")
                .email("Kukor@Ica.teszt")
                .timeofcreation(LocalDate.now())
                .status(UserStatusEnum.INAKTIV)
                .password("Ica")
                .classification(0.5)
                .precisionofanswers(0.8)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        List<User> listOfUsersInDb = StreamSupport.stream(userRepository.findAll().spliterator(), false).toList();

        Assert.assertEquals(2, listOfUsersInDb.size());
    }

    @Test
    public void getUsersWhoAreNotInDatabaseTest() {
        Assert.assertNull(userRepository.findById(100L).orElse(null));
        Assert.assertNull(userRepository.findById(1L).orElse(null));
        Assert.assertNull(userRepository.findById(-1L).orElse(null));
    }

    @Test
    public void deleteUserFromDatabseTest() {
        User user = User.builder()
                .username("teszt elek")
                .email("teszt@teszt.teszt")
                .timeofcreation(LocalDate.now())
                .status(UserStatusEnum.AKTIV)
                .password("teszt")
                .classification(0.5)
                .precisionofanswers(0.8)
                .build();

        User savedUser = userRepository.save(user);

        Assert.assertNotNull(userRepository.findById(savedUser.getId()).orElse(null));

        userRepository.deleteById(savedUser.getId());

        Assert.assertNull(userRepository.findById(savedUser.getId()).orElse(null));
    }

}
