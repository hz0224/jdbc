package jdbc.query;

import jdbc.bean.Stu;
import jdbc.util.Utils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liu Hangzhou on 2020/05/26
 * desc: 查询方法
 */

public class Query {

    public static void main(String[] args) throws SQLException {

        //一般查询方法
        String sql = "select * from stu where id > ?";
        List<Stu> list = query(sql, 115);
        for (Stu stu : list) {
            System.out.println(stu);
        }



    }


    //一般查询方法封装
    public static List<Stu> query(String sql, Object ...args) throws SQLException {
        //1 获取连接
        Connection connection = Utils.getConnection();

        //2 预编译sql语句，获取到PreparedStatement对象
        PreparedStatement ps = connection.prepareStatement(sql);
        for(int i = 0 ; i<args.length ;i++){
            ps.setObject(i+1,args[i]);
        }

        //3 查询
        ResultSet resultSet = ps.executeQuery();

        //4 解析结果集
        ArrayList<Stu> stus = new ArrayList<Stu>();
        //next:判断结果集的下一条是否有数据，如果有数据返回true，游标下移，没有数据返回false，游标不会下移.
        //next方法相当于集合迭代器的hasNext()方法和next方法的结合体，不过这个next方法并没有集合next()方法取出当前游标指向的元素的功能。
        while (resultSet.next()){
            //获取当前这条数据的各个字段值        此时resultSet对象就代表这一行数据
            String id = resultSet.getString("id");
            String name = resultSet.getString("name");
            String age = resultSet.getString("age");
            Boolean is_deleted = resultSet.getBoolean("is_deleted");
            //经验：
            //无论mysql中字段类型是什么都可以使用 getString方法获取.
            //这里建议除bit类型使用getBoolean方法获取，其他的所有类型都使用getString方法。
            //只所以像mysql中int类型不使用 getInt方法获取的原因是 如果mysql中int字段为null，getInt方法获取到的是0,
            //很多时候我们不需要这样的效果。
            //因为都使用getString方法获取了，所以定义的实体类中相应的字段也应该用String类型。如果用Integer类型定义的话
            //如果getString方法获取到的是null，那么用Integer.valueOf将null转换为Integer时会报错。
            Stu stu = new Stu(id,name,age,is_deleted);
            stus.add(stu);
        }

        //5 关闭连接
        Utils.release(connection,ps,resultSet);
        return stus;
    }




}
