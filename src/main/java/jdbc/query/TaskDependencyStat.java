package jdbc.query;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Entity;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.util.Pair;
import sun.security.provider.certpath.Vertex;

import java.io.*;
import java.net.Inet4Address;
import java.util.*;

/**
 * Created by Liu HangZhou on 2020/12/02
 * desc: 统计azkaban任务依赖
 */
public class TaskDependencyStat {
    private String uri = "bolt://47.101.40.48:7687";    //登录neo4j web界面时上面显示的地址.
    private String path = "C:\\Users\\Administrator\\Desktop\\tasks.txt";
    Driver driver = null;
    Session session = null;

    public TaskDependencyStat() {
        driver = GraphDatabase.driver(uri, AuthTokens.basic("neo4j", "mJb2K9dyjbAu3yRtvKSek7YdMirLDJ"));
        session = driver.session();
    }

    public static void main(String[] args) throws IOException {
        TaskDependencyStat taskDependencyStat = new TaskDependencyStat();

        HashSet<String> set = new HashSet<String>();
        for (String s : taskDependencyStat.getTasks()) {r
             if(s.contains("car") && !s.contains("extract_")) {
                List<Record> list = taskDependencyStat.getResult1(s);
                if(list.size() == 1) {
                    //boolean flag = list.get(0).get("p").asPath().end().get("name").asString().equals("ids_end_flow");
                    //if(flag) set.add(s);
                    set.add(s);
                }
             }
        }

        for (String s : set) {
            System.out.println(s);
        }

        taskDependencyStat.session.close();
        taskDependencyStat.driver.close();
    }


    //统计ETL重要任务
    public  HashMap<String,Integer> statKeyTask() throws IOException {

        HashMap<String, Integer> map = new HashMap<String, Integer>();
        //获取所有任务
        HashSet<String> tasks = getTasks();
        for (String task : tasks) {
            int dependencys = getResult1(task).size();
            map.put(task,dependencys);
        }
        return map;
    }


    //根据任务名拼出表名
    //hive_ods_activity_account_course          ods.ods_activity_account_course
    public String getTableName(String task){
        String tableName = null;
        if(task.startsWith("broker_")){
            tableName = "broker." + task;
        }else if(task.startsWith("hive_ods")){
            tableName = "ods." + task.substring(5);
        }else {
            tableName = "错误" + task;
        }
        return tableName;
    }


    //获取数仓这边没有使用到的最外层的任务
    public HashSet<String> get1() throws IOException {
        HashSet<String> tasks = getTasks();
        HashSet<String> result = new HashSet<String>();
        for (String task : tasks) {
            int size1 = getResult1(task).size();
            int size2 = getResult2(task).size();
            boolean flag1 = false;
            //一个record就是一个返回结果，就是在 cypher里写的 return，return 是可以返回多个值的，通过 record.get()可以获取到指定的结果.
            for (Record record : getResult2(task)) {
                //一个path代表由查询节点发出的其中一条路线，这条路线上包含有多个节点.
                flag1 = record.get("p").asPath().end().get("name").asString().startsWith("extract_");
            }
            boolean flag2 = false;
            for (Record record : getResult1(task)) {
                String name = record.get("p").asPath().end().get("name").asString();
                flag2 = name.equals("hive_ods_flow") || name.equals("broker_flow");
            }

            if(size1 == 1 && size2 == 1 && flag1 && flag2){
                result.add(task);
            }
        }

        return result;
    }



    //该任务指向别的任务,获取查询结果
    public List<Record> getResult1(String taskName){
        StatementResult result = session.run("match p = (n:tasks{name:$taskName}) -->() return p", Values.parameters("taskName", taskName));
        return result.list();
    }

    //别的任务指向该任务,获取查询结果
    public List<Record> getResult2(String taskName){
        StatementResult result = session.run("match p = (n:tasks{name:$taskName}) <--() return p", Values.parameters("taskName", taskName));
        return result.list();
    }

    //获取所有的任务
    public HashSet<String> getTasks() throws IOException {
        HashMap<String, String> allTasks = this.getAllTasks(this.path);
        //获取所有的任务
        HashSet<String> set = new HashSet<String>();
        for (Map.Entry<String, String> entry : allTasks.entrySet()) {
            String key = entry.getKey();
            set.add(key);
            String value = entry.getValue();
            if(value.length() > 0){
                for (String task : value.split(",")) {
                    set.add(task);
                }
            }
        }//for

        return set;
    }

