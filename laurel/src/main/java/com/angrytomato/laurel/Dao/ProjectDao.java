package com.angrytomato.laurel.Dao;

import com.angrytomato.laurel.domain.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectDao extends JpaRepository<Storage, Long> {
    List<Storage> findByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    Storage findByIdAndUserIdAndIsDeleted(Long id, Long userId, Boolean isDeleted);

    @Query(value = "select s from Storage s where s.userId=:userId and (s.projectName like %:criteria%) and s.isDeleted=:isDeleted")
    List<Storage> findByCriteria(@Param("userId") Long userId, @Param("criteria") String criteria, @Param("isDeleted") Boolean isDeleted);
}
