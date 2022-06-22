import JsonType._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, DeserializationException, JsArray, JsBoolean, JsNull, JsNumber, JsString, JsValue, RootJsonFormat}


/**
 * Данный трейт для конвертации данных из Json -> case class.<br>
 */
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val organizationIdFormat: RootJsonFormat[OrganizationId] = jsonFormat1(OrganizationId)
  implicit val experienceFormat: RootJsonFormat[Experience] = jsonFormat2(Experience)
  implicit val employmentFormat: RootJsonFormat[Employment] = jsonFormat2(Employment)
  implicit val keySkillsFormat: RootJsonFormat[KeySkill] = jsonFormat1(KeySkill)
  implicit val scheduleFormat: RootJsonFormat[Schedule] = jsonFormat2(Schedule)
  implicit val employerFormat: RootJsonFormat[Employer] = jsonFormat1(Employer)
  implicit val salaryFormat: RootJsonFormat[Salary] = jsonFormat4(Salary)
  implicit val areaFormat: RootJsonFormat[Area] = jsonFormat3(Area)
  implicit val siteFormat: RootJsonFormat[Site] = jsonFormat2(Site)
  implicit val vacancyInfoFormat: RootJsonFormat[VacancyInfo] = jsonFormat12(VacancyInfo)



  class EnumJsonConverter[T <: scala.Enumeration](enu: T) extends RootJsonFormat[T#Value] {
    override def write(obj: T#Value): JsValue = JsString(obj.toString)

    override def read(json: JsValue): T#Value = {
      json match {
        case JsNull => enu.withName("")
        case JsString(txt) => enu.withName(txt)
        case somethingElse => throw DeserializationException(s"Expected a value from enum $enu instead of $somethingElse")
      }
    }
  }
}