package connectors

import config.FrontendAppConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import javax.inject.Inject
import uk.gov.hmrc.http.HttpReads.Implicits._
import scala.concurrent.ExecutionContext

class HistoryConnector @Inject()(configuration: FrontendAppConfig,
                                                 httpClient: HttpClient)(implicit ec: ExecutionContext) {
    
    val baseUrl = s"${configuration.crossBorderArrangementsUrl}/disclose-cross-border-arrangements"

    def getSubmissionsUrl(enrolmentid: String) = s"$baseUrl/history/submissions/$enrolmentid"

    def getSubmissionDetails(enrolmentid: String)(implicit hc: HeaderCarrier) = {
            httpClient.GET[HttpResponse](getSubmissionsUrl(enrolmentid)).map { response =>
            response.status match {
                case 200 => true
                case _ => false
            }
            } recover {
               case _: Exception => false
            }
    }
  
}
