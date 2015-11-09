package client.components

import common._
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.WebSocket
import upickle.default._

import java.text._

case class ElemState(m: Map[String, GeneralElement])

object GeneralTable {
  // GeneralElement
  class Backend($: BackendScope[Map[String, GeneralElement], ElemState]) {
    def stop() = {}

    def start() = {
      val uri = SocketURI.getWebsocketUri(dom.document, 7)
      val ws = new WebSocket(uri)
      ws.onopen = { (event: Event) => ws.send("hello") }
      ws.onclose = { (event: Event) => println(event) }
      ws.onerror = { (event: ErrorEvent) => println(event) }
      ws.onmessage = { (event: MessageEvent) =>
        val s: GeneralElement = read[GeneralElement](event.data.toString)
        team_values += s.id.toString() -> s
        $.modState(x => ElemState(team_values))
      }
    }
  }

  var team_values = Map[String, GeneralElement]()
  val ElemRow = ReactComponentB[GeneralElement]("ElemRow")
    .render(s => {
        <.tr(
          <.td(s.field1),
          <.td(s.field2),
          <.td(s.field3.formatted("%.2f"))
        ) // tr
    }).build

  val GenericArea = ReactComponentB[Map[String, GeneralElement]]("GenericArea")
    .initialState(ElemState(Map.empty))
    .backend(new Backend(_))
    .render((P, S, B) => {

      <.table(^.id := "team-table", ^.className := "table-bordered",
        <.thead(
          <.tr(
            <.th("Field1"),
            <.th("Field2"),
            <.th("Field3")
          )),
        <.tbody(S.m.map(x => ElemRow(x._2)))
      ); // table

      
    })
    .componentDidMount(_.backend.start())
    .componentWillUnmount(_.backend.stop())
    .build
 
  def apply() = GenericArea(Map.empty)
}
