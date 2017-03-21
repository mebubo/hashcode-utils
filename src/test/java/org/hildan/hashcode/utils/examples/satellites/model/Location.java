package org.hildan.hashcode.utils.examples.satellites.model;

public class Location {

    public ImageCollection parentCollection;

    public int[] coords = new int[2];

    public boolean pictureTaken = false;

    public Location(ImageCollection parentCollection, int latitude, int longitude) {
        this.parentCollection = parentCollection;
        this.coords[Simulation.LATITUDE] = latitude;
        this.coords[Simulation.LONGITUDE] = longitude;
    }

}
