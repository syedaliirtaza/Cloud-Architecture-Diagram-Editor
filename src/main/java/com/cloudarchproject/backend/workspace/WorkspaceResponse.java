package com.cloudarchproject.backend.workspace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


// Response DTO for workspace
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceResponse {
    private Long id;
    private String workspaceName;
    private String workspaceDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;

    public static WorkspaceResponse fromEntity(Workspace workspace) {
        return WorkspaceResponse.builder()
                .id(workspace.getId())
                .workspaceName(workspace.getWorkspaceName())
                .workspaceDescription(workspace.getWorkspaceDescription())
                .createdAt(workspace.getCreatedAt())
                .updatedAt(workspace.getUpdatedAt())
                .createdBy(workspace.getUser().getId())
                .build();
    }
}