package com.cloudarchproject.backend.workspace;

import com.cloudarchproject.backend.collaboration.CollaboratorRole;
import com.cloudarchproject.backend.collaboration.WorkspaceCollaborator;
import com.cloudarchproject.backend.collaboration.WorkspaceCollaboratorRepository;
import com.cloudarchproject.backend.user.User;
import com.cloudarchproject.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    @Autowired
    private WorkspaceCollaboratorRepository collaboratorRepo;
    @Autowired
    private UserRepository userRepository;


    public List<WorkspaceResponse> getAllWorkspacesByUser(User user) {
        List<Workspace> workspaces = workspaceRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return workspaces.stream()
                .map(WorkspaceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<WorkspaceResponse> getAllWorkspacesForUser(User user) {
        // Workspaces owned by the user
        List<Workspace> owned = workspaceRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        // Workspaces where user is a collaborator
        List<WorkspaceCollaborator> collaborations = collaboratorRepo.findByUserId((user.getId()));
        Set<Workspace> all = new HashSet<>(owned);
        for (WorkspaceCollaborator collab : collaborations) {
            all.add(collab.getWorkspace());
        }

        return all.stream()
                .map(WorkspaceResponse::fromEntity)
                .sorted(Comparator.comparing(WorkspaceResponse::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }


    public WorkspaceResponse createWorkspace(WorkspaceRequest request, User user) {
        if (request.getWorkspaceName() == null || request.getWorkspaceName().trim().isEmpty()) {
            throw new IllegalArgumentException("Workspace name cannot be empty");
        }
        if (request.getWorkspaceDescription() == null || request.getWorkspaceDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Workspace description cannot be empty");
        }

        // Create workspace entity
        Workspace workspace = Workspace.builder()
                .workspaceName(request.getWorkspaceName().trim())
                .workspaceDescription(request.getWorkspaceDescription().trim())
                .user(user)
                .build();

        Workspace savedWorkspace = workspaceRepository.save(workspace);
        return WorkspaceResponse.fromEntity(savedWorkspace);
    }

    public WorkspaceResponse getWorkspaceById(Long workspaceId, User user) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found"));

        boolean isOwner = workspace.getUser().getId().equals(user.getId());
        boolean isCollaborator = collaboratorRepo.existsByWorkspaceIdAndUserId(workspaceId, user.getId());

        if (!isOwner && !isCollaborator) {
            throw new IllegalArgumentException("Workspace not found or you don't have permission to access it");
        }

        return WorkspaceResponse.fromEntity(workspace);
    }

    public WorkspaceResponse updateWorkspace(Long workspaceId, WorkspaceRequest request, User user) {
        // Validate input
        if (request.getWorkspaceName() == null || request.getWorkspaceName().trim().isEmpty()) {
            throw new IllegalArgumentException("Workspace name cannot be empty");
        }
        if (request.getWorkspaceDescription() == null || request.getWorkspaceDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Workspace description cannot be empty");
        }

        Workspace workspace = workspaceRepository.findByIdAndUserId(workspaceId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found or you don't have permission to access it"));

        workspace.setWorkspaceName(request.getWorkspaceName().trim());
        workspace.setWorkspaceDescription(request.getWorkspaceDescription().trim());

        Workspace updatedWorkspace = workspaceRepository.save(workspace);

        return WorkspaceResponse.fromEntity(updatedWorkspace);
    }

    public void deleteWorkspace(Long workspaceId, User user) {
        if (!workspaceRepository.existsByIdAndUserId(workspaceId, user.getId())) {
            throw new IllegalArgumentException("Workspace not found or you don't have permission to delete it");
        }

        workspaceRepository.deleteById(workspaceId);
    }

    public void addCollaborator(Long workspaceId, Long userIdToAdd, User requestingUser) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found"));
        // Only owner can add
        if (!workspace.getUser().getId().equals(requestingUser.getId())) {
            throw new SecurityException("Only owner can add collaborators");
        }
        User userToAdd = userRepository.findById(userIdToAdd)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (collaboratorRepo.existsByWorkspaceIdAndUserId(workspaceId, userIdToAdd)) {
            throw new IllegalArgumentException("User already a collaborator");
        }
        WorkspaceCollaborator collaborator = WorkspaceCollaborator.builder()
                .workspace(workspace)
                .user(userToAdd)
                .role(CollaboratorRole.COLLABORATOR)
                .build();
        collaboratorRepo.save(collaborator);
    }

    public List<User> getCollaborators(Long workspaceId, User requestingUser) {
        // Optional: check permission
        List<WorkspaceCollaborator> collabs = collaboratorRepo.findByWorkspaceId(workspaceId);
        return collabs.stream().map(WorkspaceCollaborator::getUser).toList();
    }
}