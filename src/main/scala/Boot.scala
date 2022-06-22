import JsonType.{KeySkill, OrganizationId, Salary, VacancyInfo}
import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.{FileIO, Flow, GraphDSL, RunnableGraph, Source}
import akka.stream.{ClosedShape, Materializer}
import akka.util.ByteString
import spray.json.{JsArray, JsObject, PrettyPrinter}

import java.io.File
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future, Promise}


object Boot extends App with JsonSupport with PrettyPrinter {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer.createMaterializer(actorSystem)
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher
  val promise: Promise[Int] = Promise[Int]()
  val iterator = Range.inclusive(0, 90).iterator
  val separator = "!!!"
  var counter = 0
  val searchVacancy = "QA"
  val areaId = 1550


  def searchByOrganisation(page: Int): Iterable[OrganizationId] = {
    println("# page " + page)
    val result = Http().singleRequest(
      HttpRequest(
        method = HttpMethods.GET,
        uri = Uri(s"https://api.hh.ru/vacancies?text=!$searchVacancy&professional_role=124&area=$areaId&only_with_salary=true&page=$page&per_page=20"))
    ).flatMap { response =>
      Unmarshal(response.entity).to[JsObject]
        .map(_.fields.getOrElse("items", JsArray.empty))
        .map(json => json.convertTo[Iterable[OrganizationId]])
    }

    Await.result(result, Duration.Inf)
  }

  val graph = GraphDSL.create() { implicit b: GraphDSL.Builder[Any] =>
    import GraphDSL.Implicits._

    val source = Source.fromIterator[Int](() => iterator).map(searchByOrganisation).mapConcat(_.toList).map(_.id)

    def convertStage: Flow[String, VacancyInfo, NotUsed] = Flow.fromFunction[String, VacancyInfo](converter)

    def toStringStage: Flow[VacancyInfo, ByteString, NotUsed] = Flow.fromFunction[VacancyInfo, ByteString] { vacancyInfo =>

      counter += 1

      val result = Seq(

        counter,

        vacancyInfo.published_at.getOrElse(""),

        vacancyInfo.employer.flatMap(_.name).getOrElse(""),

        vacancyInfo.name.getOrElse(""),

        vacancyInfo.alternate_url.getOrElse(""),

        vacancyInfo.site.flatMap(_.name).getOrElse(""),

        printSalary(vacancyInfo.salary),

        vacancyInfo.key_skills.map(_.map(_.name)).map(_.mkString(", ")).getOrElse(""),

        vacancyInfo.area.flatMap(_.name).getOrElse(""),

        vacancyInfo.employment.flatMap(_.name).getOrElse(""),

        vacancyInfo.schedule.flatMap(_.name).getOrElse(""),

        vacancyInfo.experience.flatMap(_.name).getOrElse(""),

        printEnglish(vacancyInfo),

        searchVacancy,

        printLevel(vacancyInfo)

      ).mkString(separator)


      ByteString(result + "\n")
    }

    val sink = FileIO.toPath(new File(s"D:\\ParseHH\\$searchVacancy.txt").toPath)

    source ~> convertStage ~> toStringStage ~> sink
    ClosedShape
  }

  promise.success(800000)

  RunnableGraph
    .fromGraph(graph)
    .run()


  private def converter(id: String): VacancyInfo = {
    println("Текущий ID для обработки: " + id)
    val basicInformation = Http().singleRequest(
      HttpRequest(
        method = HttpMethods.GET,
        uri = Uri(s"https://api.hh.ru/vacancies/$id")
      )
    ).flatMap { response =>
      Unmarshal(response.entity).to[VacancyInfo]
    }

    Await.result(basicInformation, Duration.Inf)
  }

  def printSalary(salary: Option[Salary]): String = {
    salary.map(salary =>
      salary.from.getOrElse("")
        + separator + salary.to.getOrElse("")
        + separator + salary.currency.getOrElse("")
        + separator + salary.gross.map(gross => if (gross) "До вычета" else "После вычета").getOrElse(""))
      .getOrElse("")
  }

  def printLevel(vacancyInfo: VacancyInfo): String = {
    val middle = "Middle"
    val senior = "Senior"
    val junior = "Junior"
    val teamLead = "Lead"
    val vacancyName = vacancyInfo.name.getOrElse("")
    lazy val keySkills = vacancyInfo.key_skills.getOrElse(Iterable.empty[KeySkill])
    lazy val description = vacancyInfo.description.getOrElse("")
    val stringBuilder = new StringBuilder()


    if (vacancyName.contains(middle)) stringBuilder.append("Middle ")
    if (vacancyName.contains(senior)) stringBuilder.append("Senior ")
    if (vacancyName.contains(junior)) stringBuilder.append("Junior ")
    if (vacancyName.contains(teamLead)) stringBuilder.append("Team Lead ")

    if (stringBuilder.isEmpty) {
      if (keySkills.exists(_.name.contains(middle))) stringBuilder.append("Middle ")
      if (keySkills.exists(_.name.contains(senior))) stringBuilder.append("Senior ")
      if (keySkills.exists(_.name.contains(junior))) stringBuilder.append("Junior ")
      if (keySkills.exists(_.name.contains(teamLead))) stringBuilder.append("Team Lead ")
    }

    if (stringBuilder.isEmpty) {
      if (description.contains(middle)) stringBuilder.append("Middle ")
      if (description.contains(senior)) stringBuilder.append("Senior ")
      if (description.contains(junior)) stringBuilder.append("Junior ")
      if (description.contains(teamLead)) stringBuilder.append("Team Lead ")
    }


    stringBuilder.toString()
  }


  def printEnglish(vacancyInfo: VacancyInfo): String = {
    vacancyInfo.key_skills
      .flatMap(_.find(_.name.contains("Англ")))
      .map(_.name).getOrElse("")
  }
}
