package pages.organisation

import converters.Converter.PageList
import converters.{Converter, PageConverter}
import models.{Address, UserAnswers}
import models.organisation.Organisation
import pages.QuestionPage

object OrganisationPages extends PageConverter[Organisation] {

  val addressList: PageList = List(
    IsOrganisationAddressKnownPage
    , IsOrganisationAddressUkPage
    , SelectAddressPage
    , PostcodePage
    , OrganisationAddressPage
  )

  val emailList: PageList = List(
    EmailAddressQuestionForOrganisationPage
    , EmailAddressForOrganisationPage
  )

  val tinList: PageList = List(
    WhichCountryTaxForOrganisationPage
    , DoYouKnowAnyTINForUKOrganisationPage
    , WhatAreTheTaxNumbersForUKOrganisationPage
    , DoYouKnowTINForNonUKOrganisationPage
    , WhatAreTheTaxNumbersForNonUKOrganisationPage
    , IsOrganisationResidentForTaxOtherCountriesPage
    , OrganisationLoopPage
  )

  val converters: UserAnswers => Seq[Converter[Organisation]] = userAnswers => List (
    Converter(List(OrganisationNamePage), toName(userAnswers))
    , Converter(addressList             , toAddress(userAnswers))
    , Converter(emailList               , toEmail(userAnswers))
    , Converter(tinList)
  )

  def toName(userAnswers: UserAnswers)(organisation: Organisation, pageList: PageList): Organisation =
    pageList.headOption.flatMap{ case a: QuestionPage[String] => userAnswers.get[String](a) }
      .fold(organisation) { name => organisation.copy(organisationName = name) }

  def toAddress(userAnswers: UserAnswers)(organisation: Organisation, pageList: PageList): Organisation =
    pageList.find(_.isInstanceOf[IsOrganisationAddressKnownPage.type])
      .fold(organisation){ case a: QuestionPage[Boolean] =>
        if (userAnswers.get[Boolean](a).contains(true)) {
          pageList.find(_.isInstanceOf[OrganisationAddressPage.type])
            .fold(organisation){ case a: QuestionPage[Address] =>
              organisation.copy(address = userAnswers.get[Address](a))
            }
        } else {
          organisation
        }
      }

  def toEmail(userAnswers: UserAnswers)(organisation: Organisation, pageList: PageList): Organisation =
    pageList.find(_.isInstanceOf[EmailAddressQuestionForOrganisationPage.type])
      .fold(organisation){ case a: QuestionPage[Boolean] =>
        if (userAnswers.get[Boolean](a).contains(true)) {
          pageList.find(_.isInstanceOf[EmailAddressForOrganisationPage.type])
            .fold(organisation){ case a: QuestionPage[String] =>
              organisation.copy(emailAddress = userAnswers.get[String](a))
            }
        } else {
          organisation
        }
      }

  def toTins(userAnswers: UserAnswers)(organisation: Organisation, pageList: PageList): Organisation = ???

}
