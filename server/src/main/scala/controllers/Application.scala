package controllers

import play.api.libs.json.JsValue
import play.api.mvc.{Action, Controller, WebSocket}
import play.libs.Akka
import services.{UserConnection, StockServer, TeamServer, MatchServer, MarkServer, LBetServer, HBetServer, MatchRiskServer, RatingRiskServer}

object Application extends Controller {
  import play.api.Logger
  import play.api.Play.current

  val stockServer = Akka.system.actorOf(StockServer.props)
  val teamServer = Akka.system.actorOf(TeamServer.props)
  val matchServer = Akka.system.actorOf(MatchServer.props)
  val markServer = Akka.system.actorOf(MarkServer.props)
  val hbetServer = Akka.system.actorOf(HBetServer.props)
  val lbetServer = Akka.system.actorOf(LBetServer.props)
  val matchriskServer = Akka.system.actorOf(MatchRiskServer.props)
  val ratingriskServer = Akka.system.actorOf(RatingRiskServer.props)

  def index = Action {
    Ok(views.html.index("REACTive Stocks Demo"))
  }

  def socket = WebSocket.acceptWithActor[String, JsValue] { request => out =>
    UserConnection.props(stockServer, out)
  }

  def teamsocket = WebSocket.acceptWithActor[String, JsValue] { request => out =>
    UserConnection.props(teamServer, out)
  }

  def matchsocket = WebSocket.acceptWithActor[String, JsValue] { request => out =>
    UserConnection.props(matchServer, out)
  }

  def marksocket = WebSocket.acceptWithActor[String, JsValue] { request => out =>
    UserConnection.props(markServer, out)
  }

  def longtermbetsocket = WebSocket.acceptWithActor[String, JsValue] { request => out =>
    UserConnection.props(lbetServer, out)
  }

  def hedgebetsocket = WebSocket.acceptWithActor[String, JsValue] { request => out =>
    UserConnection.props(hbetServer, out)
  }
  
  def matchrisksocket = WebSocket.acceptWithActor[String, JsValue] { request => out =>
    UserConnection.props(matchriskServer, out)
  }

  def ratingrisksocket = WebSocket.acceptWithActor[String, JsValue] { request => out =>
    UserConnection.props(ratingriskServer, out)
  }

  def logging = Action(parse.anyContent) { implicit request =>
    request.body.asJson.foreach { msg =>
      Logger.info(s"CLIENT - $msg")
    }
    Ok("")
  }
}
