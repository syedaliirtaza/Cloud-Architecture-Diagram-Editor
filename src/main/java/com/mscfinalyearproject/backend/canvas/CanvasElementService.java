package com.mscfinalyearproject.backend.canvas;

import com.mscfinalyearproject.backend.user.User;
import com.mscfinalyearproject.backend.user.UserRepository;
import com.mscfinalyearproject.backend.workspace.Workspace;
import com.mscfinalyearproject.backend.workspace.WorkspaceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CanvasElementService {

    private final CanvasElementRepository elementRepo;
    private final WorkspaceRepository workspaceRepo;
    private final UserRepository userRepo;

    public CanvasElement getElement(UUID id) {
        return elementRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Element not found"));
    }

    public CanvasElement saveElement(CanvasElement element) {
        return elementRepo.save(element);
    }

    @Transactional
    public CanvasElement handleElementEvent(ElementEvent event) {

        Workspace workspace = workspaceRepo.findById(event.getWorkspaceId())
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found"));
        User user = userRepo.findById(event.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ElementPayload payload = event.getElement();

        switch (event.getAction()) {
            case "add":
                CanvasElement newElement = CanvasElement.builder()
                        .id(payload.getId())
                        .workspace(workspace)
                        .type(payload.getType())
                        .data(payload.getData())
                        .createdBy(user)
                        .build();
                return elementRepo.save(newElement);
            case "update":
                CanvasElement existing = elementRepo.findById(payload.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Element not found"));
                existing.setData(payload.getData());
                return elementRepo.save(existing);
            case "delete":
                elementRepo.deleteById(payload.getId());
                return null;
            default:
                throw new IllegalArgumentException("Unknown action: " + event.getAction());
        }
    }
}
