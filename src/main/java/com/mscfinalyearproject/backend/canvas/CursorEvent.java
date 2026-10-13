package com.mscfinalyearproject.backend.canvas;

import lombok.Data;

@Data
public class CursorEvent {
    private Long workspaceId;
    private Long userId;
    private double x;
    private double y;
}
