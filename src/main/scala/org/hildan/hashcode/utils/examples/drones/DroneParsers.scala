package org.hildan.hashcode.utils.examples.drones

import org.hildan.hashcode.utils.examples.drones.model.{Order, Simulation, Warehouse}
import org.hildan.hashcode.utils.parser.Parser
import org.hildan.hashcode.utils.parser.Parser.integer

object DroneParsers {
  def order(n: Int): Parser[Order] = for {
    x <- integer
    y <- integer
    nItems <- integer
    items <- integer.repeat(nItems)
    o = new Order(x, y, n)
    _ = o.setItems(items)
  } yield o

  def warehouse(n: Int): Parser[Warehouse] = for {
    x <- integer
    y <- integer
    stocks <- integer.repeat(n)
    wh = new Warehouse(x, y)
    _ = wh.setStocks(stocks)
  } yield wh

  def simulation: Parser[Simulation] = for {
    nRows <- integer
    nCols <- integer
    d <- integer
    nTurns <- integer
    maxLoad <- integer
    nProductTypes <- integer
    productTypeWeights <- integer.repeat(nProductTypes, Array[Int]())
    nWarehouses <- integer
    warehouses <- warehouse(nProductTypes).repeat(nWarehouses, Array[Warehouse]())
    nOrders <- integer
    orders <- order(nProductTypes).repeat(nOrders)
    sim = new Simulation(nRows, nCols, d, nTurns, maxLoad, nProductTypes)
    _ = sim.warehouses = warehouses
    _ = sim.setOrders(orders)
    _ = sim.productTypeWeights = productTypeWeights
  } yield sim
}
