package com.orion.engineering.physics.atom;

public class AtomCharge
{
    public short getNetCharge(Atom atom)
    {
        return (short)(atom.nucleus.getNumberOfProtons() - atom.numberOfElectrons);
    }


    public boolean isPositivelyCharged(Atom atom)
    {
        return getNetCharge(atom) > 0;
    }


    public boolean isNegativelyCharged(Atom atom)
    {
        return !isPositivelyCharged(atom);
    }


    public boolean isNeutrallyCharged(Atom atom)
    {
        return getNetCharge(atom) == 0;
    }
}
