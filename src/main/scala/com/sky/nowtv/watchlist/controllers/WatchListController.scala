package com.sky.nowtv.watchlist.controllers

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import com.sky.nowtv.watchlist.common._
import com.sky.nowtv.watchlist.services.{ValidationService, WatchListService}
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{Json, JsValue}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WatchListController @Inject()(components: ControllerComponents,
                                    watchListService: WatchListService)(implicit exec: ExecutionContext)
  extends AbstractController(components) with WatchListJsonProtocol with ErrorJsonProtocol {

  def viewWatchlist(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[List[Customer]].fold(handleErrors, view)
  }

  def addWatchList(): Action[JsValue] = Action.async(parse.json) { request: Request[JsValue] =>
    request.body.validate[List[WatchList]].fold(handleErrors, add)
  }

  def deleteWatchList(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[List[WatchList]].fold(handleErrors, delete)

  }

  private def view(customers: List[Customer]): Future[Result] = {
    Future {
      val validated: List[ValidationResult[Customer]] = customers.map(ValidationService.isValidCustomer)
      val errors: NonEmptyList[List[InputValidation]] = validated.collect { case Invalid(e) => e }.sequence
      val watchlist: Seq[WatchList] = validated.collect { case Valid(customer) => watchListService.watchList(customer) }
      Ok(Json.obj("watchlist" -> watchlist, "errors" -> errors))
    }
  }

  private def add(watchList: List[WatchList]): Future[Result] = {
    Future {
      val watchListMap: Map[Customer, Set[Content]] = watchList.groupBy(_.customer).mapValues(_.flatMap(_.contents).foldLeft(Set[Content]())(_ ++ _))
      val result = watchListMap.map { case (customer, contents) =>
        val errors = watchListService.addWatchList(customer, contents)
        Json.obj("customerId" -> customer.id, "errors" -> errors)
      }
      Ok(Json.toJson(result))
    }
  }

  private def delete(watchList: List[WatchList]): Future[Result] = {
    Future {
      val watchListMap: Map[Customer, Set[Content]] = watchList.groupBy(_.customer).mapValues(_.flatMap(_.contents).foldLeft(Set[Content]())(_ ++ _))
      val result = watchListMap.map { case (customer, contents) =>
        val errors = watchListService.deleteWatchList(customer, contents)
        Json.obj("customerId" -> customer.id, "errors" -> errors)
      }
      Ok(Json.toJson(result))
    }
  }
}
