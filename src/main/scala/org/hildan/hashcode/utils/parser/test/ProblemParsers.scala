package org.hildan.hashcode.utils.parser.test

import org.hildan.hashcode.utils.parser.Parser
import org.hildan.hashcode.utils.parser.Parser.{doubl, integer, string}

object ProblemParsers {
  def point: Parser[Point] = for {
    x <- doubl
    y <- doubl
  } yield new Point(x, y)

  def shape: Parser[Shape] = for {
    name <- string
    nPoints <- integer
    points <- point.repeat(nPoints)
  } yield new Shape(name, nPoints, points)

  def problem: Parser[Problem] = for {
    param1 <- integer
    param2 <- integer
    nShapes <- integer
    shapes <- shape.repeat(nShapes)
  } yield new Problem(param1, param2, nShapes, shapes)
}
