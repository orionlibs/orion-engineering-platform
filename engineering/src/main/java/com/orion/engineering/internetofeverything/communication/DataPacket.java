package com.orion.engineering.internetofeverything.communication;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DataPacket
{
    private byte[] data;
}
