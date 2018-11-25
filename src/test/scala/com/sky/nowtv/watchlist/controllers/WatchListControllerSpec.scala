package com.sky.nowtv.watchlist.controllers

import com.google.inject.Injector
import com.sky.nowtv.watchlist.common._
import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import org.apache.http.protocol.HTTP
import org.scalatestplus.play.{PlaySpec, WsScalaTestClient}
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.collection.mutable

class WatchListControllerSpec extends PlaySpec with GuiceOneAppPerTest with WsScalaTestClient with WatchListJsonProtocol {

  "Invalid request type" must {
    "return Unsupported Media type" in {
      val result = route(app, FakeRequest("GET", "/watchlist/view")).get
      status(result) mustBe UNSUPPORTED_MEDIA_TYPE
    }
  }

  "Invalid json request" must {
    "return Bad Request for view watchlist" in {
      val result = route(app, FakeRequest("GET", "/watchlist/view")
        .withHeaders(HTTP.CONTENT_TYPE -> "application/json"))
        .get
      status(result) mustBe BAD_REQUEST
    }

    "return Bad Request for add watchlist" in {
      val result = route(app, FakeRequest("POST", "/watchlist/add")
        .withHeaders(HTTP.CONTENT_TYPE -> "application/json"))
        .get
      status(result) mustBe BAD_REQUEST
    }

    "return Bad Request for delete watchlist" in {
      val result = route(app, FakeRequest("POST", "/watchlist/delete")
        .withHeaders(HTTP.CONTENT_TYPE -> "application/json"))
        .get
      status(result) mustBe BAD_REQUEST
    }
  }

  "Incorrect json request" must {
    "return Bad Request for view watchlist" in {
      val result = route(app, FakeRequest("GET", "/watchlist/view")
        .withHeaders(HTTP.CONTENT_TYPE -> "application/json")
        .withBody("""{"customerId": "123"}"""))
        .get
      println(contentAsString(result))
      status(result) mustBe BAD_REQUEST
    }
    "return Bad Request for add watchlist" in {
      val result = route(app, FakeRequest("POST", "/watchlist/add")
        .withHeaders(HTTP.CONTENT_TYPE -> "application/json")
        .withBody("""{"customerId": "123"}"""))
        .get
      println(contentAsString(result))
      status(result) mustBe BAD_REQUEST
    }
    "return Bad Request for delete watchlist" in {
      val result = route(app, FakeRequest("POST", "/watchlist/delete")
        .withHeaders(HTTP.CONTENT_TYPE -> "application/json")
        .withBody("""{"customerId": "123"}"""))
        .get
      println(contentAsString(result))
      status(result) mustBe BAD_REQUEST
    }
  }

  "Empty watchlist" must {
    "be returned for a customer without prior watchlist" in {
      val result = route(app, FakeRequest("GET", "/watchlist/view")
        .withHeaders(HTTP.CONTENT_TYPE -> "application/json")
        .withBody("""[{"customerId": "123"}]"""))
        .get
      status(result) mustBe OK
      contentAsString(result) mustBe """{"watchlist":[{"customerId":"123","contents":[]}],"errors":[]}"""
    }
  }

  "Customer Watchlist" must {
    "be returned" in {
      val watchlistMap = app.injector.instanceOf[Injector].instance[mutable.MultiMap[Customer, Content]]
      watchlistMap.addBinding(Customer("123"), Content("zRE49"))
      watchlistMap.addBinding(Customer("123"), Content("wYqiZ"))
      watchlistMap.addBinding(Customer("123"), Content("15nW5"))
      watchlistMap.addBinding(Customer("123"), Content("srT5k"))
      watchlistMap.addBinding(Customer("123"), Content("FBSxr"))

      watchlistMap.addBinding(Customer("abc"), Content("hWjNK"))
      watchlistMap.addBinding(Customer("abc"), Content("U8jVg"))
      watchlistMap.addBinding(Customer("abc"), Content("GH4pD"))
      watchlistMap.addBinding(Customer("abc"), Content("rGIha"))

      val result = route(app, FakeRequest("GET", "/watchlist/view")
        .withHeaders(HTTP.CONTENT_TYPE -> "application/json")
        .withBody("""[{"customerId": "123"}]"""))
        .get
      status(result) mustBe OK
      val responseJson = Json.parse(contentAsString(result))
      val contents: Seq[WatchList] = (responseJson \ "watchlist").validate[Seq[WatchList]].getOrElse(Seq())

      contents.filter(w => w.customer.id == "123").head.contents must contain(
        mutable.Set(Content("zRE49"), Content("wYqiZ"), Content("15nW5"), Content("srT5k"), Content("FBSxr"))
      )
    }
  }

