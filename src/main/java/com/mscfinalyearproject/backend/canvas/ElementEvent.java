package com.mscfinalyearproject.backend.canvas;

import lombok.Data;

@Data
public class ElementEvent {
    private String action;
    private ElementPayload element;
    private Long workspaceId;
    private Long userId;
}
