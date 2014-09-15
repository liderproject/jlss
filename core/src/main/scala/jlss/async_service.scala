package jlss

trait JLSSAsyncQueue {
  def add(o : Object) : Boolean
  def peek() : Object
  def poll() : Object
  def close() : Unit
  def terminate() : Unit
}

class JLSSBlockingQueue extends JLSSAsyncQueue {
  private val queue = new java.util.concurrent.ConcurrentLinkedQueue[Object]()
  private val lock = new Object()
  private var closed = false

  def add(o : Object) : Boolean = {
    queue.add(o)
    lock.synchronized {
      lock.notifyAll()
    }
    return !closed
  }
  def peek() : Object = {
    queue.peek() match {
      case null => {
        lock.synchronized {
          if(closed) {
            return null
          }
          lock.wait()
        }          
        peek()
      }
      case o => o
    }
  }

  def poll() : Object = {
    queue.poll() match {
      case null => {
        lock.synchronized {
          if(closed) {
            return null
          }
          lock.wait()
        }          
        poll()
      }
      case o => o
    }
  }

  def close() {
    closed = true
    lock.synchronized {
      lock.notifyAll()
    }
  }

  def terminate() { }
}
  
/*******************************************************************************
 * A pressure queue communicates between a producer and a consumer, while 
 * maintaining a buffer. The size of the buffer ("pressure") should be kept in
 * the range <i>margin &lt; pressure &lt (max - margin)</i>. This is achieved by
 * temporarily blocking on either the add or the peek/poll function
 */
class JLSSPressureQueue(margin : Int, max: Int) extends JLSSAsyncQueue {
  require(max > 2 * margin)
  private val queue = new java.util.concurrent.ConcurrentLinkedQueue[Object]()
  private val highPressure = new Object()
  private val lowPressure = new Object()
  private var closed = false
  private var terminated = false
  private val pressure = new java.util.concurrent.atomic.AtomicInteger

  /**
   * Add an object to the queue. May block if the pressure is too high
   * @return <code>false</code> if the consumer has disconnected and future
   * messages should not be generated
   * @throws IllegalStateException If this queue is already closed
   */
  def add(o : Object) : Boolean = {
    if(closed) throw new IllegalStateException("Already closed")
    if(!terminated) {
      if(pressure.get() > max) {
        highPressure.synchronized {
          //System.err.println("HI +" + pressure.get())
          highPressure.wait(100)
          //System.err.println("HI -" + pressure.get())
        }
        lowPressure.synchronized {
          lowPressure.notifyAll()
        }
      }
      queue.add(o)
      if(pressure.incrementAndGet() == margin) {
        lowPressure.synchronized {
          lowPressure.notifyAll()
        }
      }
      return true
    } else {
      return false
    }
  }

  /**
   * Return the next object in the queue but do not remove it. May block if the pressure is too low
   * @return The next object in the queue or <code>null</code> if {@link close()} has been called
   */
  def peek() : Object = {
    if(terminated) throw new IllegalStateException("Already terminated")
    if(!closed && pressure.get() < margin) {
      lowPressure.synchronized {
        //System.err.println("LO +" + pressure.get())
        lowPressure.wait(100)
        //System.err.println("LO -" + pressure.get())
      }
      highPressure.synchronized {
        highPressure.notifyAll()
      }
    }
    queue.peek() match {
      case null => {
        if(closed) {
          null
        } else {
          peek()
        }
      }
      case o => {
        if(pressure.decrementAndGet() == max - margin) {
          highPressure.synchronized {
            highPressure.notifyAll()
          }
        }
        o
      }
    }
  }

  /**
   * Return the next object in the queue and remove it. May block if the pressure is too low
   * @return The next object in the queue or <code>null</code> if {@link close()} has been called
   */
  def poll() : Object = {
    if(terminated) throw new IllegalStateException("Already terminated")
    if(!closed && pressure.get() < margin) {
      lowPressure.synchronized {
        //System.err.println("LO +" + pressure.get())
        lowPressure.wait(100)
        //System.err.println("LO -" + pressure.get())
      }
      highPressure.synchronized {
        highPressure.notifyAll()
      }
    }
    queue.poll() match {
      case null => {
        if(closed) {
          null
        } else {
          poll()
        }
      }
      case o => {
        if(pressure.decrementAndGet() == max - margin) {
          highPressure.synchronized {
            highPressure.notifyAll()
          }
        } 
        o
      }
    }
  }

  /**
   * Called by the producer to indicate that no more messages will be added to the queue
   */
  def close() {
    closed = true
    lowPressure.synchronized {
      lowPressure.notifyAll()
    }
  }

  /**
   * Called by the consumer to indiciate that no more messages will be removed from the queue
   */
  def terminate() {
    terminated = true
    highPressure.synchronized {
      highPressure.notifyAll()
    }
  }
} 

object AsynchronousServiceContext {
  def apply() : ServiceContext = new AsynchronousServiceContext(new JLSSBlockingQueue())
  def blocking : ServiceContext = new AsynchronousServiceContext(new JLSSBlockingQueue())
  def pressure(margin : Int = 1000, max : Int = 10000) : ServiceContext = new AsynchronousServiceContext(new JLSSPressureQueue(margin,max))
}

