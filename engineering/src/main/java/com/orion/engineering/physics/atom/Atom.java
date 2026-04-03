package com.orion.engineering.physics.atom;

import com.orion.engineering.physics.atom.particle.Neutron;
import com.orion.engineering.physics.atom.particle.Proton;
import lombok.Getter;

@Getter
public class Atom
{
    protected Nucleus nucleus;
    protected short numberOfElectrons;
    protected AtomCharge charge;


    public Atom(Nucleus nucleus, short numberOfElectrons, AtomCharge charge)
    {
        this.nucleus = nucleus;
        this.numberOfElectrons = numberOfElectrons;
        this.charge = charge;
    }


    public short getAtomicNumber()
    {
        return nucleus.getNumberOfProtons();
    }


    public double getAtomicMass()
    {
        return Proton.MASS * nucleus.getNumberOfProtons() + Neutron.MASS * nucleus.getNumberOfNeutrons();
    }
}
