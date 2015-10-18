package client.components

import common._
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.WebSocket
import upickle.default._

import java.text._

case class MatchRiskState(m: Map[String, MatchRisk])
case class TeamState(m: Map[String, Team])
case class MatchState(m: Map[String, Match])
case class MarkState(m: Map[String, Mark])
case class LongTermBetState(m: Map[String, LongTermBet])
case class State(nav: Int)

// https://japgolly.github.io/scalajs-react/#examples/product-table
object StockTable {
  def getWebsocketUri(document: Document, nav: Int): String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"
    var url: String = ""

    nav match {
      case 0 =>
        url = s"$wsProtocol://${dom.document.location.host}/match_risk_ws"
      case 1 =>
        url = s"$wsProtocol://${dom.document.location.host}/longterm_bet_ws"
      case 2 =>
        url = s"$wsProtocol://${dom.document.location.host}/mark_ws"
      case 3 =>
        url = s"$wsProtocol://${dom.document.location.host}/match_ws"
      case 4 =>
        url = s"$wsProtocol://${dom.document.location.host}/team_ws"
    }
   
    url
  }

  // render
  class Backend1($: BackendScope[Map[String, MatchRisk], MatchRiskState]) {
    def stop() = {}

    def start() = {
      val uri = getWebsocketUri(dom.document, 0)
      val ws = new WebSocket(uri)
      ws.onopen = { (event: Event) => ws.send("hello") }
      ws.onclose = { (event: Event) => println(event) }
      ws.onerror = { (event: ErrorEvent) => println(event) }
      ws.onmessage = { (event: MessageEvent) =>
        val s: MatchRisk = read[MatchRisk](event.data.toString)
        matchrisk_values += s.id.toString() -> s
        $.modState(x => MatchRiskState(matchrisk_values))
      }
    }
  }

  var matchrisk_values = Map[String, MatchRisk]()
  val MatchRiskRow = ReactComponentB[MatchRisk]("MatchRiskRow")
    .render(s => {
        <.tr(
          <.td(s.kickoff),
          <.td(s.league),
          <.td(s.match_name),
          <.td(s.val_1),
          <.td(s.val_x),
          <.td(s.val_2)
        ) // tr
    }).build

  val MatchRiskArea = ReactComponentB[Map[String, MatchRisk]]("MatchRiskArea")
    .initialState(MatchRiskState(Map.empty))
    .backend(new Backend1(_))
    .render((P, S, B) => {
      <.table(^.id := "stocks-table", ^.className := "table-bordered",
        <.thead(
          <.tr(
            <.th("Kickoff"),
            <.th("League"),
            <.th("Match"),
            <.th("1"),
            <.th("X"),
            <.th("2")
          )),
        <.tbody(S.m.map(x => MatchRiskRow(x._2)))
      ) // table
    })
    .componentDidMount(_.backend.start())
    .componentWillUnmount(_.backend.stop())
    .build

  // Team
  class Backend2($: BackendScope[Map[String, Team], TeamState]) {
    def stop() = {}

    def start() = {
      val uri = getWebsocketUri(dom.document, 4)
      val ws = new WebSocket(uri)
      ws.onopen = { (event: Event) => ws.send("hello") }
      ws.onclose = { (event: Event) => println(event) }
      ws.onerror = { (event: ErrorEvent) => println(event) }
      ws.onmessage = { (event: MessageEvent) =>
        val s: Team = read[Team](event.data.toString)
        team_values += s.id.toString() -> s
        $.modState(x => TeamState(team_values))
      }
    }
  }

  var team_values = Map[String, Team]()
  val TeamRow = ReactComponentB[Team]("TeamRow")
    .render(s => {
        <.tr(
          <.td(s.league),
          <.td(s.name),
          <.td(s.played),
          <.td(s.current_points),
          <.td(s.current_global_difference),
          <.td(s.expeceted_remaining_points.formatted("%.2f")),
          <.td(s.total_expected_points.formatted("%.2f")),
          <.td(s.mean_expected_points.formatted("%.2f")),
          <.td(s.training_set_size),
          <.td(s.training_error.formatted("%.2f"))
        ) // tr
    }).build

  val TeamArea = ReactComponentB[Map[String, Team]]("TeamArea")
    .initialState(TeamState(Map.empty))
    .backend(new Backend2(_))
    .render((P, S, B) => {
      <.table(^.id := "stocks-table", ^.className := "table-bordered",
        <.thead(
          <.tr(
            <.th("League"),
            <.th("Name"),
            <.th("Played"),
            <.th("Points"),
            <.th("GD"),
            <.th("Expected Remaining Points"),
            <.th("Total Expected Points"),
            <.th("Mean Expected Points"),
            <.th("Training Set Size"),
            <.th("Training Error")
          )),
        <.tbody(S.m.map(x => TeamRow(x._2)))
      ) // table
    })
    .componentDidMount(_.backend.start())
    .componentWillUnmount(_.backend.stop())
    .build

  // Match
  class Backend3($: BackendScope[Map[String, Match], MatchState]) {
    def stop() = {}

    def start() = {
      val uri = getWebsocketUri(dom.document, 3)
      val ws = new WebSocket(uri)
      ws.onopen = { (event: Event) => ws.send("hello") }
      ws.onclose = { (event: Event) => println(event) }
      ws.onerror = { (event: ErrorEvent) => println(event) }
      ws.onmessage = { (event: MessageEvent) =>
        val s: Match = read[Match](event.data.toString)
        match_values += s.id.toString() -> s
        $.modState(x => MatchState(match_values))
      }
    }
  }

  var match_values = Map[String, Match]()
  val MatchRow = ReactComponentB[Match]("MatchRow")
    .render(s => {
        <.tr(
          <.td(s.kickoff),
          <.td(s.league),
          <.td(s.home_team),
          <.td(s.model_home_expected_points.formatted("%.2f")),
          <.td(s.market_home_expected_points.formatted("%.2f")),
          <.td(s.home_error.formatted("%.2f")),
          <.td(s.away_team),
          <.td(s.model_away_expected_points.formatted("%.2f")),
          <.td(s.market_away_expected_points.formatted("%.2f")),
          <.td(s.away_error.formatted("%.2f"))
        ) // tr
    }).build

  val MatchArea = ReactComponentB[Map[String, Match]]("MatchArea")
    .initialState(MatchState(Map.empty))
    .backend(new Backend3(_))
    .render((P, S, B) => {
      <.table(^.id := "stocks-table", ^.className := "table-bordered",
        <.thead(
          <.tr(
            <.th("Kickoff"),
            <.th("League"),
            <.th("Home Team"),
            <.th("Model Home Expected Points"),
            <.th("Market Home Expected Points"),
            <.th("Home Error"),
            <.th("Away Team"),
            <.th("Model Away Expected Points"),
            <.th("Market Away Expected Points"),
            <.th("Away Error")
          )),
        <.tbody(S.m.map(x => MatchRow(x._2)))
      ) // table
    })
    .componentDidMount(_.backend.start())
    .componentWillUnmount(_.backend.stop())
    .build

  // Mark
  class Backend4($: BackendScope[Map[String, Mark], MarkState]) {
    def stop() = {}

    def start() = {
      val uri = getWebsocketUri(dom.document, 2)
      val ws = new WebSocket(uri)
      ws.onopen = { (event: Event) => ws.send("hello") }
      ws.onclose = { (event: Event) => println(event) }
      ws.onerror = { (event: ErrorEvent) => println(event) }
      ws.onmessage = { (event: MessageEvent) =>
        val s: Mark = read[Mark](event.data.toString)
        mark_values += s.id.toString() -> s
        $.modState(x => MarkState(mark_values))
      }
    }
  }

  var mark_values = Map[String, Mark]()
  val MarkRow = ReactComponentB[Mark]("MarkRow")
    .render(s => {
        <.tr(
          <.td(s.league),
          <.td(s.market),
          <.td(s.team),
          <.td(s.bidder),
          <.td(s.bid.formatted("%.2f")),
          <.td(s.offer.formatted("%.2f")),
          <.td(s.seller),
          <.td(s.mark.formatted("%.2f")),
          <.td(s.risk.formatted("%.2f")),
          <.td(s.value_type),
          <.td(s.value.formatted("%.2f")),
          <.td(s.risk_adjusted_value.formatted("%.2f")),
          <.td(s.roe.formatted("%.2f"))
        ) // tr
    }).build

  val MarkArea = ReactComponentB[Map[String, Mark]]("MarkArea")
    .initialState(MarkState(Map.empty))
    .backend(new Backend4(_))
    .render((P, S, B) => {
      <.table(^.id := "stocks-table", ^.className := "table-bordered",
        <.thead(
          <.tr(
            <.th("League"),
            <.th("Market"),
            <.th("Team"),
            <.th("Bidder"),
            <.th("Bid"),
            <.th("Offer"),
            <.th("Seller"),
            <.th("Mark"),
            <.th("Risk"),
            <.th("Value Type"),
            <.th("Value"),
            <.th("Risk Adjusted Value"),
            <.th("ROE")
          )),
        <.tbody(S.m.map(x => MarkRow(x._2)))
      ) // table
    })
    .componentDidMount(_.backend.start())
    .componentWillUnmount(_.backend.stop())
    .build

  // LongTermBet
  class Backend5($: BackendScope[Map[String, LongTermBet], LongTermBetState]) {
    def stop() = {}

    def start() = {
      val uri = getWebsocketUri(dom.document, 1)
      val ws = new WebSocket(uri)
      ws.onopen = { (event: Event) => ws.send("hello") }
      ws.onclose = { (event: Event) => println(event) }
      ws.onerror = { (event: ErrorEvent) => println(event) }
      ws.onmessage = { (event: MessageEvent) =>
        val s: LongTermBet = read[LongTermBet](event.data.toString)
        lbet_values += s.id.toString() -> s
        $.modState(x => LongTermBetState(lbet_values))
      }
    }
  }

  var lbet_values = Map[String, LongTermBet]()
  val LBetRow = ReactComponentB[LongTermBet]("LBetRow")
    .render(s => {
        <.tr(
          <.td(s.date),
          <.td(s.league),
          <.td(s.market),
          <.td(s.team),
          <.td(s.CP),
          <.td(s.size),
          <.td(s.price),
          <.td(s.mark.formatted("%.2f")),
          <.td(s.value)
        ) // tr
    }).build

  val LongTermBetArea = ReactComponentB[Map[String, LongTermBet]]("LongTermBetArea")
    .initialState(LongTermBetState(Map.empty))
    .backend(new Backend5(_))
    .render((P, S, B) => {
      <.table(^.id := "stocks-table", ^.className := "table-bordered",
        <.thead(
          <.tr(
            <.th("Date"),
            <.th("League"),
            <.th("Market"),
            <.th("Team"),
            <.th("C/P"),
            <.th("Size"),
            <.th("Price"),
            <.th("Mark"),
            <.th("Value")
          )),
        <.tbody(S.m.map(x => LBetRow(x._2)))
      ) // table
    })
    .componentDidMount(_.backend.start())
    .componentWillUnmount(_.backend.stop())
    .build
  /*
   Left navbar links
  */

  val MainArea = ReactComponentB[Unit]("MainArea")
    .initialState(State(0))
    .render((_, S, _) => {
      <.div(^.className := "row",
        <.div(^.className := "col-sm-3 col-md-2 sidebar",
          <.ul(^.className := "nav nav-sidebar",
            <.li(<.a(^.href := "#", "Risks", ^.onClick --> {})),
            <.li(<.a(^.href := "#", "Bets", ^.onClick --> {})),
            <.li(<.a(^.href := "#", "Marks", ^.onClick --> {})),
            <.li(<.a(^.href := "#", "Matches", ^.onClick --> {})),
            <.li(<.a(^.href := "#", "Teams", ^.onClick --> {}))
          ) // ul
        ), // div sidebar

/*        S.nav match {
          case 0 =>
            <.div(^.className := "col-sm-12 col-md-10",
              <.div(^.className := "table-responsive", ^.id := "table_area0", MatchRiskArea(Map.empty))
            )
          case 1 =>
            <.div(^.className := "col-sm-12 col-md-10",
              <.div(^.className := "table-responsive", ^.id := "table_area1", LongTermBetArea(Map.empty))
            )
          case 2 =>
            <.div(^.className := "col-sm-12 col-md-10",
              <.div(^.className := "table-responsive", ^.id := "table_area2", MarkArea(Map.empty))
            )
          case 3 =>
            <.div(^.className := "col-sm-12 col-md-10",
              <.div(^.className := "table-responsive", ^.id := "table_area3", MatchArea(Map.empty))
            )
          case 4 =>
            <.div(^.className := "col-sm-12 col-md-10",
              <.div(^.className := "table-responsive", ^.id := "table_area4", TeamArea(Map.empty))
            )
        }*/

          <.div(^.className := "col-sm-12 col-md-10",
            <.div(^.className := "table-responsive", ^.id := "table_area0", MatchRiskArea(Map.empty)),
            <.div(^.className := "table-responsive", ^.id := "table_area1", LongTermBetArea(Map.empty)),
            <.div(^.className := "table-responsive", ^.id := "table_area2", MarkArea(Map.empty)),
            <.div(^.className := "table-responsive", ^.id := "table_area3", MatchArea(Map.empty)),
            <.div(^.className := "table-responsive", ^.id := "table_area4", TeamArea(Map.empty))
          )
              
      ) // div row
    })
    .build
 
  def apply() = MainArea(State(0))
}
