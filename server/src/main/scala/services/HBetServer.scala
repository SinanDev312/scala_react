package services

import java.math.{RoundingMode, MathContext}

import akka.actor._
import common._

import scala.collection.immutable.TreeMap
import scala.util.Random

class HBetServer extends Actor with ActorLogging {

  import HBetServer._
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
    case HBetInfoEvent(symbol) => {
      hbets.get(symbol).map(fmatch => sender ! standardizeInfo(symbol, fmatch))
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

object HBetServer {
  def props: Props = Props(classOf[HBetServer])

  case class HBetInfoEvent(symbol: Int)

  private[this] case class Info(date: String, league: String, fmatch: String,
                                selection: String, CP: String, size: Int,
                                price: Double, result: String, PL: Int)

  private var hbets = TreeMap[Int, Info](
    0   -> Info("2015-6-3", "SPA.1", "Eiche vs Barcelona", "Draw", "Sportsrisq", 193, 7.21001, "Draw", 1198),
    1   -> Info("2015-6-3", "SPA.1", "Eiche vs Barcelona", "Barcelona", "Sportsrisq", 25355, 1.24703, "Draw", -25355),
    2   -> Info("2015-6-3", "SPA.1", "Celta Vigo vs Real Madrid", "Real Madrid", "Sportsrisq", 1105, 1.50979, "Celta Vigo", -1105),
    3   -> Info("2015-6-3", "SPA.1", "Atletico Madrid vs Malaga", "Malaga", "Sportsrisq", 1184, 17.58656, "Atletico Madrid", -1184),
    4   -> Info("2015-6-3", "SPA.1", "Atletico Madrid vs Malaga", "Draw", "Sportsrisq", 2209, 7.03463, "Draw", 13329),
    5   -> Info("2015-6-3", "SPA.1", "Athletic Bilbao vs Real Sociedad", "Real Sociedad", "Sportsrisq", 221, 3.85828, "Draw", -221),
    6   -> Info("2015-6-3", "ENG.1", "Tottenham vs Aston Villa", "Aston Villa", "Sportsrisq", 107, 7.53753, "Tottenham", -107)
  )

  def lInfoArray = for (e <- hbets) yield standardizeInfo(e._1, e._2)

  def standardizeInfo(ID: Int, info: Info): HedgeBet = {
    HedgeBet(ID, info.date, info.league, info.fmatch, info.selection, info.CP, info.size,
                info.price, info.result, info.PL)
  }
}
