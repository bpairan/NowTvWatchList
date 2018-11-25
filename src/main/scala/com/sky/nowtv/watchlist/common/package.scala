package com.sky.nowtv.watchlist

import cats.data.ValidatedNel

import scala.collection.mutable

package object common {

  type ValidationResult[A] = ValidatedNel[InputValidation, A]

  sealed trait InputValidation {
    val cause: String
    val reason:String
  }

  case class InvalidCustomer(cause: String) extends InputValidation {
    val reason = "invalidCustomer"
  }

  case class InvalidContent(cause: String) extends InputValidation {
    val reason = "invalidContent"
  }

  case class Content(id: String)

  case class Customer(id: String)

  class Response(watchList: WatchList, errors: List[InputValidation])

  case class WatchList(customer: Customer, contents: Option[mutable.Set[Content]])

}
