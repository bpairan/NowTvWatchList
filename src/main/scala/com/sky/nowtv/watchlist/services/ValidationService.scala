package com.sky.nowtv.watchlist.services

import cats.implicits._
import com.sky.nowtv.watchlist.common._

object ValidationService {

  def isValidCustomer(customer: Customer): ValidationResult[Customer] = {
    if (customer.id.length != 3 || !customer.id.forall(_.isLetterOrDigit)) {
      InvalidCustomer(customer.id).invalidNel
    } else {
      customer.validNel
    }
  }

  def isContentValid(content: Content): ValidationResult[Content] = {
    if (content.id.length != 5 || !content.id.forall(_.isLetterOrDigit)) {
      InvalidContent(content.id).invalidNel
    } else {
      content.validNel
    }
  }

}
