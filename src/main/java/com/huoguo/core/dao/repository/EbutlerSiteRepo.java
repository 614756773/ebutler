package com.huoguo.core.dao.repository;

import com.huoguo.model.entity.EbutlerSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EbutlerSiteRepo extends JpaRepository<EbutlerSite, Integer> {
    EbutlerSite findByName(String name);
}
