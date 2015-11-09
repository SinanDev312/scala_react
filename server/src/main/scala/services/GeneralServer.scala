package services

import java.math.{RoundingMode, MathContext}

import akka.actor._
import common._

import scala.collection.immutable.TreeMap
import scala.util.Random

class GeneralServer extends Actor with ActorLogging {

  import GeneralServer._
  import context.dispatcher

  import scala.concurrent.duration._
  import scala.language.postfixOps

  val InitialDelay = 100.milliseconds
  val VerySlow = 5000.milliseconds
  val Slow = 2000.milliseconds
  val Fast = 100.milliseconds

  override def receive: Receive = {
    case SubscribeEvent => {
      subscribe(sender)
    }
    case ElemInfoEvent(symbol) => {
      elems.get(symbol).map(fmatch => sender ! standardizeInfo(symbol, fmatch))
    }
    case Terminated(r) => unsubscribe(r)
    case UnsubscribeEvent => unsubscribe(sender)
    case x => log.error(s"Invalid event: $x")
  }

  def subscribe(r: ActorRef): Unit = {
    Users += r
    context.watch(r)
    log.debug(s"Actor subscribed: $r")
    lInfoArray.foreach(fmatch => r ! fmatch)
  }

  def unsubscribe(r: ActorRef): Unit = {
    context.unwatch(r)
    Users -= r
    log.debug(s"Actor unsubscribed: $r")
  }

  // who will receive messages
  private var Users = Set[ActorRef]()
}

object GeneralServer {
  def props: Props = Props(classOf[GeneralServer])

  case class ElemInfoEvent(symbol: Int)

  private[this] case class Info(field1: String, field2: Int, field3: Double)

  private var elems = TreeMap[Int, Info](
    0   -> Info("2015-6-3", 10, 20.345),
    1   -> Info("2015-6-3", 20, 21.310),
    2   -> Info("2015-6-4", 13, 22.03),
    3   -> Info("2015-6-4", 20, 30.902),
    4   -> Info("2015-6-5", 24, 30.04),
    5   -> Info("2015-6-6", 101,30.4),
    6   -> Info("2015-6-7", 20, 30.5),
    7   -> Info("2015-6-7", 19, 30.5),
    8   -> Info("2015-6-9", 16, 30.145)
  )

  def lInfoArray = for (e <- elems) yield standardizeInfo(e._1, e._2)

  def standardizeInfo(ID: Int, info: Info): GeneralElement = {
    GeneralElement(ID, info.field1, info.field2, info.field3)
  }
}
