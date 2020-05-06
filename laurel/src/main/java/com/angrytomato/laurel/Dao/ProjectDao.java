package com.angrytomato.laurel.Dao;

import com.angrytomato.laurel.domain.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectDao extends JpaRepository<Storage, Long> {
}
