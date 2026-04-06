package com.orion.engineering.internetofeverything.console;

import com.orion.engineering.core.json.JSONService;
import com.orion.engineering.internetofeverything.communication.pubsub.IoEPubSubTopic;
import com.orion.engineering.internetofeverything.thing.payload.ThingPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ThingMessagesViewer
{
    //@Autowired @Qualifier(value = "mockDatabase") private Database database;
    @Autowired private SimpMessagingTemplate messagingTemplate;
    private boolean shouldShowMessagesLIVE;


    public ThingMessagesViewer()
    {
        this.shouldShowMessagesLIVE = true;
    }


    public void pause()
    {
        this.shouldShowMessagesLIVE = false;
    }


    public void publishPayloadToWebsocket(ThingPayload payload)
    {
        if(shouldShowMessagesLIVE)
        {
            String messageToPublish = JSONService.convertObjectToJSON(ThingWebsocketMessage.builder()
                            .message(payload.getPayload())
                            .build());
            messagingTemplate.convertAndSend(IoEPubSubTopic.THING_PAYLOAD_VIEWER_PAYLOAD.get(), messageToPublish);
        }
    }
}
