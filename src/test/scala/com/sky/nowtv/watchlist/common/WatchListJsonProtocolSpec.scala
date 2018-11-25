package com.sky.nowtv.watchlist.common

import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.Json

import scala.collection.mutable

class WatchListJsonProtocolSpec extends FlatSpec with Matchers with WatchListJsonProtocol {

  "Singe customer input" should "be parsed" in {
    val input =
      """
        |[
        |  { "customerId": "123" }
        |]
      """.stripMargin
    Json.parse(input).validate[List[Customer]].asOpt should contain(Seq(Customer("123")))
  }

  "Multiple customer input" should "be parsed into List of Customer" in {
    val input =
      """
        |[
        |  { "customerId": "123" },
        |  { "customerId": "456" }
        |]
      """.stripMargin
    Json.parse(input).validate[List[Customer]].asOpt should contain(Seq(Customer("123"), Customer("456")))
  }

  "Contents list" should "be returned for customers with data" in {
    val watchlist = Seq(
      WatchList(
        Customer("123"), Some(mutable.Set(Content("zRE49"), Content("wYqiZ"), Content("15nW5")))
      ),
      WatchList(
        Customer("456"), Some(mutable.Set(Content("srT5k"), Content("FBSxr"), Content("15nW5")))
      )
    )
    Json.toJson(watchlist).toString() shouldBe """[{"customerId":"123","contents":["zRE49","wYqiZ","15nW5"]},{"customerId":"456","contents":["srT5k","FBSxr","15nW5"]}]"""
  }

  it should "return empty watchlist contents" in {
    val watchlist = Seq(
      WatchList(
        Customer("123"), None
      ))
    Json.toJson(watchlist).toString() shouldBe """[{"customerId":"123","contents":[]}]"""
  }
}
