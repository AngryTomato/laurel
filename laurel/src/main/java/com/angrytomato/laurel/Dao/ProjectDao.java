package com.angrytomato.laurel.Dao;

import com.angrytomato.laurel.domain.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectDao extends JpaRepository<Storage, Long> {
    List<Storage> findByUserId(Long userId);

    Storage findByIdAndUserId(Long id, Long userId);
}
