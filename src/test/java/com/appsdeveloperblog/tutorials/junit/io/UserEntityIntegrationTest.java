package com.appsdeveloperblog.tutorials.junit.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class UserEntityIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;


    UserEntity userEntity;

    @BeforeEach
    void setup() {
        userEntity = new UserEntity();
        userEntity.setUserId("1");
        userEntity.setFirstName("Sergey");
        userEntity.setLastName("Kargopolov");
        userEntity.setEmail("test@test.com");
        userEntity.setEncryptedPassword("12345678");
    }

    @Test
    void testUserEntity_whenValidDetailsProvided_shouldReturnStoredUserDetails() {
        //Arrange

        //Act
        UserEntity storedUserEntity = testEntityManager.persistAndFlush(userEntity);

        //Assert

        Assertions.assertTrue(storedUserEntity.getId() > 0);
        Assertions.assertEquals(userEntity.getUserId(), storedUserEntity.getUserId());
        Assertions.assertEquals(userEntity.getFirstName(), storedUserEntity.getFirstName());
        Assertions.assertEquals(userEntity.getLastName(), storedUserEntity.getLastName());
        Assertions.assertEquals(userEntity.getEmail(), storedUserEntity.getEmail());
        Assertions.assertEquals(userEntity.getEncryptedPassword(), storedUserEntity.getEncryptedPassword());

    }

    @Test
    void testUserEntity_whenFirstNameIsTooLong_shouldThrowException() {
        //Arrange
        userEntity.setFirstName("1234568795644564654655646454654651235465789754646798879798456487941654987564987456498745649879");
        //Assert & Act
        assertThrows(PersistenceException.class, () -> {
            testEntityManager.persistAndFlush(userEntity);
        }, "Was expecting a PersistenceException to be thrown");
    }

    @Test
    void testUserEntity_whenUserIDisNotUnique_shouldThrowException() {
        //Arrange
        UserEntity newEntity = new UserEntity();
        newEntity.setUserId("1");
        newEntity.setFirstName("Sergey");
        newEntity.setLastName("Kargopolov");
        newEntity.setEmail("test@test.com");
        newEntity.setEncryptedPassword("12345678");
        testEntityManager.persistAndFlush(newEntity);
        //Assert & Act
        userEntity.setUserId("1");

        assertThrows(PersistenceException.class, () -> {
            testEntityManager.persistAndFlush(userEntity);
        }, "Was expecting a PersistenceException to be thrown");
    }


}
