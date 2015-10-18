package client

import client.components.{GlobalStyles, StockTable}
import common.StockInfo
import japgolly.scalajs.react.React
import japgolly.scalajs.react.extra.router2._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom
import org.scalajs.dom._

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scalacss.Defaults._
import scalacss.ScalaCssReact._

@JSExport("StockMain")
object StockMain extends JSApp {

  sealed trait Loc

  case object StocksLoc extends Loc

  def layout(c: RouterCtl[Loc], r: Resolution[Loc]) = {
    <.div(
      // here we use plain Bootstrap class names as these are specific to the top level layout defined here
      <.nav(^.className := "navbar navbar-inverse navbar-fixed-top")(
        <.div(^.className := "container")(
          <.div(^.className := "navbar-header")(<.span(^.className := "navbar-brand")("YC Football")),
          <.div(^.className := "collapse navbar-collapse")(
//            MainMenu(MainMenu.Props(c, r.page, TodoStore.todos))
          )
        )
      ),
      // currently active module is shown in this container
      <.div(^.className := "container")(r.render())
    )
  }

  @JSExport
  def main(): Unit = {
    //log.warn("Application starting")
    // send log messages also to the server
    //log.enableServerLogging("/logging")
    //log.info("This message goes to server as well")

    // Define the locations (pages) used in this application

    val routerConfig = RouterConfigDsl[Loc].buildConfig { dsl =>
      import dsl._
      (staticRoute(root, StocksLoc) ~> renderR(ctl => StockTable.MainArea(0))
        ).notFound(redirectToPage(StocksLoc)(Redirect.Replace))
    }.renderWith(layout)

    // create stylesheet
    GlobalStyles.addToDocument()
    // create the router
    val router = Router(BaseUrl(dom.window.location.href.takeWhile(_ != '#')), routerConfig)
    // tell React to render the router in the document body
    React.render(router(), dom.document.getElementById("root"))
  }
}