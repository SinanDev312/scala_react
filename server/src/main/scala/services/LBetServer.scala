package services

import java.math.{RoundingMode, MathContext}

import akka.actor._
import common._

import scala.collection.immutable.TreeMap
import scala.util.Random

class LBetServer extends Actor with ActorLogging {

  import LBetServer._
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
    case LBetInfoEvent(symbol) => {
      lbets.get(symbol).map(fmatch => sender ! standardizeInfo(symbol, fmatch))
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

object LBetServer {
  def props: Props = Props(classOf[LBetServer])

  case class LBetInfoEvent(symbol: Int)

  private[this] case class Info(date: String, league: String, market: String,
                                team: String, CP: String, size: Int,
                                price: Double, mark: Double, value: Int)

  private var lbets = TreeMap[Int, Info](
    0   -> Info("2015-5-13", "SPA.1", "Winner", "Barcelona", "Sportsrisq", 3517, 1.830, 0.00, -3517),
    1   -> Info("2015-5-13", "SPA.1", "Top Six", "Real Sociedad", "Sportsrisq", 2717, 1.23, 0.00, -2717),
    2   -> Info("2015-5-11", "SPA.1", "Winner", "Atletico Madrid", "Sportsrisq", -29100, 1.881, 100.00, -25637),
    3   -> Info("2015-5-07", "SPA.1", "Winner", "Real Madrid", "Sportsrisq", 16000, 3.250, 0.00, -16000),
    4   -> Info("2015-2-26", "SPA.1", "Top Four", "Athletic Bilbao", "Sportsrisq", 56100, 1.784, 100.00, 43982),
    5   -> Info("2015-2-24", "ENG.1", "Winner", "Man City", "Sportsrisq", -5657, 2.349, 100.00, -7631),
    6   -> Info("2015-2-22", "ENG.1", "Relegation", "Fulham", "Sportsrisq BF", 2495, 1.44655, 100.00, 1114),
    7   -> Info("2015-2-22", "ENG.1", "Top Half", "Hull", "Sportsrisq BF", 2, 9.4, 0.00, -2),
    8   -> Info("2015-2-22", "ENG.1", "Top Half", "West Ham", "Sportsrisq BF", -19, 6.8, 0.00, 19),
    9   -> Info("2015-2-22", "ENG.1", "Top Six", "Man Utd", "Sportsrisq BF", -1427, 1.54403, 0.00, 1427),
    10  -> Info("2015-2-22", "ENG.1", "Top Two", "Chelsea", "Sportsrisq BF", 473, 1.4129, 0.00, -473),
    11  -> Info("2015-2-21", "ENG.1", "Top Five", "Man Utd", "Sportsrisq BF", -1243, 2.48977, 0.00, 1243)
  )

  def lInfoArray = for (e <- lbets) yield standardizeInfo(e._1, e._2)

  def standardizeInfo(ID: Int, info: Info): LongTermBet = {
    LongTermBet(ID, info.date, info.league, info.market,
                                info.team, info.CP, info.size,
                                info.price, info.mark, info.value)
  }
}
