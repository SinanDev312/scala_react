package client.components

import common._
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.WebSocket
import upickle.default._
import java.text._

case class MarkState(m: Map[String, Mark])

// https://japgolly.github.io/scalajs-react/#examples/product-table
object MarkTable {
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
      <.table(^.id := "mark-table", ^.className := "table-bordered",
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

  def apply() = MarkArea(Map.empty)
}
