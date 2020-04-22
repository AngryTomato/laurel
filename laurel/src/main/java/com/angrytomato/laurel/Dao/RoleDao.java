package com.angrytomato.laurel.Dao;

import com.angrytomato.laurel.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleDao extends JpaRepository<Role, Long> {
    @Query(value = "select r from Role r where r.id in (select ru.roleId from RoleUser ru where ru.userId=:userId)")
    List<Role> findRoleByUserId(@Param("userId") Long userId);
}
