package services

import java.math.{RoundingMode, MathContext}

import akka.actor._
import common._

import scala.collection.immutable.TreeMap
import scala.util.Random

class TeamServer extends Actor with ActorLogging {

  import TeamServer._
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
    case TeamInfoEvent(symbol) => {
      teams.get(symbol).map(team => sender ! team)
    }
    case Terminated(r) => unsubscribe(r)
    case UnsubscribeEvent => unsubscribe(sender)
    case x => log.error(s"Invalid event: $x")
  }

  def subscribe(r: ActorRef): Unit = {
    Users += r
    context.watch(r)
    log.debug(s"Actor subscribed: $r")
    teamInfoArray.foreach(team => r ! team)
  }

  def unsubscribe(r: ActorRef): Unit = {
    context.unwatch(r)
    Users -= r
    log.debug(s"Actor unsubscribed: $r")
  }

  // who will receive messages
  private var Users = Set[ActorRef]()
}

object TeamServer {
  def props: Props = Props(classOf[TeamServer])

  // Re-Ask for concrete team information
  case class TeamInfoEvent(symbol: Int)

  private[this] case class Info(league: String, name: String, played: Int, current_points: Double, 
                                current_global_difference: Double, expeceted_remaining_points: Double, total_expected_points: Double,
                                mean_expected_points: Double, training_set_size: Int, training_error: Double)

  // Initial teams values ("on-open")
  private var teams = TreeMap[Int, Info](
    0   -> Info("SPA.1", "Real Madrid", 37, 84, 64, 2.69, 86.69, 2.42, 6, 0.00),
    1   -> Info("SPA.1", "Barcelona", 37, 86, 67, 1.86, 87.86, 2.42, 7, 0.00),
    3   -> Info("SPA.1", "Atletico Madrid", 37, 89, 51, 1.26, 87.86, 0.42, 8, 0.00),
    4   -> Info("SPA.1", "Man City", 37, 83, 63, 1.06, 87.86, 2.44, 7, 1.00),
    5   -> Info("SPA.1", "Liverpool", 37, 81, 50, 2.86, 87.86, 2.12, 7, 0.00),
    6   -> Info("SPA.1", "Chelsea", 37, 79, 43, 3.86, 87.86, 4.42, 7, 1.00),
    7   -> Info("SPA.1", "Arsenal", 37, 76, 25, 1.32, 86.86, 3.42, 7, 0.00),
    8   -> Info("SPA.1", "Man Utd", 37, 63, 21, 1.84, 85.86, 2.42, 7, 2.00),
    9   -> Info("SPA.1", "Everton", 37, 69, 20, 1.8, 87.83, 2.42, 7, 0.00),
    10   -> Info("SPA.1", "Tottenham", 37, 66, 1, 1.00, 82.86, 2.42, 7, 0.00)
  )

  /** Randomize "on-open" stocks */
  def teamInfoArray = for (e <- teams) yield randomizeTeam(e._1, e._2)

  def randomizeTeam(ID: Int, info: Info): Team = {
    Team(ID, info.league, info.name, info.played, info.current_points, 
            info.current_global_difference, info.expeceted_remaining_points, info.total_expected_points,
            info.mean_expected_points, info.training_set_size, info.training_error)
  }
}
