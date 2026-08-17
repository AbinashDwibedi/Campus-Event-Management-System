package com.abinash.campus_management.repository;

import com.abinash.campus_management.entity.ClubMemberships;
import com.abinash.campus_management.entity.Clubs;
import com.abinash.campus_management.entity.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubMembershipRepository extends JpaRepository<ClubMemberships, Long> {

    boolean existsByClubAndUser(Clubs club, MyUser user);

    List<ClubMemberships> findByUser(MyUser user);

    List<ClubMemberships> findByClub(Clubs club);

    Optional<ClubMemberships> findByClubAndUser(Clubs club, MyUser user);

    Optional<ClubMemberships> findByClubAndUser_Id(Clubs club, Long userId);
}
