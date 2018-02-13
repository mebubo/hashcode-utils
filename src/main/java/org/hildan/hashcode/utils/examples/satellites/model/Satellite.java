package org.hildan.hashcode.utils.examples.satellites.model;

import static org.hildan.hashcode.utils.examples.satellites.model.Simulation.LATITUDE;
import static org.hildan.hashcode.utils.examples.satellites.model.Simulation.LONGITUDE;

public class Satellite {

    public final int maxOrientationChangePerTurn;

    public final int maxOrientationValue;

    public int latitudeVelocity;

    public int[] position;

    public Satellite(int latitude, int longitude, int v0, int maxOrientationChangePerTurn, int maxOrientationValue) {
        this.position = new int[2];
        this.position[LATITUDE] = latitude;
        this.position[LONGITUDE] = longitude;
        this.latitudeVelocity = v0;
        this.maxOrientationChangePerTurn = maxOrientationChangePerTurn;
        this.maxOrientationValue = maxOrientationValue;
    }
}
