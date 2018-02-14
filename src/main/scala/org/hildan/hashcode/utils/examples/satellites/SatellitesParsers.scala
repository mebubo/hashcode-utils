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

  def location(collection: ImageCollection): Parser[Location] = for {
    lattitude <- integer
    longitude <- integer
  } yield new Location(collection, lattitude, longitude)

  def range: Parser[Array[Int]] = for {
    start <- integer
    stop <- integer
  } yield Array[Int](start, stop)

  def collection: Parser[ImageCollection] = for {
    value <- integer
    nLocations <- integer
    nRanges <- integer
    coll = new ImageCollection(value)
    locations <- location(coll).repeat(nLocations, Array[Location]())
    ranges <- range.repeat(nRanges, Array[Array[Int]]())
  } yield {
    coll.locations = locations
    coll.ranges = ranges
    coll
  }

  def simulation: Parser[Simulation] = for {
    nTurns <- integer
    nSatellites <- integer
    satellites <- satellite.repeat(nSatellites, Array[Satellite]())
    nCollections <- integer
    collections <- collection.repeat(nCollections, Array[ImageCollection]())
    sim = new Simulation(nTurns)
  } yield {
    sim.setCollections(collections)
    sim.setSatellites(satellites)
    sim
  }
}
