package client.components

import common._
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.WebSocket
import upickle.default._

import java.text._

case class MatchState(m: Map[String, Match])

// https://japgolly.github.io/scalajs-react/#examples/product-table
object MatchTable {
  // Match
  class Backend($: BackendScope[Map[String, Match], MatchState]) {
    def stop() = {}

    def start() = {
      val uri = SocketURI.getWebsocketUri(dom.document, 3)
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
    .backend(new Backend(_))
    .render((P, S, B) => {
      <.table(^.id := "match-table", ^.className := "table-bordered",
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
 
  def apply() = MatchArea(Map.empty)
}
