package jlss

import org.scalatest._

class PressureQueueTest extends FlatSpec with Matchers {

  "pressure queue" should "not block" in {
    val q = new JLSSPressureQueue(10,100)
    val t1 = new Thread(new Runnable {
      def run {
        for(i <- 1 to 100000) {
          q.add(new Integer(i))
        }
        q.close()
      }
    })
    val t2 = new Thread(new Runnable {
      def run {
        var last = 0
        while(last < 100000) {
          assert(q.poll().asInstanceOf[Integer].toInt == last + 1)
          last += 1
        }
        q.terminate()
      }
    })
    t1.start()
    t2.start()
    t1.join()
    t2.join()
  }
}
