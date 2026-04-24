package com.orion.engineering.physics.atom;

import lombok.Getter;

@Getter
public class Nucleus
{
    private short numberOfProtons;
    private short numberOfNeutrons;


    public void removeProton()
    {
        if(numberOfProtons - 1 < 0)
        {
            numberOfProtons = 0;
        }
        else
        {
            numberOfProtons--;
        }
    }


    public void removeNeutron()
    {
        if(numberOfNeutrons - 1 < 0)
        {
            numberOfNeutrons = 0;
        }
        else
        {
            numberOfNeutrons--;
        }
    }
}
