package com.abinash.campus_management.repository;

import com.abinash.campus_management.entity.Events;
import com.abinash.campus_management.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Events, Long> {

    Page<Events> findByStatusOrderByStartTimeAsc(Status status, Pageable pageable);

    List<Events> findByClub_Id(Long clubId);

    @Query("SELECT COUNT(r) FROM EventRegistrations r WHERE r.event.id = :eventId")
    int countRegistrationsByEventId(Long eventId);
}
