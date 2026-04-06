package com.orion.engineering.internetofeverything.console;

import com.orion.engineering.core.json.JSONService;
import com.orion.engineering.internetofeverything.communication.ThingPayloadReceiver;
import com.orion.engineering.internetofeverything.communication.pubsub.IoEPubSubTopic;
import com.orion.engineering.internetofeverything.thing.payload.ThingPayload;
import com.orion.engineering.internetofeverything.thing.payload.ThingPayloadObserver;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ThingPayloadPage implements ThingPayloadObserver
{
    private final ThingPayloadReceiver thingPayloadReceiver;
    @Autowired private SimpMessagingTemplate messagingTemplate;
    private boolean shouldShowMessagesLIVE;


    public ThingPayloadPage(ThingPayloadReceiver thingPayloadReceiver)
    {
        this.shouldShowMessagesLIVE = true;
        this.thingPayloadReceiver = thingPayloadReceiver;
        thingPayloadReceiver.addObserver(this);
    }


    @PreDestroy
    public void onDestroy()
    {
        thingPayloadReceiver.removeObserver(this);
    }


    public void pause()
    {
        this.shouldShowMessagesLIVE = false;
    }


    @Override
    public void onPayloadReceived(ThingPayload payload)
    {
        if(shouldShowMessagesLIVE)
        {
            String messageToPublish = JSONService.convertObjectToJSON(
                            ThingWebsocketMessage.builder()
                                            .message(payload.getPayload())
                                            .build()
            );
            messagingTemplate.convertAndSend(IoEPubSubTopic.THING_PAYLOAD_VIEWER_PAYLOAD.get(), messageToPublish);
        }
    }
}
