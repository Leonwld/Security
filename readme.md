
### web.xml
在web.xml中增加spring mvc 与 spring security配置：

    <?xml version="1.0" encoding="UTF-8"?>
    <web-app
            xmlns="http://java.sun.com/xml/ns/javaee"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
            version="3.0">
        <context-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>
                classpath*:applicationContext-*.xml
            </param-value>
        </context-param>

        <!-- UTF-8 encoding filter -->
        <filter>
            <filter-name>encodingFilter</filter-name>
            <filter-class>
                org.springframework.web.filter.CharacterEncodingFilter
            </filter-class>
            <init-param>
                <param-name>encoding</param-name>
                <param-value>UTF-8</param-value>
            </init-param>
            <init-param>
                <param-name>forceEncoding</param-name>
                <param-value>true</param-value>
            </init-param>
        </filter>
        <filter-mapping>
            <filter-name>encodingFilter</filter-name>
            <url-pattern>/*</url-pattern>
        </filter-mapping>

        <!-- spring security3.2 Filter-->
        <filter>
            <filter-name>springSecurityFilterChain</filter-name>
            <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
        </filter>
        <filter-mapping>
            <filter-name>springSecurityFilterChain</filter-name>
            <url-pattern>/*</url-pattern>
        </filter-mapping>



        <!-- Web App Dispatcher -->
        <servlet>
            <servlet-name>AppBaseDispatcherServlet</servlet-name>
            <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
            <init-param>
                <param-name>contextConfigLocation</param-name>
                <param-value>classpath:app-servlet.xml</param-value>
            </init-param>
            <load-on-startup>1</load-on-startup>
        </servlet>

        <servlet-mapping>
            <servlet-name>AppBaseDispatcherServlet</servlet-name>
            <url-pattern>/</url-pattern>
        </servlet-mapping>



        <!--Spring ApplicationContext 载入 -->
        <listener>
            <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
        </listener>

    </web-app>

### app-servlet.xml

    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:context="http://www.springframework.org/schema/context"
           xmlns:tx="http://www.springframework.org/schema/tx"
           xmlns:mvc="http://www.springframework.org/schema/mvc"
           xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.2.xsd
               http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.2.xsd
               http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.2.xsd
       http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc-3.2.xsd">

        <!-- 不过滤静态资源 -->
        <mvc:resources mapping="/resources/**" location="/resources/" order="0" />
        <mvc:resources mapping="/favicon.ico" location="/resources/img/favicon.ico" order="0"/>
        <!-- 自动扫描 -->
        <context:component-scan base-package="org.excalibur" ></context:component-scan>

        <!-- 定义JSP文件的位置 -->
        <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
            <property name="viewClass" value="org.springframework.web.servlet.view.JstlView" />
            <property name="prefix" value="/WEB-INF/views/jsp/"/>
            <property name="suffix" value=".jsp"/>
        </bean>

        <!-- 默认的注解映射的支持 -->
        <tx:annotation-driven />
        <mvc:annotation-driven />


    </beans>

### applicationContext-security.xml
编写applicationContext-security.xml的spring security配置：

    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:sec="http://www.springframework.org/schema/security"
           xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.2.xsd
                            http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security.xsd">

        <!--开启注解-->
        <!--pre-post注解是方法级别的 支持事先或事后-->
        <sec:global-method-security pre-post-annotations="enabled" />

    </beans>

### 业务逻辑层
接下来我们写个业务逻辑层来测试一下。
UserService:

    package org.excalibur.books.controller.org.excalibur.books.service;

    import org.springframework.security.access.prepost.PreAuthorize;

    /**
     * Created with IntelliJ IDEA.
     * User: faith
     * Date: 13-6-24
     * Time: 上午11:13
     * To change this template use File | Settings | File Templates.
     */
    public interface UserService {

        @PreAuthorize("hasRole('ROLE_USER')")
        public String login(String name,String password);

    }

UserServiceImpl:

    package org.excalibur.books.controller.org.excalibur.books.service.impl;

    import UserService;
    import org.springframework.stereotype.Service;

    /**
     * Created with IntelliJ IDEA.
     * User: faith
     * Date: 13-6-24
     * Time: 上午11:15
     * To change this template use File | Settings | File Templates.
     */
    @Service("userService")
    public class UserServiceImpl implements UserService{
        @Override
        public String login(String name,String password) {
            System.out.println(name+","+password);
            return "登录成功";
        }
    }

### 测试
UserServiceTest：

    package org.excalibur.books.service;

    import junit.framework.Assert;
    import UserService;
    import org.junit.Test;
    import org.junit.runner.RunWith;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.access.AccessDeniedException;
    import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.authority.AuthorityUtils;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.test.context.ContextConfiguration;
    import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

    /**
     * Created with IntelliJ IDEA.
     * User: faith
     * Date: 13-6-24
     * Time: 上午11:22
     * To change this template use File | Settings | File Templates.
     */
    @ContextConfiguration(locations = {"classpath*:app-servlet.xml","classpath*:applicationContext-security.xml"})
    @RunWith(SpringJUnit4ClassRunner.class)
    public class UserServiceTest {
        private Authentication admin = new UsernamePasswordAuthenticationToken("test", "xxx", AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
        private Authentication user = new UsernamePasswordAuthenticationToken("test", "xxx", AuthorityUtils.createAuthorityList("ROLE_USER"));

        @Autowired
        private UserService userService;


        @Test(expected = AuthenticationCredentialsNotFoundException.class)
        public void testLogin1(){

            userService.login("dario","123456");

        }
        @Test(expected = AccessDeniedException.class)
        public void testLogin2(){
            SecurityContextHolder.getContext().setAuthentication(admin);
            userService.login("dario", "123456");

        }
        @Test
        public void testLogin3(){
            SecurityContextHolder.getContext().setAuthentication(user);
            String result = userService.login("excalibur", "123456");
            Assert.assertEquals(dario,"登录成功");
        }


    }
