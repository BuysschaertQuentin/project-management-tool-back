package com.iscod.project_management_tool_back.repository;

import com.iscod.project_management_tool_back.entity.PmtUserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPmtUserRepository extends JpaRepository<PmtUserDto, Long> {

    Optional<PmtUserDto> findByEmail(String email);

    Optional<PmtUserDto> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<PmtUserDto> findByEmailIn(List<String> emails);

    List<PmtUserDto> findByUsernameOrEmailContaining(String search, String email);
}
