package com.iscod.project_management_tool_back.repository;

import com.iscod.project_management_tool_back.entity.PmtUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPmtUserRepository extends JpaRepository<PmtUser, Long> {

    Optional<PmtUser> findByEmail(String email);

    Optional<PmtUser> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<PmtUser> findByEmailIn(List<String> emails);

    List<PmtUser> findByUsernameOrEmailContaining(String search, String email);
}
