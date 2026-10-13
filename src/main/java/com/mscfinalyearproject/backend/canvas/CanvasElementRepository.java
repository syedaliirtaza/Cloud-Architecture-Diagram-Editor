package com.mscfinalyearproject.backend.canvas;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CanvasElementRepository extends JpaRepository<CanvasElement, UUID> {
    List<CanvasElement> findByWorkspaceId(Long workspaceId);
}
