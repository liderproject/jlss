package jlss.benchmarks

import jlss._
import jlss.services._

object Benchmarks {

  def asyncPerformance {
    val source = new RandomGeneratorService(10000)

    val sw = new java.io.StringWriter()
    val sink = new Outputter(sw)
    implicit val context = AsynchronousServiceContext.blocking

    val tStart = System.currentTimeMillis()
    source | FairlyGoodTokenizer | sink
    val tEnd = System.currentTimeMillis() - tStart

    System.err.println("Async took %d millis" format tEnd)
  }

  def pressurePerfomance {
    val source = new RandomGeneratorService(10000)

    val sw = new java.io.StringWriter()
    val sink = new Outputter(sw)
    implicit val context = AsynchronousServiceContext.pressure()

    val tStart = System.currentTimeMillis()
    source | FairlyGoodTokenizer | sink
    val tEnd = System.currentTimeMillis() - tStart

    System.err.println("Pressure took %d millis" format tEnd)
  }



  def defaultPerformance {
    val source = new RandomGeneratorService(10000)

    val sw = new java.io.StringWriter()
    val sink = new Outputter(sw)
    implicit val context = DefaultServiceContext

    val tStart = System.currentTimeMillis()
    source | FairlyGoodTokenizer | sink
    val tEnd = System.currentTimeMillis() - tStart

    System.err.println("Default took %d millis" format tEnd)
  }


  class RandomGeneratorService(lines : Int) extends StreamSource {
    val freqs = Map('E' -> 8.4509654E-4, 'e' -> 0.091242135, 'X' -> 1.8808807E-5, 's' -> 0.043105274, 'x' -> 9.828749E-4, 'n' -> 0.049687516,
      'N' -> 0.0018974723, 'j' -> 0.0012707413, 'y' -> 0.023287293, 'T' -> 0.0041916114, 'Y' -> 0.0034265672, 't' -> 0.06884612, 'J' ->
      8.0487935E-4, 'u' -> 0.029348163, 'U' -> 3.3297707E-4, 'f' -> 0.012446843, 'F' -> 8.580945E-4, 'A' -> 0.002879965, 'a' -> 0.057379097,
      'm' -> 0.020080773, 'M' -> 0.001936772, 'I' -> 0.010630647, 'i' -> 0.045705784, ' ' -> 0.2041998, 'v' -> 0.0074670967, 'G' ->
      0.0014527128, 'V' -> 2.2822882E-4, 'q' -> 3.8183408E-4, 'Q' -> 6.093748E-5, 'L' -> 0.0014348979, 'b' -> 0.010441259, 'g' -> 0.017691901,
      'B' -> 0.0016639679, 'l' -> 0.03169995, 'P' -> 0.0010166696, 'p' -> 0.010702518, 'C' -> 0.0018128327, 'H' -> 0.002640879, 'c' ->
      0.01576094, 'W' -> 0.004267688, 'h' -> 0.043002974, 'r' -> 0.041268818, 'K' -> 5.165541E-4, 'w' -> 0.016579123, 'R' -> 8.77515E-4, 'k'
      -> 0.010420385, 'O' -> 0.0016261972, 'D' -> 0.0017077021, 'Z' -> 5.497371E-5, 'o' -> 0.06996219, 'z' -> 5.5409526E-4, 'S' ->
      0.0028927336, 'd' -> 0.026407108)

    private def randChar : Char = {
      var p = util.Random.nextDouble()
      for((k,v) <- freqs) {
        if(v > p) {
          return k
        } else {
          p -= v
        }
      }
      return '@'
    }

    def nextLine = (0 to 80).map({ i => randChar }).mkString("")

    import JsonGen._

    def apply() : JsonObject = obj (
      "lines" -> (0 to lines).map({ i => nextLine })
    )
  }


  def main(args : Array[String]) {
    System.err.println("Press enter to start")
    System.in.read()
    defaultPerformance
    asyncPerformance
    pressurePerfomance
  }
}
