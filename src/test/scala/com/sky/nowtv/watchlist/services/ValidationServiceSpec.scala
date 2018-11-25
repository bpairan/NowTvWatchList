package com.sky.nowtv.watchlist.services

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import com.sky.nowtv.watchlist.common.{Content, Customer, InvalidContent, InvalidCustomer}
import org.scalatest.{FlatSpec, Matchers}

class ValidationServiceSpec extends FlatSpec with Matchers {

  "Customer id" should "be valid" in {
    ValidationService.isValidCustomer(Customer("123")) shouldBe Valid(Customer("123"))
    ValidationService.isValidCustomer(Customer("A12")) shouldBe Valid(Customer("A12"))
  }

  it should "be invalid" in {
    ValidationService.isValidCustomer(Customer("1234")) shouldBe Invalid(NonEmptyList.of(InvalidCustomer("1234")))
    ValidationService.isValidCustomer(Customer("_12")) shouldBe Invalid(NonEmptyList.of(InvalidCustomer("_12")))
    ValidationService.isValidCustomer(Customer("1.23")) shouldBe Invalid(NonEmptyList.of(InvalidCustomer("1.23")))
    ValidationService.isValidCustomer(Customer("")) shouldBe Invalid(NonEmptyList.of(InvalidCustomer("")))
  }

  "Content" should "be valid" in {
    ValidationService.isContentValid(Content("srT5k")) shouldBe Valid(Content("srT5k"))
    ValidationService.isContentValid(Content("ABCDE")) shouldBe Valid(Content("ABCDE"))
    ValidationService.isContentValid(Content("12345")) shouldBe Valid(Content("12345"))
  }

  it should "be invalid" in {
    ValidationService.isContentValid(Content("srT5kK")) shouldBe Invalid(NonEmptyList.of(InvalidContent("srT5kK")))
    ValidationService.isContentValid(Content("$rT5k")) shouldBe Invalid(NonEmptyList.of(InvalidContent("$rT5k")))
    ValidationService.isContentValid(Content("123.4")) shouldBe Invalid(NonEmptyList.of(InvalidContent("123.4")))
  }
}
