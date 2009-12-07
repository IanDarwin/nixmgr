package action;

import java.util.Date;

import javax.persistence.EntityManager;

import model.Account;
import model.Userrole;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

@Name("authenticator")
public class Authenticator {
	@Logger
	private Log log;
	@In
	EntityManager entityManager;
	@In
	Identity identity;
	@In
	Credentials credentials;
	@Out(required=false) // null if login fails!
	Account loggedInUser;

	public boolean authenticate() {
		log.info("authenticating {0}", credentials.getUsername());
		try {
			loggedInUser = 
			(Account) entityManager.createQuery(
			"from Account a where a.username = #{credentials.username}").
			getSingleResult();
		} catch (Exception e) {
			System.err.println("FAIL: " + e);
			return false;
		}
		if (!loggedInUser.getPassword().equals(credentials.getPassword())) {
			System.err.println("FAIL: password");
			loggedInUser = null;
			return false;
		}
		for (Userrole r : loggedInUser.getRoles()) {
			identity.addRole(r.getName());
		}
		loggedInUser.setLastlogin(new Date());
		return true;
	}
}
