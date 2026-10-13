package com.cloudarchproject.backend.workspace;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

    @Query("SELECT w FROM Workspace w WHERE w.user.id = :userId ORDER BY w.createdAt DESC")
    List<Workspace> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT w FROM Workspace w WHERE w.id = :workspaceId AND w.user.id = :userId")
    Optional<Workspace> findByIdAndUserId(@Param("workspaceId") Long workspaceId, @Param("userId") Long userId);

    @Query("SELECT COUNT(w) > 0 FROM Workspace w WHERE w.id = :workspaceId AND w.user.id = :userId")
    boolean existsByIdAndUserId(@Param("workspaceId") Long workspaceId, @Param("userId") Long userId);
}