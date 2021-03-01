package jdbc.test;

import javax.xml.bind.SchemaOutputResolver;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 读取redash.csv中的sql判断是否使用到了指定的表.
 */
public class Test1 {
    public static void main(String[] args) throws IOException {

        BufferedReader reader1 = new BufferedReader(new FileReader(new File("C:\\Users\\Administrator\\Desktop\\table_name.txt")));
        String table = reader1.readLine();
        HashSet<String> tables = new HashSet<String>();
        while (table != null && table.length() > 0){
            tables.add(table.trim());
            table = reader1.readLine();
        }
        BufferedReader reader2 = new BufferedReader(new FileReader(new File("C:\\Users\\Administrator\\Desktop\\redash_sql.csv")));
        String line = reader2.readLine();
        List<String> sql = new ArrayList<String>();
        for(int i = 0; i <= 122822 ; i++){
            sql.add(line);
            line = reader2.readLine();
        }

        for (String name : tables) {
            boolean flag = true;
            for (String s : sql) {
                if(s == null || s.length() == 0)continue;
                if(s.contains(name)) {
                    flag = false;
                    break;
                }
            }
            if(flag) System.out.println(name);
        }


        reader1.close();
        reader2.close();
    }
}
