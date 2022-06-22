import JsonType.{Employer, Employment, Site}

object JsonType {

  /**
   *
   * @param id
   */
  case class OrganizationId(id: String) // ID организаций

  /**
   * Блок вакансии, является полноценным <u>json</u>, <br>
   * который содержит в себе остальные блоки.
   *
   * @param published_at  дата публикации вакансии
   * @param name          название вакансии
   * @param alternate_url ссылка на вакансию
   * @param salary        [[Salary]]
   * @param key_skills    [[KeySkill]]
   * @param Area          [[Area]]
   * @param Schedule      [[Schedule]]
   * @param Experience    [[Experience]]
   * @param Employer      [[Employer]]
   * @param Employment    [[Employment]]
   * @param Site          [[Site]]
   */
  case class VacancyInfo(
                          published_at: Option[String],
                          name: Option[String],
                          alternate_url: Option[String],
                          description: Option[String],
                          salary: Option[Salary],
                          key_skills: Option[Iterable[KeySkill]],
                          area: Option[Area],
                          schedule: Option[Schedule],
                          experience: Option[Experience],
                          employer: Option[Employer],
                          employment: Option[Employment],
                          site: Option[Site]
                        )

  /**
   * Блок зарплаты, <u>object или null</u>.
   *
   * @param from     от скольки
   * @param to       до скольки
   * @param currency валюта
   * @param gross    до вычета налога
   */
  case class Salary(
                     from: Option[Int],
                     to: Option[Int],
                     currency: Option[String],
                     gross: Option[Boolean]
                   )

  /**
   * Короткое представление работодателя. <br>
   * Может не прийти в случае, если вакансия анонимная
   * @param name Наименование работодателя
   */
  case class Employer(name: Option[String])

  /**
   * Ключевые навыков указанные в вакансии.
   * @param name Наименование навыка
   */
  case class KeySkill(name: String) // Временная зона

  /**
   * Регион размещения вакансии
   * @param id Идентификатор региона
   * @param name Название региона
   * @param url Url получения информации о регионе
   */
  case class Area(id: Option[String], name: Option[String], url: Option[String]) // Временная зона

  /**
   * Тип занятости.
   * @param id Идентификатор типа занятости
   * @param name Название типа занятости
   */
  case class Employment(id: Option[String], name: Option[String])

  /**
   * График работы.
   * @param id Идентификатор графика работы
   * @param name 	Название графика работы
   */
  case class Schedule(id: Option[String], name: Option[String])

  /**
   * Требуемый опыт работы.
   * @param id Идентификатор требуемого опыта работы
   * @param name Название требуемого опыта работы
   */
  case class Experience(id: Option[String], name: Option[String])

  /**
   * Площадка размещения вакансии, в данном случае только для hh. <br>
   * Возможно планируют интеграцию, впилил ради интереса.
   * @param id Идентификатор площадки
   * @param name Название площадки (сокращённо hh)
   */
  case class Site(id: Option[String], name: Option[String])
}