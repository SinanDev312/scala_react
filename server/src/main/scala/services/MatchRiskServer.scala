package services

import java.math.{RoundingMode, MathContext}

import akka.actor._
import common._

import scala.collection.immutable.TreeMap
import scala.util.Random

class MatchRiskServer extends Actor with ActorLogging {

  import MatchRiskServer._
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
    case MatchRiskInfoEvent(symbol) => {
      risks.get(symbol).map(fmatch => sender ! standardizeInfo(symbol, fmatch))
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

object MatchRiskServer {
  def props: Props = Props(classOf[MatchRiskServer])

  case class MatchRiskInfoEvent(symbol: Int)

  private[this] case class Info(kickoff: String, league: String, match_name: String,
                                val_1: Int, val_x: Int, val_2: Int)

  private var risks = TreeMap[Int, Info](
    0   -> Info("2015-05-10", "SPA.1", "Levante vs Valencia", -2462, -126, 3720),
    1   -> Info("2015-05-10", "SPA.1", "Villarreal vs Rayo Vallecano", 3767, -1806, -4303),
    2   -> Info("2015-05-11", "ENG.1", "Southampton vs Man Utd", -3888, -271, 332),
    3   -> Info("2015-05-11", "ENG.1", "Fulham vs Crystal Palace", -2249, -718, 3836),
    4   -> Info("2015-05-11", "ENG.1", "Norwich vs Arsenal", 2910, 1874, -6),
    5   -> Info("2015-05-11", "ENG.1", "West Brom vs Stoke", -939, -1748, -4798),
    6   -> Info("2015-05-11", "ENG.1", "Hull vs Everton", -4444, -4102, -2353)
  )

  def lInfoArray = for (e <- risks) yield standardizeInfo(e._1, e._2)

  def standardizeInfo(ID: Int, info: Info): MatchRisk = {
    MatchRisk(ID, info.kickoff, info.league, info.match_name,
                info.val_1, info.val_x, info.val_2)
  }
}
