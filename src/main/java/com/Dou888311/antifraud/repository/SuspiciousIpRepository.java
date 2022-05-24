package com.Dou888311.antifraud.repository;

import com.Dou888311.antifraud.Entity.IP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuspiciousIpRepository extends JpaRepository<IP, Long> {
    Boolean existsByIp(String ip);

    IP findIPByIp(String ip);
}