class AsynchronousServiceContext(mkQueue : => JLSSAsyncQueue) extends ServiceContext {
  private object ObjectBegin
  private object ObjectEnd
  private object ArrayBegin
  private object ArrayEnd
  private case class Field(val key : String)

  trait JsonQueue[A] extends Iterator[A] {
    var child : Option[JsonQueue[_]] = None
    var complete = false
    def seekEnd {
      while(!complete) {
        next
        hasNext
      }
    }
  }

  class JsonQueueProcesser(queue : JLSSAsyncQueue) {
    def apply(json : Json) {
      json match {
        case JsonArray(vs) => {
          queue.add(ArrayBegin)
          for(v <- vs) {
            apply(v)
          }
          queue.add(ArrayEnd)
        }
        case JsonObject(vs) => {
          queue.add(ObjectBegin)
          for(JsonField(k,vs2) <- vs) {
            queue.add(Field(k))
            apply(vs2)
          }
          queue.add(ObjectEnd)
        }
        case otherJson : Json => 
          queue.add(otherJson) 
      }
    }
  }

  private class JsonQueueArray(q : JLSSAsyncQueue) extends JsonQueue[Json] {
    var finished = false

    def hasNext = if(finished) {
      false
    } else {
      child match {
        case Some(c) => 
          if(!c.complete) {
            c.seekEnd
          }
          child = None
        case None =>
      }

      val p = q.peek()
      if(p == null) {
        false
      } else if(p == ArrayEnd) {
        q.poll() 
        complete = true
        false
      } else {
        true
      }
    }

    def next = if(finished) {
      throw new NoSuchElementException()
    } else {
      child match {
        case Some(c) => 
          if(!c.complete) {
            c.seekEnd
          }
          child = None
        case None =>
      }

      q.poll() match {
        case ArrayEnd => throw new NoSuchElementException()
        case ObjectBegin => {
          val c = new JsonQueueObject(q)
          child = Some(c)
          JsonObject(c)
        }
        case ArrayBegin => {
          val c = new JsonQueueArray(q)
          child = Some(c)
          JsonArray(c)
        }
        case o : Json => o
        case null => throw new JsonStreamException("Unexpected end of stream")
        case _ => throw new JsonStreamException()
      }
    }
  }

  private class JsonQueueObject(q : JLSSAsyncQueue) extends JsonQueue[JsonField] {
    var finished = false
    def hasNext = if(finished) {
      false
    } else {
      child match {
        case Some(c) => 
          if(!c.complete) {
            c.seekEnd
          }
          child = None
        case None =>
      }

      val p = q.peek()
      if(p == null) {
        false
      } else if(p == ObjectEnd) {
        q.poll()
        complete = true
        false
      } else {
        true
      }
    }

    def next = if(finished) {
      throw new NoSuchElementException()
    } else {
      child match {
        case Some(c) => 
          if(!c.complete) {
            c.seekEnd
          }
          child = None
        case None =>
      }

      val key = q.poll() match {
        case Field(k) => k
        case x => throw new JsonStreamException("Expected field")
      }
      q.poll() match {
        case ObjectEnd => throw new NoSuchElementException()
        case ObjectBegin => {
          val c = new JsonQueueObject(q)
          child = Some(c)
          JsonField(key, JsonObject(c))
        }
        case ArrayBegin => {
          val c = new JsonQueueArray(q)
          child = Some(c)
          JsonField(key, JsonArray(c))
        }
        case o : Json => JsonField(key, o)
        case null => throw new JsonStreamException("Unexpected end of stream")
        case _ => throw new JsonStreamException()
      }
    }
  }
  
  def connect(src : StreamSource, svc : StreamSink) {
    val queue = mkQueue
    val t1 = new Thread(new Runnable {
      def run {
        try {
          new JsonQueueProcesser(queue)(src())
        } finally {
          queue.close()
        }
      }
    })
    t1.start()
    queue.poll() match {
      case ObjectBegin => try {
        svc(JsonObject(new JsonQueueObject(queue)))
      } finally {
        queue.terminate()
      }
      case ArrayBegin => try {
        svc(JsonArray(new JsonQueueArray(queue)))
      } finally {
        queue.terminate()
      }
      case null => throw new JsonStreamException("source failure")
      case _ => throw new JsonStreamException()
    }
  }

  def connect(src : StreamSource, svc : StreamService) = new StreamSource {
    def apply() = {
      val queue = mkQueue
      val t1 = new Thread(new Runnable {
        def run {
          try {
            new JsonQueueProcesser(queue)(src())
          } finally {
            queue.close()
          }
        }
      })
      t1.start()
      queue.poll() match {
        case ObjectBegin => try {
          svc(JsonObject(new JsonQueueObject(queue)))
        } finally {
          //queue.terminate()
        }
        case ArrayBegin => try {
          svc(JsonArray(new JsonQueueArray(queue)))
        } finally {
          //queue.terminate()
        }
        case null => throw new JsonStreamException("source failure")
        case _ => throw new JsonStreamException()
      }
    }
  }
}
