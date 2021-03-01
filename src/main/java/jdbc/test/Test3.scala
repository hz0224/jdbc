package jdbc.test

import jdbc.query.TaskDependencyStat

import scala.collection.JavaConversions._

object Test3 {

  def main(args: Array[String]): Unit = {

    val stat = new TaskDependencyStat()
    val hashMap = stat.statKeyTask()
    val result = hashMap.toList.sortWith(_._2 > _._2).take(20)

    for((k,v) <- result){
      println(k,v)
    }


  }
}
