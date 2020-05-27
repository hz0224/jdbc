package jdbc.util;

import java.sql.*;

public class Utils {

    //获取连接
    public static Connection getConnection(){
        //1 连接信息
        String user = "root";
        String passwd = "17746371311";
        String url = "jdbc:mysql://39.105.49.35:3306/test";
        String driverName = "com.mysql.jdbc.Driver";

        Connection connection = null;
        try {
            // 2 加载驱动
            Class.forName(driverName);
            //3 获取连接
            connection = DriverManager.getConnection(url, user, passwd);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }


    //关闭连接
    public static void release(Connection connection, PreparedStatement ps, ResultSet rs){
        try{
            if(connection != null) connection.close();
            if(ps != null) ps.close();
            if(rs != null) rs.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
