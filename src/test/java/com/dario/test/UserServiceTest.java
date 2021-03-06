package com.dario.test;

import junit.framework.Assert;
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
import com.dario.service.UserService;

/**
 * Created with IntelliJ IDEA.
 * User: faith
 * Date: 16-3-14
 * Time: 上午11:22
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:app-servlet.xml","classpath:applicationContext-security.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class UserServiceTest {
    private Authentication admin = new UsernamePasswordAuthenticationToken("test", "xxx", AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
    private Authentication user = new UsernamePasswordAuthenticationToken("test", "xxx", AuthorityUtils.createAuthorityList("ROLE_USER"));

    @Autowired
    private UserService userService;


    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void testLogin1(){

        String result = userService.login("excalibur","123456");
        Assert.assertEquals(result,"登录成功");

    }
    @Test(expected = AccessDeniedException.class)
    public void testLogin2(){
        SecurityContextHolder.getContext().setAuthentication(admin);
        String result = userService.login("excalibur", "123456");
        Assert.assertEquals(result,"登录成功");

    }
    @Test
    public void testLogin3(){
        SecurityContextHolder.getContext().setAuthentication(user);
        String result = userService.login("excalibur", "123456");
        Assert.assertEquals(result,"登录成功");
    }


}
