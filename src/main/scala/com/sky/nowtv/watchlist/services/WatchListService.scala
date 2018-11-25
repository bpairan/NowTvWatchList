package com.sky.nowtv.watchlist.services

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import com.sky.nowtv.watchlist.common._
import javax.inject.Inject

import scala.collection.mutable

class WatchListService @Inject()(watchListMap: mutable.MultiMap[Customer, Content]) {

  def watchList(customer: Customer): WatchList = {
    WatchList(customer, watchListMap.get(customer))
  }

  def addWatchList(customer: Customer, contents: Set[Content]): List[InputValidation] = {
    validateAnd(customer, contents, (cu, co) => watchListMap.addBinding(cu, co))
  }

  def deleteWatchList(customer: Customer, contents: Set[Content]): List[InputValidation] = {
    validateAnd(customer, contents, (cu, co) => watchListMap.removeBinding(cu, co))
  }

  private def validateAnd(customer: Customer, contents: Set[Content], function: (Customer, Content) => Unit) = {
    ValidationService.isValidCustomer(customer) match {
      case Valid(cust) =>
        val validatedContents: List[ValidationResult[Content]] = contents.toList.map(content => ValidationService.isContentValid(content))
        cust.synchronized {
          validatedContents.collect { case Valid(a) => function(cust, a) }
        }
        val errors: NonEmptyList[List[InputValidation]] = validatedContents.collect { case Invalid(c) => c }.sequence
        errors.reduceLeft(_ ++ _)
      case Invalid(i) => i.toList
    }
  }
}
