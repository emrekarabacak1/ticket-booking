package com.example.demo.repository;

import com.example.demo.entity.Category;
import com.example.demo.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCategoryId(Long categoryId);

    List<Event> findByDateAfter(LocalDateTime date);

}
