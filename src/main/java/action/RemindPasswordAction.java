package action;

import javax.persistence.EntityManager;

import model.Account;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;

import unix.SystemAccountAccessor;

@Name("remindPasswordAction")
@Restrict("#{!identity.loggedIn}")
public class RemindPasswordAction {

	private String userName;
	@In private EntityManager entityManager;
	@In Renderer renderer;
	private Account forgetful;

	private String fail(String mesg) {
		FacesMessages.instance().add(mesg);
		return null;
	}

	@End
	public String remind() {
		if (userName == null) {
			return fail("Username is required");
		}

		forgetful = (Account) entityManager.createQuery(
			"from Account a where a.username = ?1").setParameter(1,userName).
			getSingleResult();

		System.out.println("BEFORE: Forgetful = " + forgetful);

		// Need this in scope BEFORE the method ends (e.g., when 
		// 99.44% of all outjections happen), so Do It Now.
		Contexts.getConversationContext().set("forgetful", forgetful);

		renderer.render("/account/remind-password-email.xhtml");

		System.out.println("AFTER: Forgetful = " + forgetful);

		return "reminded";
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
