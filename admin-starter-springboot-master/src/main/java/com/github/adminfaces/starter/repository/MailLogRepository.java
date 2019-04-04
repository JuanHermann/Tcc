package com.github.adminfaces.starter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.adminfaces.starter.model.MailLog;


public interface MailLogRepository extends JpaRepository<MailLog, Integer> {

}
