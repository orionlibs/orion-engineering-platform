package com.orion.engineering.internetofeverything.thing;

import com.orion.engineering.internetofeverything.communication.CommunicationProtocol;
import com.orion.engineering.internetofeverything.communication.DataPacket;
import com.orion.engineering.internetofeverything.thing.security.ThingCertificates;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.NotImplementedException;

@Builder
@Getter
public class Thing
{
    private UUID id;
    private String idString;
    private String name;
    private String description;
    private String type;
    private boolean isConnected;
    private ThingConfiguration configuration;
    private CurrentThingOperatingConditions currentThingOperatingConditions;
    private EnumSet<CommunicationProtocol> supportedCommunicationProtocols;
    private Set<ThingResource> resources;
    private ThingPlatform platform;
    private ThingSDK sdk;
    private ThingCertificates certificates;


    public ThingDescriptor describe()
    {
        throw new NotImplementedException();
    }


    public void sendDataTo(DataPacket dataToSend, Thing recipientThing)
    {
        throw new NotImplementedException();
    }


    public void performAction(ThingContext context)
    {
        throw new NotImplementedException();
    }
}
