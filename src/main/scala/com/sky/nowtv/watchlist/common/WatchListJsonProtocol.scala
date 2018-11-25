package com.sky.nowtv.watchlist.common

import cats.syntax.option.catsSyntaxOptionId
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Json.toJson
import play.api.libs.json.Reads._

import scala.collection.mutable

trait WatchListJsonProtocol {

  implicit val watchListGetReads: Reads[Customer] = (__ \ "customerId").read[String].map(Customer)

  implicit val watchListGetWrites: Writes[Seq[WatchList]] = watchListSeq =>
    toJson(watchListSeq.map { watchList =>
      Json.obj("customerId" -> watchList.customer.id,
        "contents" -> watchList.contents.getOrElse(Seq()).map(_.id))
    })

  implicit val AddContentsReads: Reads[WatchList] = (
    (__ \ "customerId").read[String].map(Customer) and
      (__ \ "contents").read[Seq[String]].map(_.map(Content))
    ) {
    (customer, contents) => WatchList(customer, mutable.Set(contents: _*).some)
  }
}
