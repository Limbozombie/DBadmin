package newgain.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 工具类
 */
public class Utility {
    
    //让工具类不可实例化
    private Utility() throws Exception {
        
        throw new Exception("Don’t  instantiate this class ");
    }
    
    /**
     * 获取连接
     */
    public static Connection getConn(String name , String password) throws Exception {
        //注册驱动
        Class.forName("com.mysql.jdbc.Driver");
        //创建连接
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/?useUnicode=true&characterEncoding=utf8" , name , password);
    }
    
    public static Connection getConn(String database , String name , String password) throws Exception {
        //注册驱动
        Class.forName("com.mysql.jdbc.Driver");
        //创建连接
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/" + database + "?useUnicode=true&characterEncoding=utf8" , name , password);
    }
    
    /**
     * 关闭资源
     */
    public static void close(ResultSet rs , Statement stat , Connection conn) {
        
        try {
            if (rs != null) {
                rs.close();
            }
            if (stat != null) {
                stat.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    
}
