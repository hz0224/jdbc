package jdbc.test

import java.io.{BufferedReader, File, FileReader}
import java.util.regex.Pattern

import scala.collection.mutable

/**
  * 测试Redash中使用最多的表
  */
object Test2 {

  val regex = "(\\b(pdw|ods|stage|app|bds|broker|dwd|ids|tag|temp|test)\\.\\w+\\b)"
  private val pattern = Pattern.compile(regex)

  def main(args: Array[String]): Unit = {

    val reader = new BufferedReader(new FileReader(new File("C:\\Users\\Administrator\\Desktop\\ETL依赖整理\\redash_new.csv")))
    var line = reader.readLine()
    val map = new mutable.HashMap[String,mutable.HashSet[String]]
    while(line != null && line.length > 0){
      val id = line.split(",")(0)
      val sql = line.split(",")(1)
      val tables = getTable(sql)
      if(tables.size > 0){
        for(table <- tables){
          if(map.contains(table)){
             map(table) += id
          }else{
             map.put(table,new  mutable.HashSet[String]())
             map(table) += id
          }
        }
      }
       line = reader.readLine()
    }

    val list = map.toList.sortWith(_._2.size > _._2.size)
    for((table,ids) <- list){
      println(table,ids)
    }
    reader.close()
  }


  //解析sql，得到所有表名
  def getTable(sql: String)={
    val matcher = pattern.matcher(sql)
    val tables = new mutable.HashSet[String]()
    while (matcher.find()){
       val table = matcher.group(1)
       tables.add(table)
    }
    tables
  }

}
