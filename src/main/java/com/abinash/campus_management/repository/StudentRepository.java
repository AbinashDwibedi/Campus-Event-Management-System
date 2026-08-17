package com.abinash.campus_management.repository;

import com.abinash.campus_management.entity.MyUser;
import com.abinash.campus_management.entity.Students;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Students, Long> {
    boolean existsByUser(MyUser loggedUserName);

    Optional<Students> findByUser_Name(String name);

    Optional<Students> findByRollNumber(String rollNumber);

    Page<Students> findByDepartmentAndJoiningYear(String department, String joiningYear, Pageable pageable);
}
