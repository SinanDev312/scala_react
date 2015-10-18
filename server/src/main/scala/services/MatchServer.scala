package services

import java.math.{RoundingMode, MathContext}

import akka.actor._
import common._

import scala.collection.immutable.TreeMap
import scala.util.Random

class MatchServer extends Actor with ActorLogging {

  import MatchServer._
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
    case MatchInfoEvent(symbol) => {
      matches.get(symbol).map(fmatch => sender ! fmatch)
    }
    case Terminated(r) => unsubscribe(r)
    case UnsubscribeEvent => unsubscribe(sender)
    case x => log.error(s"Invalid event: $x")
  }

  def subscribe(r: ActorRef): Unit = {
    Users += r
    context.watch(r)
    log.debug(s"Actor subscribed: $r")
    matchInfoArray.foreach(fmatch => r ! fmatch)
  }

  def unsubscribe(r: ActorRef): Unit = {
    context.unwatch(r)
    Users -= r
    log.debug(s"Actor unsubscribed: $r")
  }

  // who will receive messages
  private var Users = Set[ActorRef]()
}

object MatchServer {
  def props: Props = Props(classOf[MatchServer])

  case class MatchInfoEvent(symbol: Int)

  private[this] case class Info(kickoff: String, league: String, home_team: String,
                                model_home_expected_points: Double, market_home_expected_points: Double,
                                away_team: String,
                                model_away_expected_points: Double, market_away_expected_points: Double)

  private var matches = TreeMap[Int, Info](
    0   -> Info("2015-5-10", "SPA.1", "Levante", 1.115, 1.114, "Valencia", 1.604, 1.610),
    1   -> Info("2015-5-10", "SPA.1", "Villarreal", 2.102, 2.030, "Rayo Vallecano", 0.687, 0.748),
    3   -> Info("2015-5-11", "SPA.1", "Southampton", 1.324, 1.395, "Man Utd", 1.386, 1.328),
    4   -> Info("2015-5-11", "SPA.1", "Fulham", 1.565, 1.394, "Crystal Palace", 1.152, 1.327),
    5   -> Info("2015-5-11", "SPA.1", "Norwich", 0.842, 0.911, "Arsenal", 1.914, 1.833),
    6   -> Info("2015-5-11", "SPA.1", "West Brom", 1.700, 1.680, "Stoke", 1.029, 1.049),
    7   -> Info("2015-5-11", "SPA.1", "Hull", 1.020, 1.012, "Everton", 1.711, 1.714),
    8   -> Info("2015-5-11", "SPA.1", "Liverpool", 2.615, 2.527, "Newcastle", 0.260, 0.345),
    9   -> Info("2015-5-11", "SPA.1", "Sunderland", 1.405, 1.557, "Swansea", 1.306, 1.154),
    10   -> Info("2015-5-11", "SPA.1", "Cardiff", 0.579, 0.557, "Chelsea", 2.226, 2.247)
  )

  /** Randomize "on-open" stocks */
  def matchInfoArray = for (e <- matches) yield standardizeInfo(e._1, e._2)

  def standardizeInfo(ID: Int, info: Info): Match = {
    Match(ID, info.kickoff, info.league, info.home_team,
        info.model_home_expected_points, info.market_home_expected_points, info.model_home_expected_points - info.market_home_expected_points, 
        info.away_team, info.model_away_expected_points, info.market_away_expected_points,
        info.model_away_expected_points - info.market_away_expected_points)
  }
}
