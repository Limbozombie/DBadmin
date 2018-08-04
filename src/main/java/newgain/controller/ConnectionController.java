package newgain.controller;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import newgain.dao.Utility;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ConnectionController {
    
    private String userName;
    private String password;
    private PreparedStatement stat = null;
    private ResultSet rs = null;
    private Connection con = null;
    
    @RequestMapping("connection")
    @ResponseBody
    public List<String> getConnection(String serverName , String userName , String password) {
        
        this.userName = userName;
        this.password = password;
        List<String> tablesList = new ArrayList<String>();
        tablesList.add(serverName);
        tablesList.add("web");
        try {
            con = Utility.getConn(userName , password);
            //获得数据库的所有表名
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet tables = metaData.getTables(null , null , "%" , null);
            while (tables.next()) {
                tablesList.add(tables.getString("TABLE_NAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utility.close(rs , stat , con);
        }
        return tablesList;
    }
    
    @RequestMapping("retrieve/*")
    @ResponseBody
    public Map<String, List<?>> retrieve(String tableName , String structure) {
        
        Map<String, List<?>> map = new HashMap<String, List<?>>();
        List<String> head = new ArrayList<String>();
        List data;
        String sql;
        try {
            con = Utility.getConn(userName , password);
            if ("true".equals(structure)) {
                sql = "DESC  " + tableName;
                String[] headStr = {"Field" , "Type" , "Null" , "Key" , "Default" , "Extra"};
                Collections.addAll(head , headStr);
            } else {
                DatabaseMetaData dm = con.getMetaData();
                ResultSet colRet = dm.getColumns(null , "%" , tableName , "%");
                while (colRet.next()) {
                    head.add(colRet.getString("COLUMN_NAME"));
                }
                sql = "SELECT * FROM  " + tableName;
            }
            data = new QueryRunner().query(con , sql , new ArrayListHandler());
            map.put("head" , head);
            map.put("data" , data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utility.close(rs , stat , con);
        }
        return map;
    }
    
}
