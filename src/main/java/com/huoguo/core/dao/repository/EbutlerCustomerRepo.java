package com.huoguo.core.dao.repository;

import com.huoguo.model.entity.EbutlerCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EbutlerCustomerRepo extends JpaRepository<EbutlerCustomer, Integer> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM ebutler_customer WHERE site_id IN :siteIds", nativeQuery = true)
    void deleteBySiteIds(@Param("siteIds") List<Integer> siteIds);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM ebutler_customer WHERE line_id IN :lineIds", nativeQuery = true)
    void deleteByLineIds(@Param("lineIds") List<Integer> lineIds);

    @Transactional
    @Modifying
    @Query(value = "UPDATE ebutler_customer SET site_name = ?2 WHERE site_id = ?1", nativeQuery = true)
    void updateSiteName(@Param("siteId") Integer siteId, @Param("siteName") String siteName);

    @Transactional
    @Modifying
    @Query(value = "UPDATE ebutler_customer SET line_name = :lineName WHERE line_id = :lineId", nativeQuery = true)
    void updateLineName(@Param("lineId") Integer lineId, @Param("lineName") String lineName);
}
