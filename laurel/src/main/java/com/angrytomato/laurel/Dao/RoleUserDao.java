package com.angrytomato.laurel.Dao;

import com.angrytomato.laurel.domain.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleUserDao extends JpaRepository<RoleUser, Long> {

}
