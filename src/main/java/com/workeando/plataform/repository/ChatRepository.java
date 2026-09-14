package com.workeando.plataform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workeando.plataform.model.Chat;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
}