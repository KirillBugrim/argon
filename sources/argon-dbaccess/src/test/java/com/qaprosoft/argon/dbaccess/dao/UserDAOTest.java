package com.qaprosoft.argon.dbaccess.dao;

import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import com.qaprosoft.argon.models.db.Status;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.qaprosoft.argon.dbaccess.dao.mysql.AuthorityDAO;
import com.qaprosoft.argon.dbaccess.dao.mysql.StatusDAO;
import com.qaprosoft.argon.dbaccess.dao.mysql.UserDAO;
import com.qaprosoft.argon.dbaccess.utils.KeyGenerator;
import com.qaprosoft.argon.models.db.User;
import com.qaprosoft.argon.models.db.Authority;
import com.qaprosoft.argon.models.db.Authority.AuthorityType;
import com.qaprosoft.argon.models.db.Status.StatusType;

/**
 * @author asemenkov
 * @since 07 Dec 2017
 */
@Test
@ContextConfiguration("classpath:com/qaprosoft/argon/dbaccess/dbaccess-test.xml")
public class UserDAOTest extends AbstractTestNGSpringContextTests
{
	private static final boolean ENABLED = false;

	@Autowired
	private StatusDAO statusDAO;

	@Autowired
	private AuthorityDAO authorityDAO;

	@Autowired
	private UserDAO userDAO;

	private static final Status STATUS = new Status();
	{
		STATUS.setStatusType(StatusType.TEST_OFLINE);
	}

	private static final Authority AUTHORITY = new Authority();
	{
		AUTHORITY.setAuthorityType(AuthorityType.TEST_ADMIN);
	}

	private final static User USER = new User();
	{
		USER.setEmail(KeyGenerator.getKey() + "@test-mail.com");
		USER.setEnabled(true);
		USER.setFirstName("Boris");
		USER.setLastName("The Blade");
		USER.setPassword("pass" + KeyGenerator.getKey().toString());
		USER.setDob(DateTime.now().withTime(0, 0, 0, 0).minusYears(18).toDate());
		USER.setUsername("user" + KeyGenerator.getKey());
		USER.setVerified(true);
		USER.setStatus(STATUS);
	}

	@BeforeClass
	public void init(){
		statusDAO.createStatus(STATUS);
	}

	@AfterClass
	public void delete(){
		statusDAO.deleteStatusById(STATUS.getId());
		authorityDAO.deleteAuthorityById(AUTHORITY.getId());
	}

	@Test(enabled = ENABLED)
	public void createUser()
	{
		userDAO.createUser(USER);
		assertNotEquals(USER.getId(), 0, "User ID must be set up by autogenerated keys.");
	}

	@Test(enabled = ENABLED, dependsOnMethods = "createUser", expectedExceptions = DuplicateKeyException.class)
	public void createUserFail()
	{
		userDAO.createUser(USER);
	}

	@Test(enabled = ENABLED, dependsOnMethods = "createUser")
	public void getUserById()
	{
		checkUser(userDAO.getUserById(USER.getId()));
	}

	@Test(enabled = ENABLED, dependsOnMethods = "createUser")
	public void getUserByUserName()
	{
		checkUser(userDAO.getUserByUserName(USER.getUsername()));
	}

	@Test(enabled = ENABLED, dependsOnMethods = "createUser")
	public void getUserByEmail()
	{
		checkUser(userDAO.getUserByEmail(USER.getEmail()));
	}

	@Test(enabled = ENABLED, dependsOnMethods = "createUser")
	public void updateUser()
	{
		USER.setFirstName("Nikita");
		USER.setLastName("Mihalkov");
		userDAO.updateUser(USER);
		checkUser(userDAO.getUserById(USER.getId()));
	}

	@Test(enabled = ENABLED, dependsOnMethods = "createUser")
	public void addAuthority()
	{
		authorityDAO.createAuthority(AUTHORITY);
		USER.setAuthorities(new ArrayList<>(Arrays.asList()));
		userDAO.addAuthority(USER.getId(), AUTHORITY.getId());
		USER.getAuthorities().add(AUTHORITY);
		checkUser(userDAO.getUserById(USER.getId()));
	}

	@Test(enabled = ENABLED, dependsOnMethods = "addAuthority")
	public void deleteAuthority()
	{
		userDAO.deleteAuthority(USER.getId(), AUTHORITY.getId());
		USER.getAuthorities().remove(AUTHORITY);
		checkUser(userDAO.getUserById(USER.getId()));
	}

	// Only 1 of these flags must be true
	private static final boolean DELETE_USER_BY_ID = false;
	private static final boolean DELETE_USER_BY_USERNAME = true;
	private static final boolean DELETE_USER_BY_EMAIL = false;

	@Test(enabled = ENABLED && DELETE_USER_BY_ID, dependsOnMethods =
	{ "createUser", "createUserFail", "getUserById",
			"getUserByUserName", "getUserByEmail", "updateUser", "deleteAuthority" })
	public void deleteUserById()
	{
		userDAO.deleteUserById(USER.getId());
		assertNull(userDAO.getUserById(USER.getId()));
	}

	@Test(enabled = ENABLED && DELETE_USER_BY_USERNAME, dependsOnMethods =
	{ "createUser", "createUserFail", "getUserById",
			"getUserByUserName", "getUserByEmail", "updateUser", "deleteAuthority" })
	public void deleteUserByUserName()
	{
		userDAO.deleteUserByUserName(USER.getUsername());
		assertNull(userDAO.getUserByUserName(USER.getUsername()));
	}

	@Test(enabled = ENABLED && DELETE_USER_BY_EMAIL, dependsOnMethods =
	{ "createUser", "createUserFail", "getUserById",
			"getUserByUserName", "getUserByEmail", "updateUser", "deleteAuthority" })
	public void deleteUserByEmail()
	{
		userDAO.deleteUserByEmail(USER.getEmail());
		assertNull(userDAO.getUserByEmail(USER.getEmail()));
	}

	private void checkUser(User user)
	{
		assertEquals(user.getUsername(), USER.getUsername(), "Username is not as expected.");
		assertEquals(user.getDob().compareTo(USER.getDob()), 0, "User's dob is not as expected.");
		assertEquals(user.getEmail(), USER.getEmail(), "User's email is not as expected.");
		assertEquals(user.getEnabled(), USER.getEnabled(), "User's enabled flag is not as expected.");
		assertEquals(user.getFirstName(), USER.getFirstName(), "User's first name is not as expected.");
		assertEquals(user.getId(), USER.getId(), "User's id is not as expected.");
		assertEquals(user.getLastName(), USER.getLastName(), "User's last name is not as expected.");
		assertEquals(user.getPassword(), USER.getPassword(), "User's password is not as expected.");
		assertEquals(user.getStatus().getId(), USER.getStatus().getId(), "User's status is not as expected.");
		assertEquals(user.getStatus().getStatusType(), USER.getStatus().getStatusType(), "User's status is not as expected.");
		assertEquals(user.getVerified(), USER.getVerified(), "User's verified flag is not as expected.");
		assertEquals(user.getAuthorities().size(), USER.getAuthorities().size(),
				"User's size of athorites is not as expected.");
	}

}
