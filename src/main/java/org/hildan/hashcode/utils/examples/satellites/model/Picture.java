package org.hildan.hashcode.utils.examples.satellites.model;

public class Picture {

    public final int[] position;

    public final int turnTakenAt;

    public final int satellite;

    public Picture(int[] position, int turnTakenAt, int satellite) {
        this.position = position;
        this.turnTakenAt = turnTakenAt;
        this.satellite = satellite;
    }
}
