package com.abinash.campus_management.repository;

import com.abinash.campus_management.entity.Clubs;
import com.abinash.campus_management.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubRepository extends JpaRepository<Clubs, Long> {

    List<Clubs> findByIsActiveTrue();

    List<Clubs> findByIsActiveTrueAndCategory(Category category);

    boolean existsByName(String name);

    boolean existsByClubCode(String clubCode);

    long countByIsActiveTrue();
}
