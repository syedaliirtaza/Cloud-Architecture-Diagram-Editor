package com.mscfinalyearproject.backend.canvas;

import java.util.Map;
import java.util.UUID;

public record CanvasElementDto(
        UUID id,
        String type,
        Map<String, Object> data
) {}
