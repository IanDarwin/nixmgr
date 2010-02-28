package action;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import model.Account;

import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;

@Name("remindPasswordAction")
public class RemindPasswordAction {

	private String userName;
	@In private EntityManager entityManager;
	@In private FacesMessages facesMessages;
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

		try {
		    forgetful = (Account) entityManager.createQuery(
			"from Account a where a.username = ?1").setParameter(1,userName).
			getSingleResult();
		} catch (NoResultException ex) {
			facesMessages.add("User name not found.");
			return null;
		}

		// Need this in scope BEFORE the method ends (e.g., when 
		// 99.44% of all outjections happen), so Do It Now.
		Contexts.getConversationContext().set("forgetful", forgetful);

		renderer.render("/account/remind-password-email.xhtml");

		return "reminded";
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
