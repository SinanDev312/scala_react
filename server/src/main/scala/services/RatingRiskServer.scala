package services

import java.math.{RoundingMode, MathContext}

import akka.actor._
import common._

import scala.collection.immutable.TreeMap
import scala.util.Random

class RatingRiskServer extends Actor with ActorLogging {

  import RatingRiskServer._
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
    case RatingRiskInfoEvent(symbol) => {
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

object RatingRiskServer {
  def props: Props = Props(classOf[RatingRiskServer])

  case class RatingRiskInfoEvent(symbol: Int)

  private[this] case class Info(league: String, market: String, team: String, coverage: Int,
                                delta: Double, LS: String, risk: Int)

  private var risks = TreeMap[Int, Info](
    0   -> Info("ENG.1", "Relegation", "Southampton", 48260, -9.010, "Short", 4348),
    1   -> Info("SPA.1", "Winner", "Eiche", -47991, 8.028, "Short", 3853),
    2   -> Info("SPA.1", "Relegation", "Osasuna", -42964, -8.889, "Long", 3819),
    3   -> Info("ENG.1", "Relegation", "Sunderland", -40093, -9.322, "Long", 3737),
    4   -> Info("SPA.1", "Winner", "Levante", -47646, 7.576, "Short", 3610),
    5   -> Info("SPA.1", "Winner", "Athletic Bilbao", 45020, 7.766, "Long", 3496)
  )

  def lInfoArray = for (e <- risks) yield standardizeInfo(e._1, e._2)

  def standardizeInfo(ID: Int, info: Info): RatingRisk = {
    RatingRisk(ID, info.league, info.market, info.team,
                  info.coverage, info.delta, info.LS, info.risk)
  }
}