  "Invalid Customer" must {
    "return validation error for view watchlist" in {
      val result = route(app, FakeRequest("GET", "/watchlist/view")
        .withHeaders(HTTP.CONTENT_TYPE -> "application/json")
        .withBody("""[{"customerId": "1234"}]"""))
        .get
      status(result) mustBe OK
      contentAsString(result) mustBe """{"watchlist":[],"errors":[{"invalidCustomer":"1234"}]}"""
    }

    "return validation error for add watchlist" in {
      val result = route(app, FakeRequest("POST", "/watchlist/add")
        .withHeaders(HTTP.CONTENT_TYPE -> "application/json")
        .withBody("""[{"customerId": "1234","contents": ["zRE49", "wYqiZ", "15nW5", "srT5k", "FBSxr"]}]"""))
        .get
      status(result) mustBe OK
      contentAsString(result) mustBe """[{"customerId":"1234","errors":[{"invalidCustomer":"1234"}]}]"""
    }

    "return validation error for delete watchlist" in {
      val result = route(app, FakeRequest("POST", "/watchlist/delete")
        .withHeaders(HTTP.CONTENT_TYPE -> "application/json")
        .withBody("""[{"customerId": "1234","contents": ["zRE49", "wYqiZ", "15nW5", "srT5k", "FBSxr"]}]"""))
        .get
      status(result) mustBe OK
      contentAsString(result) mustBe """[{"customerId":"1234","errors":[{"invalidCustomer":"1234"}]}]"""
    }
  }


  "Watchlist for customer" must {
    "be added" in {
      val result = route(app, FakeRequest("POST", "/watchlist/add")
        .withHeaders(HTTP.CONTENT_TYPE -> "application/json")
        .withBody(
          """[
            |  {
            |    "customerId": "123",
            |    "contents": ["zRE49", "wYqiZ", "15nW5", "srT5k", "FBSxr"]
            |  },
            |  {
            |    "customerId": "abc",
            |    "contents": ["hWjNK", "U8jVg", "GH4pD", "rGIha"]
            |  }
            |]""".stripMargin))
        .get
      status(result) mustBe OK
      contentAsString(result) mustBe """[{"customerId":"abc","errors":[]},{"customerId":"123","errors":[]}]"""
      val watchlistMap = app.injector.instanceOf[Injector].instance[mutable.MultiMap[Customer, Content]]
      watchlistMap.get(Customer("123")) must contain(
        mutable.Set(Content("zRE49"), Content("wYqiZ"), Content("15nW5"), Content("srT5k"), Content("FBSxr"))
      )
      watchlistMap.get(Customer("abc")) must contain(
        mutable.Set(Content("hWjNK"), Content("U8jVg"), Content("GH4pD"), Content("rGIha"))
      )
    }

    "be deleted" in {
      val watchlistMap = app.injector.instanceOf[Injector].instance[mutable.MultiMap[Customer, Content]]
      watchlistMap.addBinding(Customer("123"), Content("zRE49"))
      watchlistMap.addBinding(Customer("123"), Content("wYqiZ"))
      watchlistMap.addBinding(Customer("123"), Content("15nW5"))
      watchlistMap.addBinding(Customer("123"), Content("srT5k"))
      watchlistMap.addBinding(Customer("123"), Content("FBSxr"))

      watchlistMap.addBinding(Customer("abc"), Content("hWjNK"))
      watchlistMap.addBinding(Customer("abc"), Content("U8jVg"))
      watchlistMap.addBinding(Customer("abc"), Content("GH4pD"))
      watchlistMap.addBinding(Customer("abc"), Content("rGIha"))

      val result = route(app, FakeRequest("POST", "/watchlist/delete")
        .withHeaders(HTTP.CONTENT_TYPE -> "application/json")
        .withBody(
          """[
            |  {
            |    "customerId": "123",
            |    "contents": ["15nW5"]
            |  },
            |  {
            |    "customerId": "abc",
            |    "contents": ["hWjNK"]
            |  }
            |]""".stripMargin))
        .get
      status(result) mustBe OK
      println(contentAsString(result))
      watchlistMap.get(Customer("123")) must contain(
        mutable.Set(Content("zRE49"), Content("wYqiZ"), Content("srT5k"), Content("FBSxr"))
      )
      watchlistMap.get(Customer("abc")) must contain(
        mutable.Set(Content("U8jVg"), Content("GH4pD"), Content("rGIha"))
      )
    }
  }

