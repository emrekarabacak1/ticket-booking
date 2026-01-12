package com.example.demo.repository;

import com.example.demo.entity.Category;
import com.example.demo.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Query(value = "select e from Event e left join fetch e.category",
            countQuery = "select COUNT(e) FROM Event e")
    Page<Event> findAll(Pageable pageable);
}