    //创建节点和关系
    public void createNodeAndRelation() throws IOException {
        HashMap<String, String> allTasks = this.getAllTasks(this.path);
        //获取所有的任务
        HashSet<String> set = this.getTasks();
        //创建节点
        for (String task : set) {
            this.createNode(task);
        }
        //创建关系
        for (Map.Entry<String, String> entry : allTasks.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if(value.length() == 0) continue;

            for (String task : value.split(",")) {
                this.createRelation(task,key);
            }
        }
    }
    public HashMap<String, String> getAllTasks(String path) throws IOException {
        File file = new File(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf-8"));
        String line = reader.readLine();
        StringBuilder result = new StringBuilder();
        while (line != null && line.length() > 0){
            result.append(line);
            line = reader.readLine();
        }
        JSONArray nodes = JSONObject.parseObject(result.toString()).getJSONArray("nodes");
        HashMap<String, String> sourceMap = new HashMap<String,String>();
        for (Object node : nodes) {
            JSONObject jsonNode = (JSONObject) node;
            String taskName = jsonNode.getString("id");
            StringBuilder tasks = new StringBuilder("");
            if(jsonNode.getJSONArray("in") != null){
                JSONArray array = jsonNode.getJSONArray("in");
                for(int i = 0 ; i< array.size() ; i++){
                    String task = (String) array.get(i);
                    tasks.append(task);
                    if(i != array.size() -1 ) {
                        tasks.append(",");
                    }
                }
            }
            sourceMap.put(taskName,tasks.toString());
        }
        return sourceMap;
    }
    //删除一个标签下所有的节点，需要先删除所有的关系
    public void deleteAllNode(){
        String cypher = "match (n:tasks) delete n";
        session.run(cypher);
    }
    //删除指定的关系，针对所有节点
    public void deleteAllRelation(){
        String cypher = "match p=()-[r:依赖]->() delete r";
        session.run(cypher);
    }
    //创建节点
    public  void createNode(String name){
        String cypher = "create (:tasks {name:$name})";
        session.run(cypher,Values.parameters("name",name));
    }
    //创建两个节点之间的关系
    public void createRelation(String node1,String node2){
        String cypher = "match (a:tasks{name:$firstName}) match (b:tasks{name:$secondName}) create (a)-[r:依赖] ->(b) return r";
        session.run(cypher, Values.parameters("firstName",node1,"secondName",node2));
    }
    public void test(){
        //创建节点  ()就代表了一个节点，()里使用{}这种json的方式可写多个属性. ()代表节点,{}代表属性,[]代表关系，关系也可以带属性
//        session.run("create (:tasks {name:$name})",Values.parameters("name","hive_ods_activity_template_info"));
//
//        //为已经创建的节点添加关系
//        session.run("match (a:tasks{name:$firstName}) match (b:tasks{name:$secondName}) create (a)-[r:依赖] ->(b) return r",
//                Values.parameters("firstName","hive_ods_activity_template_info","secondName","hive_ods_activity_invite"));

        //同时创建节点和关系
//        session.run("create (:tasks{name:$task1}) -[:依赖]-> (:tasks{name:$task2})",
//                Values.parameters("task1","spark_monitor_umeng_info","task2","hive_t_crawler_umeng_info"));

        //修改属性
//        session.run("match (n:tasks{name:$oldName}) set n.name = $newName",
//                Values.parameters("oldName","spark_monitor_umeng_info","newName","hive_ods_new_analysis_logstore_test"));

        //删除一个标签下所有节点,标签是不用删的，标签依附于节点存在，如果这个标签下的所有节点都没有了，那么这个标签也就不存在了。创建节点时如果标签不存在会自动创建
        //节点携带关系是无法删除的，必须先删除所有关系才能删除所有节点.
        //session.run("match (n:tasks) delete n");

        //删除所有关系
        //session.run("match p=()-[r:依赖]->() delete r");

        //删除指定的节点，需要先删除该节点所有的关系
//        session.run("match (n:tasks{name:$name}) delete n",Values.parameters("name","spark_monitor_umeng_info"));
        session.close();
        this.driver.close();
    }

}
