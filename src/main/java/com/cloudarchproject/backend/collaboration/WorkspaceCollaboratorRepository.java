package com.cloudarchproject.backend.collaboration;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkspaceCollaboratorRepository extends JpaRepository<WorkspaceCollaborator, Long> {
    List<WorkspaceCollaborator> findByWorkspaceId(Long workspaceId);
    boolean existsByWorkspaceIdAndUserId(Long workspaceId, Long userId);

    List<WorkspaceCollaborator> findByUserId(Long userId);
}
