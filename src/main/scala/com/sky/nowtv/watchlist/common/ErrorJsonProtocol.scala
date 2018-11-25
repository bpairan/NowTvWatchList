package com.sky.nowtv.watchlist.common

import cats.data.NonEmptyList
import play.api.libs.json._
import play.api.libs.json.Json.toJson
import play.api.mvc.Result
import play.api.mvc.Results._

import scala.concurrent.{ExecutionContext, Future}

trait ErrorJsonProtocol {

  def handleErrors(errors: Seq[(JsPath, Seq[JsonValidationError])])(implicit ec: ExecutionContext): Future[Result] = Future {
    BadRequest(Json.obj("status" -> "Invalid input", "message" -> JsError.toJson(errors)))
  }

  implicit val InvalidCustomerWrites: Writes[InputValidation] = inputValidation => Json.obj(inputValidation.reason -> inputValidation.cause)

  implicit val inputValidationWrites: Writes[NonEmptyList[List[InputValidation]]] = nonEmptyList => {
    toJson(nonEmptyList.reduceLeft(_ ++ _))
  }
}
