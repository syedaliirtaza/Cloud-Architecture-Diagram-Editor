package com.mscfinalyearproject.backend.canvas;

import com.mscfinalyearproject.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard/workspaces/{workspaceId}/canvas")
@RequiredArgsConstructor
public class CanvasRestController {
    private final CanvasElementRepository elementRepo;
    private final CanvasElementService canvasElementService;

    @GetMapping
    public List<CanvasElementDto> getAllElements(@PathVariable Long workspaceId) {
        return elementRepo.findByWorkspaceId(workspaceId)
                .stream()
                .map(el -> new CanvasElementDto(el.getId(), el.getType(), el.getData()))
                .toList();
    }

    @GetMapping("/{elementId}")
    public CanvasElementDto getElement(@PathVariable UUID elementId) {
        CanvasElement el = elementRepo.findById(elementId)
                .orElseThrow(() -> new IllegalArgumentException("Element not found"));
        return new CanvasElementDto(el.getId(), el.getType(), el.getData());
    }

    @PostMapping("/sync")
    public ResponseEntity<Void> syncElements(
            @PathVariable Long workspaceId,
            @RequestBody List<ElementPayload> elements,
            @AuthenticationPrincipal User user
    ) {
        for (ElementPayload payload : elements) {
            ElementEvent event = new ElementEvent();
            event.setWorkspaceId(workspaceId);
            event.setUserId(user.getId());
            event.setAction("update");
            event.setElement(payload);

            canvasElementService.handleElementEvent(event);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/canvas/{elementId}")
    public ResponseEntity<Void> deleteElement(@PathVariable UUID elementId) {
        elementRepo.deleteById(elementId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{elementId}/dsl")
    public ResponseEntity<Void> updateContainerDSL(
            @PathVariable UUID elementId,
            @RequestBody Map<String, String> body
    ) {
        String dsl = body.get("dsl");
        CanvasElement el = elementRepo.findById(elementId)
                .orElseThrow(() -> new IllegalArgumentException("Element not found"));
        if (!"diagramContainer".equals(el.getType())) {
            return ResponseEntity.badRequest().build();
        }
        el.getData().put("dsl", dsl); // Store DSL as `data.dsl`
        elementRepo.save(el);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{elementId}/dsl")
    public ResponseEntity<Map<String, String>> getContainerDSL(@PathVariable UUID elementId) {
        CanvasElement el = elementRepo.findById(elementId)
                .orElseThrow(() -> new IllegalArgumentException("Element not found"));
        if (!"diagramContainer".equals(el.getType())) {
            return ResponseEntity.badRequest().build();
        }
        String dsl = (String) el.getData().get("dsl");
        return ResponseEntity.ok(Map.of("dsl", dsl == null ? "" : dsl));
    }
}
