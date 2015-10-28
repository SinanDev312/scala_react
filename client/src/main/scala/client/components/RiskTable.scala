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

// https://japgolly.github.io/scalajs-react/#examples/product-table
object RiskTable {
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
        <.tr(^.id := "matchrisk" + s.id,
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
      <.table(^.id := "matchrisk-table", ^.className := "table-bordered",
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

  def apply() = MatchRiskArea(Map.empty)
}
