import scala.annotation.tailrec
import scala.util.Try

/*
--- Day 9: Smoke Basin ---
These caves seem to be lava tubes. Parts are even still volcanically active; small hydrothermal vents release smoke into the caves that slowly settles like rain.

If you can model how the smoke flows through the caves, you might be able to avoid it and be that much safer. The submarine generates a heightmap of the floor of the nearby caves for you (your puzzle input).

Smoke flows to the lowest point of the area it's in. For example, consider the following heightmap:

2199943210
3987894921
9856789892
8767896789
9899965678
Each number corresponds to the height of a particular location, where 9 is the highest and 0 is the lowest a location can be.

Your first goal is to find the low points - the locations that are lower than any of its adjacent locations. Most locations have four adjacent locations (up, down, left, and right); locations on the edge or corner of the map have three or two adjacent locations, respectively. (Diagonal locations do not count as adjacent.)

In the above example, there are four low points, all highlighted: two are in the first row (a 1 and a 0), one is in the third row (a 5), and one is in the bottom row (also a 5). All other locations on the heightmap have some lower adjacent location, and so are not low points.

The risk level of a low point is 1 plus its height. In the above example, the risk levels of the low points are 2, 1, 6, and 6. The sum of the risk levels of all low points in the heightmap is therefore 15.

Find all of the low points on your heightmap. What is the sum of the risk levels of all low points on your heightmap?

Your puzzle answer was 524.

--- Part Two ---
Next, you need to find the largest basins so you know what areas are most important to avoid.

A basin is all locations that eventually flow downward to a single low point. Therefore, every low point has a basin, although some basins are very small. Locations of height 9 do not count as being in any basin, and all other locations will always be part of exactly one basin.

The size of a basin is the number of locations within the basin, including the low point. The example above has four basins.

The top-left basin, size 3:

2199943210
3987894921
9856789892
8767896789
9899965678
The top-right basin, size 9:

2199943210
3987894921
9856789892
8767896789
9899965678
The middle basin, size 14:

2199943210
3987894921
9856789892
8767896789
9899965678
The bottom-right basin, size 9:

2199943210
3987894921
9856789892
8767896789
9899965678
Find the three largest basins and multiply their sizes together. In the above example, this is 9 * 14 * 9 = 1134.

What do you get if you multiply together the sizes of the three largest basins?

Your puzzle answer was 1235430.

Both parts of this puzzle are complete! They provide two gold stars: **
 */

object D09 extends App {

  val input = io.Source.fromResource("D09_input.txt").mkString.split("\n").collect {
    case s if s.nonEmpty => s.split("").map(_.toInt)
  }

  case class Point(row: Int, col: Int, value: Int)

  val getAdjacents: (Int, Int) => Seq[Point] = {
    (row:Int, col: Int) => {
      val adjacents: Seq[(Int, Int)] = Seq(
        (row -1, col), // top
        (row, col + 1), // right
        (row + 1, col), //bottom
        (row, col -1) //left
      )
      adjacents.flatMap{ case (rowIdx, colIdx) => Try(Point(rowIdx, colIdx, input(rowIdx)(colIdx))).toOption }
    }
  }

  def findLowPoints(heightmap: Array[Array[Int]]): Array[Point] = {
    def isLow(currentValue: Int, row:Int, col: Int): Boolean = getAdjacents(row, col).forall(_.value > currentValue)
    // would be better with a fold?
    for {
      (row, rowIdx) <- heightmap.zipWithIndex
      (col, colIdx) <- row.zipWithIndex if isLow(col, rowIdx, colIdx)
    } yield Point(rowIdx, colIdx, col)
  }

  def sumLowPointsRisk(heightmap: Array[Array[Int]]): Int = {
    findLowPoints(heightmap).map(_.value + 1).sum
  }

  def calculateBasins(heightmap: Array[Array[Int]]): Int = {
    @tailrec
    def getBasinPoints(start: Seq[Point], accumulated: Seq[Point]): Seq[Point] = {
      start match {
        case Nil => accumulated
        case head :: tail =>
          val nonNinesNotAccumulated = getAdjacents(head.row, head.col).filter(_.value < 9).diff(accumulated)
          val newStart = nonNinesNotAccumulated ++ tail
          getBasinPoints(newStart, accumulated ++ nonNinesNotAccumulated)
      }
    }

    findLowPoints(heightmap)
      .map{ p => getBasinPoints(Seq(p), Seq.empty).size }
      .sorted(Ordering[Int].reverse)
      .slice(0, 3)
      .foldLeft(1)(_ * _)
  }

  println(s"c1: ${sumLowPointsRisk(input)}")
  println(s"c2: ${calculateBasins(input)}")


}
