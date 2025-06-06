package com.appsdeveloperblog.tutorials.junit.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.UUID;

@DataJpaTest
public class UserRepositoryTest {


    private final String userId1 = UUID.randomUUID().toString();
    private final String userId2 = UUID.randomUUID().toString();
    private final String email1 = "test@test.com";
    private final String email2 = "test2@test.com";
    @Autowired
    TestEntityManager testEntityManager;
    @Autowired
    UsersRepository usersRepository;

    @BeforeEach
    void Setup() {
        UserEntity user = new UserEntity();
        user.setFirstName("Sergey");
        user.setLastName("Kargopolov");
        user.setEmail(email1);
        user.setUserId(userId1);
        user.setEncryptedPassword("123456789");
        testEntityManager.persistAndFlush(user);
        UserEntity user2 = new UserEntity();
        user2.setFirstName("John");
        user2.setLastName("Sears");
        user2.setEmail(email2);
        user2.setUserId(userId2);
        user2.setEncryptedPassword("abcdefghij");
        testEntityManager.persistAndFlush(user2);


    }

    @Test
    void testFindByEmail_wheGivenCorrectEmail_returnsUserEntity() {
        //Arrange
        UserEntity user = new UserEntity();
        user.setFirstName("Sergey");
        user.setLastName("Kargopolov");
        user.setEmail("test@test.com");
        user.setUserId(UUID.randomUUID().toString());
        user.setEncryptedPassword("123456789");
        testEntityManager.persistAndFlush(user);
        //Act
        UserEntity storedUser = usersRepository.findByEmail(user.getEmail());

        //Assert
        Assertions.assertEquals(user.getEmail(), storedUser.getEmail(), "Returned email address does not match the expected value");
    }

    @Test
    void testFindById_whenGivenCorrectUserId_returnsUserEntity() {
        // Act
        UserEntity storedUser = usersRepository.findByUserId(userId2);
        //Assert & Act
        Assertions.assertNotNull(storedUser, "UserEntity object should not be null");
        Assertions.assertEquals(userId2, storedUser.getUserId(), "Returned userId does not match expected value");
    }


    @Test
    void testFindUserWithEmailEndsWith_whenGivEmailDomain_returnsUsersWithGivenDomain() {
        //Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(UUID.randomUUID().toString());
        userEntity.setEmail("test@gmail.com");
        userEntity.setEncryptedPassword("123456789");
        userEntity.setFirstName("Sergey");
        userEntity.setLastName("Kargopolov");
        testEntityManager.persistAndFlush(userEntity);


        String emailDomainName = "@gmail.com";
        //Act
        List<UserEntity> users = usersRepository.findUserWithEmailEndingWith(emailDomainName);


        //Assert
        Assertions.assertEquals(1 ,users.size(), "There should be only one user on the list");
        Assertions.assertTrue(users.get(0).getEmail().endsWith(emailDomainName));
    }

}
