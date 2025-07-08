package com.mscfinalyearproject.backend.workspace;

import com.mscfinalyearproject.backend.user.User;
import com.mscfinalyearproject.backend.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard/ws")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @GetMapping
    public ResponseEntity<List<WorkspaceResponse>> getAllWorkspaces(@AuthenticationPrincipal User user) {
        List<WorkspaceResponse> workspaces = workspaceService.getAllWorkspacesForUser(user);
        return ResponseEntity.ok(workspaces);
    }


    @PostMapping
    public ResponseEntity<WorkspaceResponse> createWorkspace(
            @RequestBody WorkspaceRequest request,
            @AuthenticationPrincipal User user) {

        System.out.println("User: " + user);
        System.out.println("User ID: " + (user != null ? user.getId() : "null"));
        System.out.println("Request: " + request);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            WorkspaceResponse workspace = workspaceService.createWorkspace(request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(workspace);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponse> getWorkspace(
            @PathVariable Long workspaceId,
            @AuthenticationPrincipal User user) {
        try {
            WorkspaceResponse workspace = workspaceService.getWorkspaceById(workspaceId, user);
            return ResponseEntity.ok(workspace);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponse> updateWorkspace(
            @PathVariable Long workspaceId,
            @RequestBody WorkspaceRequest request,
            @AuthenticationPrincipal User user) {
        try {
            WorkspaceResponse workspace = workspaceService.updateWorkspace(workspaceId, request, user);
            return ResponseEntity.ok(workspace);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<Void> deleteWorkspace(
            @PathVariable Long workspaceId,
            @AuthenticationPrincipal User user) {
        try {
            workspaceService.deleteWorkspace(workspaceId, user);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{workspaceId}/collaborators")
    public ResponseEntity<?> addCollaborator(
            @PathVariable Long workspaceId,
            @RequestParam Long userId,
            @AuthenticationPrincipal User user
    ) {
        try {
            workspaceService.addCollaborator(workspaceId, userId, user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{workspaceId}/collaborators")
    public ResponseEntity<List<UserResponse>> getCollaborators(
            @PathVariable Long workspaceId,
            @AuthenticationPrincipal User user
    ) {
        List<User> users = workspaceService.getCollaborators(workspaceId, user);
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(responses);
    }

}