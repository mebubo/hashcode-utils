package org.hildan.hashcode.utils.examples.basic

import org.hildan.hashcode.utils.parser.Parser

object BasicParsers {

  def point: Parser[Point] = for {
    x <- Parser.doubl
    y <- Parser.doubl
  } yield new Point(x, y)

  def problem: Parser[Problem] = for {
    p <- Parser.integer
    c <- Parser.integer
    points <- point.repeat(p)
  } yield new Problem(c, points)

}
