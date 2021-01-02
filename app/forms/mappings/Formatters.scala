/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package forms.mappings

import models.{Country, Enumerable}
import play.api.data.FormError
import play.api.data.format.Formatter

import scala.util.control.Exception.nonFatalCatch
import scala.util.matching.Regex

trait Formatters {

  private[mappings] def stringFormatter(errorKey: String): Formatter[String] = new Formatter[String] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
      data.get(key) match {
        case None | Some("") => Left(Seq(FormError(key, errorKey)))
        case Some(s) => Right(s)
      }
    }

    override def unbind(key: String, value: String): Map[String, String] =
      Map(key -> value)
  }

  private[mappings] def booleanFormatter(requiredKey: String, invalidKey: String): Formatter[Boolean] =
    new Formatter[Boolean] {

      private val baseFormatter = stringFormatter(requiredKey)

      override def bind(key: String, data: Map[String, String]) =
        baseFormatter
          .bind(key, data)
          .right.flatMap {
          case "true" => Right(true)
          case "false" => Right(false)
          case _ => Left(Seq(FormError(key, invalidKey)))
        }

      def unbind(key: String, value: Boolean) = Map(key -> value.toString)
    }

  private[mappings] def intFormatter(requiredKey: String, wholeNumberKey: String, nonNumericKey: String, args: Seq[String] = Seq.empty): Formatter[Int] =
    new Formatter[Int] {

      val decimalRegexp = """^-?(\d*\.\d*)$"""

      private val baseFormatter = stringFormatter(requiredKey)

      override def bind(key: String, data: Map[String, String]) =
        baseFormatter
          .bind(key, data)
          .right.map(_.replace(",", ""))
          .right.flatMap {
          case s if s.matches(decimalRegexp) =>
            Left(Seq(FormError(key, wholeNumberKey, args)))
          case s =>
            nonFatalCatch
              .either(s.toInt)
              .left.map(_ => Seq(FormError(key, nonNumericKey, args)))
        }

      override def unbind(key: String, value: Int) =
        baseFormatter.unbind(key, value.toString)
    }

  private[mappings] val optionalStringFormatter: Formatter[Option[String]] = new Formatter[Option[String]] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[String]] =
      Right(
        data
          .get(key)
          .filter(_.lengthCompare(0) > 0)
      )

    override def unbind(key: String, value: Option[String]): Map[String, String] =
      Map(key -> value.getOrElse(""))
  }

  private[mappings] val optionalMaxLengthStringFormatter: Formatter[Option[String]] = new Formatter[Option[String]] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[String]] =
      Right(
        data
          .get(key)
          .filter(_.lengthCompare(0) > 0)
      )

    override def unbind(key: String, value: Option[String]): Map[String, String] =
      Map(key -> value.getOrElse(""))
  }


  private[mappings] def enumerableFormatter[A](requiredKey: String, invalidKey: String)(implicit ev: Enumerable[A]): Formatter[A] =
    new Formatter[A] {

      private val baseFormatter = stringFormatter(requiredKey)

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], A] =
        baseFormatter.bind(key, data).right.flatMap {
          str =>
            ev.withName(str).map(Right.apply).getOrElse(Left(Seq(FormError(key, invalidKey))))
        }

      override def unbind(key: String, value: A): Map[String, String] =
        baseFormatter.unbind(key, value.toString)
    }

  private[mappings] def stringTrimFormatter(errorKey: String): Formatter[String] = new Formatter[String] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] =
      data.get(key) match {
        case None => Left(Seq(FormError(key, errorKey)))
        case Some(s) => {s.trim match {
          case "" => Left(Seq(FormError(key, errorKey)))
          case s1  =>  Right (s1)
        }
        }
      }

    override def unbind(key: String, value: String): Map[String, String] =
      Map(key -> value)
  }

  protected def validatedOptionalTextFormatter(invalidKey: String,
                                               lengthKey: String,
                                               regex: String,
                                               length: Int): Formatter[Option[String]] = new Formatter[Option[String]] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[String]] = {
      data.get(key) match {
        case Some(str) if str.trim.length == 0 => Right(None)
        case Some(str) if !str.matches(regex)  => Left(Seq(FormError(key, invalidKey)))
        case Some(str) if str.length > length => Left(Seq(FormError(key, lengthKey)))
        case Some(str)  =>  Right(Some(str))
        case _ => Right(None)
      }
    }

    override def unbind(key: String, value: Option[String]): Map[String, String] = {
      Map(key -> value.getOrElse(""))
    }
  }

  protected def validatedOptionalTextAndMaxLengthFormatter(lengthKey: String,
                                                           length: Int): Formatter[Option[String]] = new Formatter[Option[String]] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[String]] = {
      data.get(key) match {
        case Some(str) if str.trim.length == 0 => Right(None)
        case Some(str) if str.length > length => Left(Seq(FormError(key, lengthKey)))
        case Some(str)  =>  Right(Some(str))
        case _ => Right(None)
      }
    }

    override def unbind(key: String, value: Option[String]): Map[String, String] = {
      Map(key -> value.getOrElse(""))
    }
  }

  protected def validatedTextFormatter(requiredKey: String,
                                       invalidKey: String,
                                       lengthKey: String,
                                       regex: String,
                                       maxLength: Int): Formatter[String] = new Formatter[String] {
    private val dataFormatter: Formatter[String] = stringTrimFormatter(requiredKey)
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
      dataFormatter
        .bind(key, data)
        .right.flatMap {
        case str if !str.matches(regex) => Left(Seq(FormError(key, invalidKey)))
        case str if str.length > maxLength => Left(Seq(FormError(key, lengthKey)))
        case str => Right(str)
      }
    }
    override def unbind(key: String, value: String): Map[String, String] = {
      Map(key -> value)
    }
  }

  protected def textMaxLengthFormatter(requiredKey: String,
                                       lengthKey: String,
                                       maxLength: Int): Formatter[String] = new Formatter[String] {
    private val dataFormatter: Formatter[String] = stringTrimFormatter(requiredKey)
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
      dataFormatter
        .bind(key, data)
        .right.flatMap {
        case str if str.length > maxLength => Left(Seq(FormError(key, lengthKey)))
        case str => Right(str)
      }
    }
    override def unbind(key: String, value: String): Map[String, String] = {
      Map(key -> value)
    }
  }

  protected def requiredRegexOnly(requiredKey: String,
                                  invalidKey: String,
                                  validFormatRegex: String) = new Formatter[String] {
    private val dataFormatter: Formatter[String] = stringTrimFormatter(requiredKey)
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
      dataFormatter
        .bind(key, data)
        .right.flatMap {
        case str if !str.matches(validFormatRegex) => Left(Seq(FormError(key, invalidKey)))
        case str => Right(str)
      }
    }
    override def unbind(key: String, value: String): Map[String, String] = {
      Map(key -> value)
    }
  }

  protected def maxLengthTextFormatter(requiredKey: String,
                                       lengthKey: String,
                                       maxLength: Int) = new Formatter[String] {
    private val dataFormatter: Formatter[String] = stringTrimFormatter(requiredKey)
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
      dataFormatter
        .bind(key, data)
        .right.flatMap {
        case str if str.length > maxLength => Left(Seq(FormError(key, lengthKey)))
        case str => Right(str)
      }
    }
    override def unbind(key: String, value: String): Map[String, String] = {
      Map(key -> value)
    }
  }

  protected def validatedArrangementID(requiredKey: String,
                                       invalidKey: String,
                                       countryList: Seq[Country]): Formatter[String] = new Formatter[String] {
    private val dataFormatter: Formatter[String] = stringTrimFormatter(requiredKey)
    private val arrangementIDRegex = "[A-Z]{2}[A]([2]\\d{3}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01]))([A-Z0-9]{6})"
    private val splitID: Regex = "(^[A-Za-z]{2})([A-Za-z0-9]+)".r

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
      dataFormatter
        .bind(key, data)
        .right.flatMap {
        case id if !id.toUpperCase.matches(arrangementIDRegex) => Left(Seq(FormError(key, invalidKey)))
        case id =>
          val splitID(countryCode, _) = id.toUpperCase

          if(countryList.exists(_.code == countryCode)) {
            Right(id)
          } else {
            Left(Seq(FormError(key, invalidKey)))
          }
      }
    }
    override def unbind(key: String, value: String): Map[String, String] = {
      Map(key -> value)
    }
  }
}
