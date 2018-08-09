package newgain.controller;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import newgain.dao.Utility;
import newgain.entity.grid.Grid;
import newgain.entity.grid.Head;
import newgain.entity.grid.Rows;
import newgain.entity.tree.Icons;
import newgain.entity.tree.TreeDB;
import newgain.entity.tree.TreeServer;
import newgain.entity.tree.TreeTable;
import org.apache.commons.dbutils.QueryRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import sun.awt.image.DataBufferNative;

@RestController
public class ConnectionController {
    
    private String userName;
    private String password;
    private PreparedStatement stat = null;
    private ResultSet rs = null;
    private Connection con = null;
    
    @RequestMapping("connection")
    public Map getConnection(String serverName , String userName , String password) {
        
        this.userName = userName;
        this.password = password;
        Map<String, Object> map = new HashMap<String, Object>();
        List<TreeServer> treeList = new ArrayList<TreeServer>();
        try {
            //获得 基础 连接
            con = Utility.getConn(userName , password);
            String sql = "SHOW DATABASES";
            //查询所有数据库名
            ResultSet rs = con.prepareStatement(sql).executeQuery(sql);
            List<TreeDB> dbList = new ArrayList<TreeDB>();
            while (rs.next()) {
                String dbName = rs.getString(1);
                //获得对应数据库名的连接
                con = Utility.getConn(dbName , userName , password);
                ResultSet tables = con.getMetaData().getTables(null , null , "%" , null);
                List<TreeTable> tableList = new ArrayList<TreeTable>();
                while (tables.next()) {
                    //获得对应数据库名中的所有表名
                    String tableName = tables.getString("TABLE_NAME");
                    TreeTable treeTable = new TreeTable(serverName + "|" + dbName + "|" + tableName , tableName , new Icons("table"));
                    //创建TreeTable对象,添加至tableList中
                    tableList.add(treeTable);
                }
                TreeDB treeDB = new TreeDB(serverName + "|" + dbName , dbName , new Icons("database") , tableList);
                //创建TreeDB对象,添加至dbList中
                dbList.add(treeDB);
            }
            TreeServer treeServer = new TreeServer(serverName , serverName , new Icons("server") , dbList);
            //创建TreeServer对象,添加到treeList中
            treeList.add(treeServer);
            map.put("status" , "ok");
            map.put("data" , treeList);
        } catch (Exception e) {
            map.put("status" , "error");
            map.put("data" , e.getMessage());
        } finally {
            Utility.close(rs , stat , con);
        }
        return map;
    }
    
    @RequestMapping("retrieve")
    public Grid retrieve(String DBName , String tableName , String structure) {
        
        String[] str = {DBName , tableName , structure};
        return getMap(str);
    }
    
    @RequestMapping("query")
    public Grid query(String DBName , String sqlStatement) {
        
        String[] str = {DBName , sqlStatement};
        return getMap(str);
    }
    
    @RequestMapping("deleteRow")
    public Map deleteRow(String DBName , String tableName , String colName , String value) {
        
        Map<String, String> map = new HashMap<String, String>();
        try {
            con = Utility.getConn(DBName , userName , password);
            String sql = "DELETE FROM " + tableName + " WHERE " + colName + "= " + value;
            boolean b = con.prepareStatement(sql).execute(sql);
            //            if (b) {
            map.put("status" , "ok");
            //            } else {
            //                map.put("status" , "error");
            //                map.put("message" , "Inner Exception ,Contact DB Administrator");
            //            }
        } catch (Exception e) {
            map.put("status" , "error");
            map.put("message" , e.getMessage());
        }
        return map;
    }
    
    private Grid getMap(String[] str) {
        
        List<Head> headList = new ArrayList<Head>();
        List<Rows> rowsList = new ArrayList<Rows>();
        String sql = "";
        String DBName = str[0];
        String tableName;
        String structure;
        switch (str.length) {
            case 2:
                sql = str[1];
                break;
            case 3:
                tableName = str[1];
                structure = str[2];
                if ("true".equals(structure)) {
                    sql = "DESC  " + tableName;
                } else {
                    sql = "SELECT * FROM  " + tableName;
                }
                break;
        }
        try {
            con = Utility.getConn(DBName , userName , password);
            ResultSet rs = con.prepareStatement(sql).executeQuery(sql);
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columnCount = rsMetaData.getColumnCount();
            int id = 0;
            //从上到下 , 从左到右
            while (rs.next()) {
                List<Object> record = new ArrayList<Object>();
                for (int i = 1 ; i <= columnCount ; i++) {
                    record.add(rs.getObject(i));
                }
                rowsList.add(new Rows(++ id , record , "color:black"));
            }
            for (int i = 1 ; i <= columnCount ; i++) {
                String columnName = rsMetaData.getColumnName(i);
                headList.add(new Head(200 , "ro" , "left" , columnName));
            }
        } catch (Exception e) {
            //清空head 和data
            headList.clear();
            rowsList.clear();
            
            headList.add(new Head(500 , "ro" , "left" , "Error"));
            List<String> errMsg = new ArrayList<String>();
            errMsg.add(e.getMessage());
            Rows rows = new Rows(0 , errMsg , "color:red");
            rowsList.add(rows);
        } finally {
            Utility.close(rs , stat , con);
        }
        return new Grid(headList , rowsList);
    }
}
