package services

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import common._
import play.api.libs.json.Json
import services.StockServer._
import services.TeamServer._
import services.MatchServer._
import services.MarkServer._
import services.LBetServer._
import services.HBetServer._
import services.MatchRiskServer._
import services.RatingRiskServer._
import services.GeneralServer._

object UserConnection {
  def props(infoServer: ActorRef, out: ActorRef): Props = Props(classOf[UserConnection], infoServer, out)
}

class UserConnection(infoServer: ActorRef, out: ActorRef) extends Actor with ActorLogging {
  override def preStart() {
    log.debug(s"Sending Subscribe event")
    self ! SubscribeEvent
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    infoServer ! UnsubscribeEvent
  }

  import models.Formats._

  // RandomStockEvent
  override def receive: Receive = {
    // Subscribe on Stock Server events
    case SubscribeEvent => {
      infoServer ! SubscribeEvent
      log.debug(s"Got Subscribe event")
    }
    // send StockInfo(...) - extended version
    case info: StockInfo => {
      log.debug(s"GOT $info")
      out ! Json.toJson(info)
    }
    // ask for stock
    case StockInfoEvent(symbol) => {
      log.debug(s"GOT ${StockInfoEvent(symbol)}")
      infoServer ! StockInfoEvent(symbol)
    }
    // Unsubscribe from Stock Server
    case UnsubscribeEvent => {
      log.debug(s"GOT UnsubscribeEvent")
      infoServer ! UnsubscribeEvent
    }

    // team information
    case teaminfo: Team => {
      log.debug(s"GOT $teaminfo")
      out ! Json.toJson(teaminfo)
    }

    // match information
    case matchinfo: Match => {
      log.debug(s"GOT $matchinfo")
      out ! Json.toJson(matchinfo)
    }

    // Mark information
    case markinfo: Mark => {
      log.debug(s"GOT $markinfo")
      out ! Json.toJson(markinfo)
    }

    // Long term bet
    case lbetinfo: LongTermBet => {
      log.debug(s"GOT $lbetinfo")
      out ! Json.toJson(lbetinfo)
    }

    // Long term bet
    case hbetinfo: HedgeBet => {
      log.debug(s"GOT $hbetinfo")
      out ! Json.toJson(hbetinfo)
    }

    // match risk
    case matchriskinfo: MatchRisk => {
      log.debug(s"GOT $matchriskinfo")
      out ! Json.toJson(matchriskinfo)
    }

    // rating risk
    case ratingriskinfo: RatingRisk => {
      log.debug(s"GOT $ratingriskinfo")
      out ! Json.toJson(ratingriskinfo)
    }

    // general element
    case generaleleminfo: GeneralElement => {
      log.debug(s"GOT $generaleleminfo")
      out ! Json.toJson(generaleleminfo)
    }

    case x => log.debug(s"got: $x")
  }
}
