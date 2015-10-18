package models

import common._
import play.api.mvc.WebSocket.FrameFormatter

object Formats {

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  /** Stock */

//  implicit val stockWrites = new Writes[Stock] {
//    def writes(v: Stock) = Json.obj( "symbol" -> v.symbol, "value" -> v.value )
//  }
//
//  implicit val stockReads: Reads[Stock] = (
//    (JsPath \ "symbol").read[String] and
//    (JsPath \ "value").read[Double]
//  )(Stock.apply _)
//
//  implicit val stockFormat = Json.format[Stock]
//  implicit val stockFrameFormatter = FrameFormatter.jsonFrame[Stock]
//
//  /** StockInfo */
//
//  implicit val stockInfoWrites = new Writes[StockInfo] {
//    def writes(v: StockInfo) = Json.obj(
//      "current" -> v.current, "diff" -> v.diff, "high" -> v.high, "low" -> v.low, "open" -> v.open
//    )
//  }

/*  implicit val stockInfoReads: Reads[StockInfo] = (
//    (JsPath \ "open").read[Double] and
    (JsPath \ "symbol").read[String] and
    (JsPath \ "current").read[Double] and
    (JsPath \ "low").read[Double] and
    (JsPath \ "high").read[Double] and
    (JsPath \ "diff").read[Double]
  )(StockInfo.apply _)*/

  implicit val stockInfoFormat = Json.format[StockInfo]
  implicit val stockInfoFrameFormatter = FrameFormatter.jsonFrame[StockInfo]

  implicit val teamFormat = Json.format[Team]
  implicit val teamFrameFormatter = FrameFormatter.jsonFrame[Team]

  implicit val matchFormat = Json.format[Match]
  implicit val matchFrameFormatter = FrameFormatter.jsonFrame[Match]

  implicit val markFormat = Json.format[Mark]
  implicit val markFrameFormatter = FrameFormatter.jsonFrame[Mark]

  implicit val longTermBetFormat = Json.format[LongTermBet]
  implicit val longTermBetFrameFormatter = FrameFormatter.jsonFrame[LongTermBet]

  implicit val hedgeBetFormat = Json.format[HedgeBet]
  implicit val hedgeBetFrameFormatter = FrameFormatter.jsonFrame[HedgeBet]

  implicit val matchRiskFormat = Json.format[MatchRisk]
  implicit val matchRiskFrameFormatter = FrameFormatter.jsonFrame[MatchRisk]

  implicit val ratingRiskFormat = Json.format[RatingRisk]
  implicit val ratingRiskFrameFormatter = FrameFormatter.jsonFrame[RatingRisk]

  /** SubscribeEvent */

  implicit val subscribeWrites = new Writes[SubscribeEvent] {
    def writes(v: SubscribeEvent) = Json.obj("subscriber" -> v.subscriber)
  }

//  implicit val subscribeReads: Reads[SubscribeEvent] = ( (JsPath \ "subscriber").read[String] )(SubscribeEvent.apply _)

  implicit val subscribeFormat = Json.format[SubscribeEvent]
  implicit val subscribeFrameFormatter = FrameFormatter.jsonFrame[SubscribeEvent]

  /** UnsubscribeEvent */

  implicit val unsubscribeWrites = new Writes[UnsubscribeEvent] {
    def writes(v: UnsubscribeEvent) = Json.obj("unsubscriber" -> v.unsubscriber)
  }

//  implicit val unsubscribeReads: Reads[UnsubscribeEvent] = ( (JsPath \ "unsubscriber").read[String] )(UnsubscribeEvent.apply _)

  implicit val unsubscribeFormat = Json.format[UnsubscribeEvent]
  implicit val unsubscribeFrameFormatter = FrameFormatter.jsonFrame[UnsubscribeEvent]

}
