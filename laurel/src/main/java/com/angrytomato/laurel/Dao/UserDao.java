package com.angrytomato.laurel.Dao;

import com.angrytomato.laurel.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Long> {
    @Query(value = "select u from User u where u.username=:username")
    User findByUsername(@Param("username") String username);

    boolean existsByUsername(String username);
}
