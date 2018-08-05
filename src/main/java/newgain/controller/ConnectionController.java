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
    public List<String> getConnection(String serverName , String userName , String password) {
        
        this.userName = userName;
        this.password = password;
        List<String> tablesList = new ArrayList<String>();
        tablesList.add(serverName);
        tablesList.add("web");
        try {
            con = Utility.getConn(userName , password);
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet tables = metaData.getTables(null , null , "%" , null);
            while (tables.next()) {
                tablesList.add(tables.getString("TABLE_NAME"));
            }
        } catch (Exception e) {
            //todo 尚未处理异常
            e.printStackTrace();
        } finally {
            Utility.close(rs , stat , con);
        }
        return tablesList;
    }
    
    @RequestMapping("retrieve/*")
    public Map<String, List<?>> retrieve(String tableName , String structure) {
        
        Map<String, List<?>> map = new HashMap<String, List<?>>();
        List<String> head = new ArrayList<String>();
        List data;
        String sql;
        try {
            con = Utility.getConn(userName , password);
            if ("true".equals(structure)) {
                sql = "DESC  " + tableName;
                //写死,减少消耗
                String[] headStr = {"Field" , "Type" , "Null" , "Key" , "Default" , "Extra"};
                Collections.addAll(head , headStr);
            } else {
                sql = "SELECT * FROM  " + tableName;
                DatabaseMetaData dm = con.getMetaData();
                ResultSet colRet = dm.getColumns(null , "%" , tableName , "%");
                while (colRet.next()) {
                    head.add(colRet.getString("COLUMN_NAME"));
                }
            }
            data = new QueryRunner().query(con , sql , new ArrayListHandler());
            map.put("head" , head);
            map.put("data" , data);
        } catch (Exception e) {
            //todo 尚未处理异常
            e.printStackTrace();
        } finally {
            Utility.close(rs , stat , con);
        }
        return map;
    }
    
    @RequestMapping("query")
    public Map<String, Object> query(String sqlStatement) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> head = new ArrayList<String>();
        try {
            con = Utility.getConn(userName , password);
            ResultSetMetaData rsMetaData = con.prepareStatement(sqlStatement).executeQuery(sqlStatement).getMetaData();
            for (int i = 1 ; i <= rsMetaData.getColumnCount() ; i++) {
                String columnName = rsMetaData.getColumnName(i);
                head.add(columnName);
            }
            List data = new QueryRunner().query(con , sqlStatement , new ArrayListHandler());
            map.put("status" , "ok");
            map.put("head" , head);
            map.put("data" , data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            List<String> data = new ArrayList<String>();
            head.add("ERROR");
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
