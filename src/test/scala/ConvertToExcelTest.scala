import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import java.io.{BufferedReader, File, FileOutputStream, FileReader}
import scala.collection.mutable.ListBuffer

object ConvertToExcelTest extends App{

  val file = new File("D:\\ParseHH\\QA_Таганрог.xlsx")
  val outStream = new FileOutputStream(file)
  val workbook = new XSSFWorkbook()
  val sheet = workbook.createSheet()
  var count = 0
  var listBuffer  = new ListBuffer[Array[String]].empty
  val reader = new BufferedReader(new FileReader("D:\\ParseHH\\QA.txt"))
  var row = sheet.createRow(count)
  var cell = row.createCell(0, CellType.STRING)
  cell.setCellValue("Дата публикации")
  cell = row.createCell(1, CellType.STRING)
  cell.setCellValue("Название компании")
  cell = row.createCell(2, CellType.STRING)
  cell.setCellValue("Название вакансии")
  cell = row.createCell(3, CellType.STRING)
  cell.setCellValue("Ссылка")
  cell = row.createCell(4, CellType.STRING)
  cell.setCellValue("Источник")
  cell = row.createCell(5, CellType.STRING)
  cell.setCellValue("Зарплата от")
  cell = row.createCell(6, CellType.STRING)
  cell.setCellValue("Зарплата до")
  cell = row.createCell(7, CellType.STRING)
  cell.setCellValue("Валюта")
  cell = row.createCell(8, CellType.STRING)
  cell.setCellValue("До или после вычета налогов")
  cell = row.createCell(9, CellType.STRING)
  cell.setCellValue("Ключевые навыки")
  cell = row.createCell(10, CellType.STRING)
  cell.setCellValue("Регион")
  cell = row.createCell(11, CellType.STRING)
  cell.setCellValue("Занятость")
  cell = row.createCell(12, CellType.STRING)
  cell.setCellValue("График работы")
  cell = row.createCell(13, CellType.STRING)
  cell.setCellValue("Требуемый опыт")
  cell = row.createCell(14, CellType.STRING)
  cell.setCellValue("Английский язык")
  cell = row.createCell(15, CellType.STRING)
  cell.setCellValue("Направление")
  cell = row.createCell(16, CellType.STRING)
  cell.setCellValue("Уровень")


  var line = reader.readLine()

  while (line != null) {
    listBuffer.addOne(line.split("!!!").drop(1))
    line = reader.readLine()
  }



  listBuffer.foreach { arr =>
    count += 1
    row = sheet.createRow(count)
    for (i <- arr.indices) {
      cell = row.createCell(i, CellType.STRING)
      cell.setCellValue(changeDateCell(arr, i))
    }
  }


  workbook.write(outStream)


  def changeDateCell(arr: Array[String], i: Int): String = {
    if (i == 0) arr(i).substring(0, arr(i).indexOf("T")) else arr(i)
  }
}
