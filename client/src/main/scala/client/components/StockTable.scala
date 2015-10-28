package client.components

import common._
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.WebSocket
import upickle.default._
import client.components.{TeamTable, MatchTable, MarkTable, BetTable, RiskTable}

import java.text._

case class MainState(m: Int)

// https://japgolly.github.io/scalajs-react/#examples/product-table
object StockTable {
  /*
   Left navbar links
  */
  class Backend($: BackendScope[Map[String, Int], MainState]) {
    def stop() = {}
    def start() = {}
    def changeNav(tmp: Int){
      $.modState(x => MainState(tmp))
    }
  }

  val MainArea = ReactComponentB[Map[String, Int]]("MainArea")
    .initialState(MainState(0))
    .backend(new Backend(_))
    .render((P, S, B) => {
      <.div(^.className := "row",
        <.div(^.className := "col-sm-3 col-md-2 sidebar",
          <.ul(^.className := "nav nav-sidebar",
            <.li(<.a(^.href := "#", "Risks", ^.onClick --> {B.changeNav(0)})),
            <.li(<.a(^.href := "#", "Bets", ^.onClick --> {B.changeNav(1)})),
            <.li(<.a(^.href := "#", "Marks", ^.onClick --> {B.changeNav(2)})),
            <.li(<.a(^.href := "#", "Matches", ^.onClick --> {B.changeNav(3)})),
            <.li(<.a(^.href := "#", "Teams", ^.onClick --> {B.changeNav(4)}))
            ) // ul
          ), // div sidebar

        S.m match {
          case 0 =>
            <.div(^.className := "col-sm-12 col-md-10",
              <.div(^.className := "table-responsive", ^.id := "table_area0", RiskTable.MatchRiskArea(Map.empty))
            )
          case 1 =>
            <.div(^.className := "col-sm-12 col-md-10",
              <.div(^.className := "table-responsive", ^.id := "table_area1", BetTable.LongTermBetArea(Map.empty))
            )
          case 2 =>
            <.div(^.className := "col-sm-12 col-md-10",
              <.div(^.className := "table-responsive", ^.id := "table_area2", MarkTable.MarkArea(Map.empty))
            )
          case 3 =>
            <.div(^.className := "col-sm-12 col-md-10",
              <.div(^.className := "table-responsive", ^.id := "table_area3", MatchTable.MatchArea(Map.empty))
            )
          case 4 =>
            <.div(^.className := "col-sm-12 col-md-10",
              <.div(^.className := "table-responsive", ^.id := "table_area4", TeamTable.TeamArea(Map.empty))
            )
        }
        ) // div row
    })
    .componentDidMount(_.backend.start())
    .componentWillUnmount(_.backend.stop())
    .build
 
  def apply() = MainArea(Map.empty)
}
