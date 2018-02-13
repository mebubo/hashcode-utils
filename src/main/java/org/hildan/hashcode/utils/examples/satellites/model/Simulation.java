package org.hildan.hashcode.utils.examples.satellites.model;

public class Simulation {

    /**
     * Index of the latitude in an array of coordinates.
     */
    public static final int LATITUDE = 0;

    /**
     * Index of the longitude in an array of coordinates.
     */
    public static final int LONGITUDE = 1;

    /**
     * Total number of turns (seconds) in the simulation.
     */
    public final int nTurns;

    /**
     * Collections of images to shoot.
     */
    public ImageCollection[] collections;

    /**
     * The satellites to use to shoot the pictures.
     */
    public Satellite[] satellites;

    public Simulation(int nTurns) {
        this.nTurns = nTurns;
    }

    public void setCollections(ImageCollection[] collections) {
        this.collections = collections;
    }

    public void setSatellites(Satellite[] satellites) {
        this.satellites = satellites;
    }
}
