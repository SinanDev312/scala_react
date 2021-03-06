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
case class HedgeBetState(m: Map[String, HedgeBet])

// https://japgolly.github.io/scalajs-react/#examples/product-table
object BetTable {
  val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"

  // LongTermBet
  class Backend1($: BackendScope[Map[String, LongTermBet], LongTermBetState]) {
    def stop() = {}

    def start() = {
      val uri = SocketURI.getWebsocketUri(dom.document, 1)
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
    .backend(new Backend1(_))
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
 
  // HedgeBet
  class Backend2($: BackendScope[Map[String, HedgeBet], HedgeBetState]) {
    def stop() = {}

    def start() = {
      val uri = SocketURI.getWebsocketUri(dom.document, 5)
      val ws = new WebSocket(uri)
      ws.onopen = { (event: Event) => ws.send("hello") }
      ws.onclose = { (event: Event) => println(event) }
      ws.onerror = { (event: ErrorEvent) => println(event) }
      ws.onmessage = { (event: MessageEvent) =>
        val s: HedgeBet = read[HedgeBet](event.data.toString)
        hbet_values += s.id.toString() -> s
        $.modState(x => HedgeBetState(hbet_values))
      }
    }
  }

  var hbet_values = Map[String, HedgeBet]()
  val HBetRow = ReactComponentB[HedgeBet]("HBetRow")
    .render(s => {
        <.tr(
          <.td(s.date),
          <.td(s.league),
          <.td(s.fmatch),
          <.td(s.selection),
          <.td(s.CP),
          <.td(s.size),
          <.td(s.price.formatted("%.2f")),
          <.td(s.result),
          <.td(s.PL)
        ) // tr
    }).build

  val HedgeBetArea = ReactComponentB[Map[String, HedgeBet]]("HedgeBetArea")
    .initialState(HedgeBetState(Map.empty))
    .backend(new Backend2(_))
    .render((P, S, B) => {
      <.table(^.id := "longtermbet-table", ^.className := "table-bordered",
        <.thead(
          <.tr(
            <.th("Date"),
            <.th("League"),
            <.th("Match"),
            <.th("Selection"),
            <.th("C/P"),
            <.th("Size"),
            <.th("Price"),
            <.th("Result"),
            <.th("P&L")
          )),
        <.tbody(S.m.map(x => HBetRow(x._2)))
      ) // table
    })
    .componentDidMount(_.backend.start())
    .componentWillUnmount(_.backend.stop())
    .build

  class TabBackend($: BackendScope[Map[String, Int], TabState]) {
    def stop() = {}
    def start() = {}

    def changeTab(m: Int) = {
      $.modState(x => TabState(m))
    }
  }

  val TabArea = ReactComponentB[Map[String, Int]]("TabArea")
    .initialState(TabState(0))
    .backend(new TabBackend(_))
    .render((P, S, B) => {
      <.div(^.className := "tabs", ^.height := "500px", 
        <.div(^.className := "tab",
          <.input(^.`type` := "radio", ^.id := "ltab" , ^.name := "BetAreaGroup", ^.checked := {S.m == 0}, ^.onClick --> {B.changeTab(0)}), 
          <.label(^.`for` := "ltab", "Long Term Bet"), 
          <.div(^.className := "content", LongTermBetArea(Map.empty))
        ),
        <.div(^.className := "tab",
          <.input(^.`type` := "radio", ^.id := "htab" , ^.name := "BetAreaGroup", ^.checked := {S.m == 1}, ^.onClick --> {B.changeTab(1)}), 
          <.label(^.`for` := "htab", "Hedge Bet"), 
          <.div(^.className := "content", HedgeBetArea(Map.empty))
        )
      )
    })
    .componentDidMount(_.backend.start())
    .componentWillUnmount(_.backend.stop())
    .build

  def apply() = LongTermBetArea(Map.empty)
}
