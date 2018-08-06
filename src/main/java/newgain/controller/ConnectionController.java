package newgain.controller;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import newgain.dao.Utility;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConnectionController {
    
    private String userName;
    private String password;
    private PreparedStatement stat = null;
    private ResultSet rs = null;
    private Connection con = null;
    
    @RequestMapping("connection")
    public Map<String, Object> getConnection(String serverName , String userName , String password) {
        
        this.userName = userName;
        this.password = password;
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> data = new ArrayList<String>();
        data.add(serverName);
        data.add("web");
        try {
            con = Utility.getConn(userName , password);
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet tables = metaData.getTables(null , null , "%" , null);
            while (tables.next()) {
                data.add(tables.getString("TABLE_NAME"));
            }
            map.put("status" , "ok");
            map.put("tableList" , data);
        } catch (Exception e) {
            map.put("status" , "error");
            data.clear();
            data.add("Connection Error,Try Again");
            map.put("tableList" , data);
            //            e.printStackTrace();
        } finally {
            Utility.close(rs , stat , con);
        }
        return map;
    }
    
    @RequestMapping("retrieve")
    public Map<String, Object> retrieve(String tableName , String structure) {
        
        String[] str = {tableName , structure};
        return getMap(str);
    }
    
    @RequestMapping("query")
    public Map<String, Object> query(String sqlStatement) {
        
        String[] str = {sqlStatement};
        return getMap(str);
    }
    
    private Map<String, Object> getMap(String[] str) {
        
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> head = new ArrayList<String>();
        List<Object> data = new ArrayList<Object>();
        String sql = "";
        switch (str.length) {
            case 1:
                sql = str[0];
                break;
            case 2:
                String tableName = str[0];
                String structure = str[1];
                if ("true".equals(structure)) {
                    sql = "DESC  " + tableName;
                } else {
                    sql = "SELECT * FROM  " + tableName;
                }
                break;
        }
        try {
            con = Utility.getConn(userName , password);
            ResultSet rs = con.prepareStatement(sql).executeQuery(sql);
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columnCount = rsMetaData.getColumnCount();
            //从上到下 , 从左到右
            while (rs.next()) {
                List<Object> record = new ArrayList<Object>();
                for (int i = 1 ; i <= columnCount ; i++) {
                    record.add(rs.getObject(i));
                }
                data.add(record);
            }
            for (int i = 1 ; i <= columnCount ; i++) {
                String columnName = rsMetaData.getColumnName(i);
                head.add(columnName);
            }
            map.put("status" , "ok");
            map.put("head" , head);
            map.put("data" , data);
        } catch (Exception e) {
            //清空head 和data
            //            鬼知道try里面的异常出现在哪行
            head.clear();
            data.clear();
            
            head.add("Error");
            data.add(e.getMessage());
            map.put("status" , "error");
            map.put("head" , head);
            map.put("data" , data);
        } finally {
            Utility.close(rs , stat , con);
        }
        return map;
    }
}
