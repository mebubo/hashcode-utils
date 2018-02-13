package org.hildan.hashcode.utils.examples.satellites

import org.hildan.hashcode.utils.examples.satellites.model.{ImageCollection, Location, Satellite, Simulation}
import org.hildan.hashcode.utils.parser.Parser
import org.hildan.hashcode.utils.parser.Parser.integer

object SatellitesParsers {
  def satellite: Parser[Satellite] = for {
    lattitude <- integer
    longitude <- integer
    v0 <- integer
    maxOrientationChangePerTurn <- integer
    maxOrientationValue <- integer
  } yield new Satellite(lattitude, longitude, v0, maxOrientationChangePerTurn, maxOrientationValue)

  def location: Parser[Location] = for {
    lattitude <- integer
    longitude <- integer
  } yield new Location(null, lattitude, longitude)

  def range: Parser[Array[Int]] = for {
    start <- integer
    stop <- integer
  } yield Array[Int](start, stop)

  def collection: Parser[ImageCollection] = for {
    value <- integer
    nLocations <- integer
    nRanges <- integer
    ls <- location.repeat(nLocations)
    locations: java.util.List[Location] = ls
    rs <- range.repeat(nRanges)
    ranges: java.util.List[Array[Int]] = rs
    coll = new ImageCollection(value)
    _ = locations.forEach(l => l.parentCollection = coll)
    _ = coll.locations = locations.toArray(Array[Location]())
    _ = coll.ranges = ranges.toArray(Array[Array[Int]]())
  } yield coll

  def simulation: Parser[Simulation] = for {
    nTurns <- integer
    nSatellites <- integer
    ss <- satellite.repeat(nSatellites)
    satellites: java.util.List[Satellite] = ss
    nCollections <- integer
    cs <- collection.repeat(nCollections)
    collections: java.util.List[ImageCollection] = cs
    sim = new Simulation(nTurns)
    _ = sim.setCollections(collections.toArray(Array[ImageCollection]()))
    _ = sim.setSatellites(satellites.toArray(Array[Satellite]()))
  } yield sim
}
