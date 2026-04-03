package com.orion.engineering.physics.atom;

import com.orion.engineering.physics.atom.particle.Neutron;
import com.orion.engineering.physics.atom.particle.Proton;

public class Atom
{
    protected Nucleus nucleus;
    protected short numberOfElectrons;


    public short getAtomicNumber()
    {
        return nucleus.getNumberOfProtons();
    }


    public double getAtomicMass()
    {
        return Proton.mass * nucleus.getNumberOfProtons() + Neutron.mass * nucleus.getNumberOfNeutrons();
    }
}
