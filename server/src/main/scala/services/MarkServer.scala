package services

import java.math.{RoundingMode, MathContext}

import akka.actor._
import common._

import scala.collection.immutable.TreeMap
import scala.util.Random

class MarkServer extends Actor with ActorLogging {

  import MarkServer._
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
    case MarkInfoEvent(symbol) => {
      marks.get(symbol).map(fmatch => sender ! fmatch)
    }
    case Terminated(r) => unsubscribe(r)
    case UnsubscribeEvent => unsubscribe(sender)
    case x => log.error(s"Invalid event: $x")
  }

  def subscribe(r: ActorRef): Unit = {
    Users += r
    context.watch(r)
    log.debug(s"Actor subscribed: $r")
    markInfoArray.foreach(fmatch => r ! fmatch)
  }

  def unsubscribe(r: ActorRef): Unit = {
    context.unwatch(r)
    Users -= r
    log.debug(s"Actor unsubscribed: $r")
  }

  // who will receive messages
  private var Users = Set[ActorRef]()
}

object MarkServer {
  def props: Props = Props(classOf[MarkServer])

  case class MarkInfoEvent(symbol: Int)

  private[this] case class Info(league: String, market: String, team: String,
                              bidder: String, bid: Double, offer: Double,
                              seller: String, mark: Double, risk: Double,
                              value_type: String, value: Double, risk_adjusted_value: Double,
                              roe: Double)

  private var marks = TreeMap[Int, Info](
    0   -> Info("SPA.1", "Relegation", "Valladolid", "", 0, 36.36, "B3", 100.00, 0.10, "Offer", 63.64, 636.36, 1.75),
    1   -> Info("SPA.1", "Relegation", "Almeria", "BF", 54.64, 63.64, "VC", 0.00, 0.10, "Bid", 54.64, 546.45, 1.2),
    2   -> Info("SPA.1", "Winner", "Atletico Madrid", "BF", 51.55, 52.08, "BF", 100.00, 0.10, "Offer", 47.92, 479.17, 0.92),
    3   -> Info("SPA.1", "Winner", "Barcelona", "BF", 46.73, 47.17, "BF", 0.00, 0.10, "Bid", 46.73, 467.29, 0.88),
    4   -> Info("SPA.1", "Relegation", "Osasuna", "", 0, 61.90, "B3", 100.00, 0.10, "Offer", 38.10, 380.95, 0.62),
    5   -> Info("SPA.1", "Winner", "Liverpool", "BF", 4.55, 4.76, "BF", 0.00, 0.10, "Bid", 4.55, 45.45, 0.05),
    6   -> Info("SPA.1", "Winner", "Man City", "BF", 95.24, 96.15, "BF", 100.00, 0.10, "Offer", 3.85, 38.46, 0.04),
    7   -> Info("SPA.1", "Winner", "Real Madrid", "BF", 1.07, 1.43, "BF", 0.00, 0.10, "Bid", 1.07, 10.71, 0.01)
  )

  def markInfoArray = for (e <- marks) yield standardizeInfo(e._1, e._2)

  def standardizeInfo(ID: Int, info: Info): Mark = {
    Mark(ID, info.league, info.market, info.team, info.bidder, info.bid, info.offer,
            info.seller, info.mark, info.risk,
            info.value_type, info.value, info.risk_adjusted_value, info.roe)
  }
}
