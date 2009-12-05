package action;

import javax.persistence.EntityManager;

import model.Account;
import model.Userrole;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
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

	public boolean authenticate() {
		log.info("authenticating {0}", credentials.getUsername());
		Account p = null;
		try {
			p = (Account) entityManager.createQuery("from Account a where a.username = #{credentials.username}").getSingleResult();
		} catch (Exception e) {
			System.err.println("FAIL: " + e);
			return false;
		}
		if (!p.getPassword().equals(credentials.getPassword())) {
			System.err.println("FAIL: password");
			return false;
		}
		for (Userrole r : p.getRoles()) {
			identity.addRole(r.getName());
		}
		return true;
	}
}

