package com.huoguo.core.dao.repository;

import com.huoguo.model.entity.EbutlerLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EbutlerLineRepo extends JpaRepository<EbutlerLine, Integer> {
    @Query(value = "select * from ebutler_line where site_id = :siteId", nativeQuery = true)
    List<EbutlerLine> findBySiteId(@Param("siteId") Integer siteId);

    EbutlerLine findByName(String name);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM ebutler_line WHERE site_id IN :siteIds", nativeQuery = true)
    void deleteBySiteIds(@Param("siteIds") List<Integer> siteIds);
}
