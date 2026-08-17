package com.abinash.campus_management.repository;

import com.abinash.campus_management.entity.EventRegistrations;
import com.abinash.campus_management.entity.Events;
import com.abinash.campus_management.entity.Students;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistrations, Long> {

    boolean existsByEventAndStudent(Events event, Students student);

    Optional<EventRegistrations> findByEventAndStudent(Events event, Students student);

    List<EventRegistrations> findByEvent(Events event);
}
