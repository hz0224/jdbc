package jdbc.connection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class GetConnection {

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException, IOException {
//       getConnection5();


    }

    //理解原理
    public static Connection getConnection1() throws SQLException {
        //1 提供 java.sql.Driver接口实现类的对象
        Driver driver = null;
        driver = new com.mysql.jdbc.Driver();

        //2 url
        String url = "jdbc:mysql://39.105.49.35:3306/ods";

        //3 提供Properties对象，指明用户名和密码
        Properties pros = new Properties();
        pros.setProperty("user","root");
        pros.setProperty("password","17746371311");

        //4 调用driver对象的connect方法获取连接
        Connection connection = driver.connect(url, pros);

        //System.out.println(connection);
        return connection;
    }

    public static void getConnection2() throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        //1 创建 Driver实现类的对象,通过方式的方式
        String className = "com.mysql.jdbc.Driver";
        Class<?> clazz = Class.forName(className);
        Driver driver = (Driver) clazz.newInstance();

        //2 url
        String url = "jdbc:mysql://hangzhou2:3306/test";

        //3 提供Properties对象，指明用户名和密码
        Properties pros = new Properties();
        pros.setProperty("user","root");
        pros.setProperty("password","123456");

        //4 调用driver对象的connect方法获取连接
        Connection connection = driver.connect(url, pros);

        System.out.println(connection);
    }


    public static void getConnection3() throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {

        //1 数据库连接的4个基本要素
        String url = "jdbc:mysql://hangzhou2:3306/test";
        String driverName = "com.mysql.jdbc.Driver";
        String user = "root";
        String password = "123456";

        //2 实例化 Driver(驱动)
        Class<?> clazz = Class.forName(driverName);
        Driver driver = (Driver) clazz.newInstance();

        //3 注册驱动
        DriverManager.registerDriver(driver);

        //4 获取连接
        Connection connection = DriverManager.getConnection(url, user, password);

        System.out.println(connection);
    }

    public static void getConnection4() throws ClassNotFoundException, SQLException {
        //1 数据库连接的4个基本要素
        String url = "jdbc:mysql://hangzhou2:3306/test";
        String driverName = "com.mysql.jdbc.Driver";
        String user = "root";
        String password = "123456";

        //2 加载驱动 (此时会发生类加载,然后 ①实例化Driver ②注册驱动)
        Class.forName(driverName);

        //3 获取连接
        Connection connection = DriverManager.getConnection(url, user, password);

        System.out.println(connection);
    }


    public static void getConnection5() throws IOException, ClassNotFoundException, SQLException {

        //1 加载配置文件
        Properties pros = new Properties();
        InputStream in = GetConnection.class.getClassLoader().getResourceAsStream("jdbc.properties");
        pros.load(in);

        //2 读取配置信息
        String url = pros.getProperty("url");
        String driverName = pros.getProperty("driverName");
        String user = pros.getProperty("user");
        String password = pros.getProperty("password");

        //3 加载驱动
        Class.forName(driverName);

        //4 获取连接
        Connection connection = DriverManager.getConnection(url, user, password);

        System.out.println(connection);
    }
}