  "Invalid content" must {
    "return validation error for add" in {
      val result = route(app, FakeRequest("POST", "/watchlist/add")
        .withHeaders(HTTP.CONTENT_TYPE -> "application/json")
        .withBody(
          """[
            |  {
            |    "customerId": "123",
            |    "contents": ["zRE49_", "wYqiZ", "15nW5", "srT5k", "FBSxr"]
            |  },
            |  {
            |    "customerId": "abc",
            |    "contents": ["$hWjNK", "U8jVg", "GH4pD", "rGIha"]
            |  }
            |]""".stripMargin))
        .get
      status(result) mustBe OK
      contentAsString(result) mustBe """[{"customerId":"abc","errors":[{"invalidContent":"$hWjNK"}]},{"customerId":"123","errors":[{"invalidContent":"zRE49_"}]}]"""

      val watchlistMap = app.injector.instanceOf[Injector].instance[mutable.MultiMap[Customer, Content]]
      watchlistMap.get(Customer("123")) must contain(
        mutable.Set(Content("wYqiZ"), Content("15nW5"), Content("srT5k"), Content("FBSxr"))
      )
      watchlistMap.get(Customer("abc")) must contain(
        mutable.Set(Content("U8jVg"), Content("GH4pD"), Content("rGIha"))
      )
    }

    "return validation error for delete" in {
      val watchlistMap = app.injector.instanceOf[Injector].instance[mutable.MultiMap[Customer, Content]]
      watchlistMap.addBinding(Customer("123"), Content("zRE49"))
      watchlistMap.addBinding(Customer("123"), Content("wYqiZ"))
      watchlistMap.addBinding(Customer("123"), Content("15nW5"))
      watchlistMap.addBinding(Customer("123"), Content("srT5k"))
      watchlistMap.addBinding(Customer("123"), Content("FBSxr"))

      watchlistMap.addBinding(Customer("abc"), Content("hWjNK"))
      watchlistMap.addBinding(Customer("abc"), Content("U8jVg"))
      watchlistMap.addBinding(Customer("abc"), Content("GH4pD"))
      watchlistMap.addBinding(Customer("abc"), Content("rGIha"))

      val result = route(app, FakeRequest("POST", "/watchlist/delete")
        .withHeaders(HTTP.CONTENT_TYPE -> "application/json")
        .withBody(
          """[
            |  {
            |    "customerId": "123",
            |    "contents": ["xxxx"]
            |  },
            |  {
            |    "customerId": "abc",
            |    "contents": ["abc"]
            |  }
            |]""".stripMargin))
        .get
      status(result) mustBe OK
      contentAsString(result) mustBe """[{"customerId":"abc","errors":[{"invalidContent":"abc"}]},{"customerId":"123","errors":[{"invalidContent":"xxxx"}]}]"""
      watchlistMap.get(Customer("123")) must contain(
        mutable.Set(Content("zRE49"), Content("wYqiZ"), Content("15nW5"), Content("srT5k"), Content("FBSxr"))
      )
      watchlistMap.get(Customer("abc")) must contain(
        mutable.Set(Content("hWjNK"), Content("U8jVg"), Content("GH4pD"), Content("rGIha"))
      )
    }
  }


}
