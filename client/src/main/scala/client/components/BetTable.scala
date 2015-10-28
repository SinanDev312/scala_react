package client.components

import common._
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.WebSocket
import upickle.default._
import java.text._

case class LongTermBetState(m: Map[String, LongTermBet])

// https://japgolly.github.io/scalajs-react/#examples/product-table
object BetTable {
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
      <.table(^.id := "longtermbet-table", ^.className := "table-bordered",
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
 
  def apply() = LongTermBetArea(Map.empty)
}
