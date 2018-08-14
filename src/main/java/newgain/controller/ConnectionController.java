package newgain.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import newgain.dao.Utility;
import newgain.entity.Grid;
import newgain.entity.Grid.Head;
import newgain.entity.Grid.Rows;
import newgain.entity.Icons;
import newgain.entity.TreeView;
import newgain.entity.TreeView.TreeDB;
import newgain.entity.TreeView.TreeDB.TreeTable;
import org.springframework.http.converter.json.GsonBuilderUtils;
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
    public Map getConnection(String serverName , String userName , String password) {
        
        this.userName = userName;
        this.password = password;
        Map<String, Object> result = new HashMap<String, Object>();
        List<TreeView> treeViewList = new ArrayList<TreeView>();
        try {
            //获得 基础 连接
            con = Utility.getConn(userName , password);
            String sql = "SHOW DATABASES";
            //查询所有数据库名
            ResultSet databasesNames = con.prepareStatement(sql).executeQuery(sql);
            List<TreeDB> dbList = new ArrayList<TreeDB>();
            while (databasesNames.next()) {
                String dbName = databasesNames.getString(1);
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
            TreeView treeView = new TreeView(serverName , serverName , new Icons("server") , dbList);
            //创建TreeServer对象,添加到treeList中
            treeViewList.add(treeView);
            result.put("status" , "ok");
            result.put("data" , treeViewList);
        } catch (Exception e) {
            result.put("status" , "error");
            result.put("data" , e.getMessage());
        } finally {
            Utility.close(rs , stat , con);
        }
        return result;
    }
    
    @RequestMapping("retrieve")
    public Grid retrieve(String DBName , String tableName , String structure) {
        
        String[] parameters = {DBName , tableName , structure};
        
        return getGrid(parameters);
    }
    
    @RequestMapping("query")
    public Grid queryBySQL(String DBName , String sqlStatement) {
        
        String[] parameters = {DBName , sqlStatement};
        return getGrid(parameters);
    }
    
    @RequestMapping("deleteRow")
    public Map deleteRow(String DBName , String tableName , String colName , String value) {
        
        Map<String, String> result = new HashMap<String, String>();
        try {
            con = Utility.getConn(DBName , userName , password);
            String sql = "DELETE FROM " + tableName + " WHERE " + colName + "= " + value;
            con.prepareStatement(sql).execute(sql);
            result.put("status" , "ok");
        } catch (Exception e) {
            result.put("status" , "error");
            result.put("message" , e.getMessage());
        } finally {
            Utility.close(rs , stat , con);
        }
        return result;
    }
    
    @RequestMapping("insertRow")
    public Map insertRow(String DBName , String tableName , String values) {
        
        Map<String, String> result = new HashMap<String, String>();
        ObjectMapper mapper = new ObjectMapper();
        List<String> colValues = new ArrayList<String>();
        try {
            Map<String, String> formData = mapper.convertValue(mapper.readTree(values) , Map.class);
            for (Entry<String, String> entry : formData.entrySet()) {
                colValues.add(entry.getValue());
            }
            con = Utility.getConn(DBName , userName , password);
            
            ResultSet resultSet = con.getMetaData().getColumns(null , "%" , tableName , "%");
            String[] columnTypes = {"VARCHAR" , "CHAR" , "ENUM" , "SET" , "BLOB" , "TXT"};
            StringBuilder builder = new StringBuilder();
            for (int i = 0 ; i < colValues.size() ; i++) {
                if ("".equals(colValues.get(i))) {
                    builder.append("null");
                } else {
                    resultSet.next();
                    String columnClassName = resultSet.getString("TYPE_NAME");
                    if (Arrays.asList(columnTypes).contains(columnClassName)) {
                        builder.append("'" + colValues.get(i) + "'");
                    } else {
                        builder.append(colValues.get(i));
                    }
                }
                if (i != colValues.size() - 1) {
                    builder.append(",");
                }
            }
            //全表插入
            String sql = "INSERT INTO " + tableName + " VALUES (" + builder.toString() + ")";
//            System.out.println(sql);
            con.prepareStatement(sql).execute(sql);
            result.put("status" , "ok");
        } catch (Exception e) {
            result.put("status" , "error");
            result.put("message" , e.getMessage());
        } finally {
            Utility.close(rs , stat , con);
        }
        return result;
        
    }
    
    private Grid getGrid(String[] parameters) {
        
        List<Head> headList = new ArrayList<Head>();
        List<Rows> rowsList = new ArrayList<Rows>();
        String sql = "";
        String DBName = parameters[0];
        String tableName;
        try {
            switch (parameters.length) {
                case 2:
                    sql = parameters[1];
                    break;
                case 3:
                    tableName = parameters[1];
                    boolean isStructure = Boolean.parseBoolean(parameters[2]);
                    if (isStructure) {
                        sql = "DESC  " + tableName;
                    } else {
                        sql = "SELECT * FROM  " + tableName;
                    }
                    break;
            }
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
            
            headList.add(new Head(900 , "ro" , "left" , "Error"));
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
