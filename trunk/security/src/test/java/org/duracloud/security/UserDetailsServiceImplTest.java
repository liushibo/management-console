package org.duracloud.security;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.security.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Woods
 *         Date: Mar 28, 2010
 */
public class UserDetailsServiceImplTest {

    private UserDetailsServiceImpl userDetailsService;

    private final String usernameA = "a-user";
    private final String usernameB = "b-user";
    private final String usernameC = "c-user";

    private List<String> grantsA;
    private List<String> grantsB;
    private List<String> grantsC;

    private List<SecurityUserBean> users;

    @Before
    public void setUp() {
        userDetailsService = new UserDetailsServiceImpl();

        grantsA = new ArrayList<String>();
        grantsB = new ArrayList<String>();
        grantsC = new ArrayList<String>();

        grantsA.add("ROLE_ROOT");
        grantsA.add("ROLE_ADMIN");
        grantsA.add("ROLE_USER");

        grantsB.add("ROLE_ADMIN");
        grantsB.add("ROLE_USER");

        grantsC.add("ROLE_USER");

        SecurityUserBean user0 = new SecurityUserBean(usernameA,
                                                      "apw",
                                                      true,
                                                      true,
                                                      true,
                                                      true,
                                                      grantsA);
        SecurityUserBean user1 = new SecurityUserBean(usernameB,
                                                      "apw",
                                                      true,
                                                      true,
                                                      true,
                                                      true,
                                                      grantsB);
        SecurityUserBean user2 = new SecurityUserBean(usernameC,
                                                      "upw",
                                                      true,
                                                      true,
                                                      true,
                                                      true,
                                                      grantsC);
        users = new ArrayList<SecurityUserBean>();
        users.add(user0);
        users.add(user1);
        users.add(user2);
    }

    @After
    public void tearDown() {
        userDetailsService = null;
        grantsA = null;
        grantsB = null;
        grantsC = null;
        users = null;
    }

    @Test
    public void testLoadUserByUsername() {
        userDetailsService.setUsers(users);
        UserDetails udA = userDetailsService.loadUserByUsername(usernameA);
        UserDetails udB = userDetailsService.loadUserByUsername(usernameB);
        UserDetails udC = userDetailsService.loadUserByUsername(usernameC);

        Assert.assertNotNull(udA);
        Assert.assertNotNull(udB);
        Assert.assertNotNull(udC);

        Assert.assertEquals(usernameA, udA.getUsername());
        Assert.assertEquals(usernameB, udB.getUsername());
        Assert.assertEquals(usernameC, udC.getUsername());

        GrantedAuthority[] gA = udA.getAuthorities();
        GrantedAuthority[] gB = udB.getAuthorities();
        GrantedAuthority[] gC = udC.getAuthorities();

        Assert.assertNotNull(gA);
        Assert.assertNotNull(gB);
        Assert.assertNotNull(gC);

        Assert.assertEquals(grantsA.size(), gA.length);
        Assert.assertEquals(grantsB.size(), gB.length);
        Assert.assertEquals(grantsC.size(), gC.length);

        for (GrantedAuthority auth : gA) {
            Assert.assertTrue(grantsA.contains(auth.getAuthority()));
        }
        for (GrantedAuthority auth : gB) {
            Assert.assertTrue(grantsB.contains(auth.getAuthority()));
        }
        for (GrantedAuthority auth : gC) {
            Assert.assertTrue(grantsC.contains(auth.getAuthority()));
        }

        boolean thrown = false;
        try {
            userDetailsService.loadUserByUsername("junk");
            Assert.fail("exception expected");
        } catch (UsernameNotFoundException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }
}
