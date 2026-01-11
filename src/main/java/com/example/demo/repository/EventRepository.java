package com.example.demo.repository;

import com.example.demo.entity.Category;
import com.example.demo.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.category WHERE e.category.id = :categoryId")
    List<Event> findByCategoryId(Long categoryId);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.category WHERE e.date > :date")
    List<Event> findByDateAfter(LocalDateTime date);

    @Override
    @Query("select e from Event e left join fetch e.category")
    List<Event> findAll();
}
