package newgain.controller;

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
    public void setUP() {
        
        this.mv = MockMvcBuilders.webAppContextSetup(context).build();
    }
    
    @Test
    public void getConnection() throws Exception {
        
        mv.perform(post("/connection")).andDo(print());
    }
    
    @Test
    public void retrieve() throws Exception {
        
        mv.perform(post("/retrieve?tableName=user&structure=true")).andDo(print());
    }
    
    @Test
    public void query() throws Exception {
        
        mv.perform(post("/query?sqlStatement=select * from books")).andDo(print());
    }
    
}
