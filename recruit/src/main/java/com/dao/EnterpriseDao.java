package com.dao;

import com.pojo.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EnterpriseDao  extends JpaRepository<Enterprise,String>,JpaSpecificationExecutor<Enterprise> {
    List<Enterprise> findByIshot(String ishot);
}
