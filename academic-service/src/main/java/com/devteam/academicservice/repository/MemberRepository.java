package com.devteam.academicservice.repository;

import com.devteam.academicservice.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
}
