package com.example.usermanagementapi;

import com.example.usermanagementapi.model.User;
import com.example.usermanagementapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private  UserRepository userRepository;
            @Autowired
 private   TestEntityManager entityManager;
    private User user1;
    private User user2;
    @BeforeEach
    void setUp(){
        userRepository.deleteAllInBatch();
        user1 = new User("Allice Wonderland","allice@example.com");
        user2 = new User("Bod ","Bob@expmple.com");
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();
        entityManager.clear();
    }
@Test
void testFindByEmailFound() {
    Optional<User> foundUser = userRepository.findByEmail(user1.getEmail());

    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getName()).isEqualTo(user1.getName());
    assertThat(foundUser.get().getEmail()).isEqualTo(user1.getEmail());
    assertThat(foundUser.get().getId()).isEqualTo(user1.getId());
}


}
