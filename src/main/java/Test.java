import sun.applet.Main;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Test {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class<?> aClass = Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/redash_new", "redash", "redash");
        System.out.println(connection);
    }
}
