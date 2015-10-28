package client.components

import common._
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.WebSocket
import upickle.default._

import java.text._

case class TeamState(m: Map[String, Team])

object TeamTable {
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
      <.table(^.id := "team-table", ^.className := "table-bordered",
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
 
  def apply() = TeamArea(Map.empty)
}
