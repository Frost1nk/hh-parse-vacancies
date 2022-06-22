import JsonType.{OrganizationId, VacancyInfo}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import spray.json.{JsArray, JsObject, PrettyPrinter}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object RequestTest extends App with JsonSupport with PrettyPrinter {

    implicit val acsys: ActorSystem = ActorSystem()
    implicit val ex: ExecutionContextExecutor = acsys.dispatcher
    val id  = "54291898"
    val req: Future[Object] = Http().singleRequest(
      HttpRequest(
        method = HttpMethods.GET,
        uri = Uri(s"https://api.hh.ru/vacancies?text=!React&area=1&professional_roles=96&only_with_salary=true&page=1&per_page=20"),
        entity = HttpEntity(ContentTypes.`application/json`,"")
        )
    ).flatMap { value =>
      Unmarshal(value.entity).to[JsObject]
        .map(_.fields.getOrElse("items", JsArray.empty))
        .map(json => json.convertTo[Iterable[OrganizationId]]).recoverWith {
        case e: NullPointerException =>
          println(e.printStackTrace())
          Future(e)
      }

    }

    req.onComplete{
      case Success(value) =>
        println(value)
        acsys.terminate()
      case Failure(exception) =>
        println(exception.printStackTrace())
        acsys.terminate()
    }

    def printEnglish(vacancyInfo: VacancyInfo): String = {
      vacancyInfo.key_skills.flatMap(_.find(_.name.contains("Англ"))).map(_.name).getOrElse("")
    }
}
