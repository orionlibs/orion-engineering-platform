package com.orion.engineering.internetofeverything.thing.security;

import com.orion.engineering.core.exception.CheckedException;

public class ThingPolicyNotExistException extends CheckedException
{
    public ThingPolicyNotExistException(String errorMessage)
    {
        super(errorMessage);
    }


    public ThingPolicyNotExistException(String errorMessage, Object... arguments)
    {
        super(errorMessage, arguments);
    }


    public ThingPolicyNotExistException(Throwable cause, String errorMessage, Object... arguments)
    {
        super(cause, errorMessage, arguments);
    }


    public ThingPolicyNotExistException(Throwable cause)
    {
        super(cause);
    }
}
