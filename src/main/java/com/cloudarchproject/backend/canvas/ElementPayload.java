package com.cloudarchproject.backend.canvas;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class ElementPayload {
    private UUID id;
    private String type;
    private Map<String, Object> data;
}
