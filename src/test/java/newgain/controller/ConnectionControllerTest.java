package newgain.controller;

import java.util.Map;
import newgain.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public class ConnectionControllerTest extends BaseTest {
    
    @Autowired
    ConnectionController connection;
    @Autowired
    private WebApplicationContext context;
    private MockMvc mv;
    
    @Before
    public void setUp() {
        
        this.mv = MockMvcBuilders.webAppContextSetup(context).build();
    }
    
    @Test
    public void getConnection() throws Exception {
        
        mv.perform(post("/connection?serverName=aaa&userName=&password=")).andDo(print());
    }
    
    @Test
    public void retrieve() throws Exception {
        
        mv.perform(post("/retrieve?DBName=web&tableName=user&structure=true")).andDo(print());
    }
    
    @Test
    public void query() throws Exception {
        
        mv.perform(post("/query?DBName=web&sqlStatement=select * from books")).andDo(print());
    }
    
    @Test
    public void deleteRow() throws Exception {
        
        mv.perform(post("/deleteRow?DBName=web&tableName=number&colName=id&value=3")).andDo(print());
    }
    
    @Test
    public void insertRow() throws Exception {
    
       connection.insertRow("web" , "user" , "{\"id\":\"R\",\"username\":\"\",\"password\":\"\",\"cn_activity_end_time\":\"\"}");
    
    }
    
}
