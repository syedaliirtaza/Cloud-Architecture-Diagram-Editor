package com.cloudarchproject.backend.canvas;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CanvasWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final CanvasElementService elementService;

    @MessageMapping("/workspace/{workspaceId}/element")
    public void handleElementEvent(@DestinationVariable Long workspaceId,
                                   @Payload ElementEvent event) {
        // Hereeeee we can persist changes only for add/update/delete operations onlysss
        switch (event.getAction()) {
            case "add", "update", "delete" -> elementService.handleElementEvent(event);
        }

        // this will broadcast to other users in same workspace
        messagingTemplate.convertAndSend(
                "/topic/workspace/" + workspaceId + "/elements",
                event
        );

//        System.out.println("this is event we receievd" + event);
    }

    @MessageMapping("/workspace/{workspaceId}/cursor")
    public void handleCursorEvent(@DestinationVariable Long workspaceId,
                                  @Payload CursorEvent cursorEvent) {
        // no persistence will happen here just live events in real time
        messagingTemplate.convertAndSend(
                "/topic/workspace/" + workspaceId + "/cursors",
                cursorEvent
        );
    }

    @MessageMapping("/workspace/{workspaceId}/container/{containerId}/dsl")
    public void handleDSLUpdate(
            @DestinationVariable Long workspaceId,
            @DestinationVariable UUID containerId,
            @Payload Map<String, String> body
    ) {
        CanvasElement el = elementService.getElement(containerId);
        el.getData().put("dsl", body.get("dsl"));
        elementService.saveElement(el);
        // bbroadcast
        messagingTemplate.convertAndSend(
                "/topic/workspace/" + workspaceId + "/container/" + containerId + "/dsl",
                body
        );
    }
}
