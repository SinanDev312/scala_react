package client.components

import common._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.WebSocket
import upickle.default._
import java.text._

object SocketURI {
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
      case 5 =>
        url = s"$wsProtocol://${dom.document.location.host}/hedge_bet_ws"
      case 6 =>
        url = s"$wsProtocol://${dom.document.location.host}/rating_risk_ws"
      case 7 =>
        url = s"$wsProtocol://${dom.document.location.host}/general_ws"
    }
   
    url
  }
}