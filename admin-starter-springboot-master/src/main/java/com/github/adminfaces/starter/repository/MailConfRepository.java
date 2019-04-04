package com.github.adminfaces.starter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.adminfaces.starter.model.MailConf;


public interface MailConfRepository extends JpaRepository<MailConf, Integer> {

}
