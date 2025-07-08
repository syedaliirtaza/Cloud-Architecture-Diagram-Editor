package com.mscfinalyearproject.backend.workspace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceRequest {
    private String workspaceName;
    private String workspaceDescription;
}