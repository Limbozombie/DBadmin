package newgain;

import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.support.WebApplicationObjectSupport;

@RunWith(SpringJUnit4ClassRunner.class) //指定Junit的Runner,帮我们创建容器
@ContextConfiguration("classpath:spring/spring-*.xml") //指定创建容器时使用的配置文件
@WebAppConfiguration("/webapp")   //模拟ServletContext并指定"/webapp"作为应用名
public class BaseTest extends WebApplicationObjectSupport {
    
    protected MockHttpServletRequest request = new MockHttpServletRequest();
    protected MockHttpServletResponse response = new MockHttpServletResponse();
}
