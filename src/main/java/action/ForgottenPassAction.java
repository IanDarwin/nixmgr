package action;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import model.Account;
import model.ForgetterRequest;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;

import com.darwinsys.security.PassPhrase;

/**
 * ForgottenPassAction: allows existing users to recover their account password.
 * The password reset is noted in a ForgetterRequest,
 * and the user is sent an  email with a link to create a new password.  
 * <p>Has two related actions, requestReset() and approve().
 * <p>Do NOT annotate with Restrict("#{identity.loggedIn}")!
 */
@Scope(ScopeType.EVENT)
@Name("forgottenPassAction")
public class ForgottenPassAction implements Serializable {

	private static final long serialVersionUID = -7327405743544720117L;

	@In	private EntityManager entityManager;

	@In Renderer renderer;

	@In private FacesMessages facesMessages;

	@In(required = false) private ForgetterBean forgetterBean;
	
	@Out(required = false) 
	private Account person = null;

	/** Used by the "password changed OK" email */
	private String userName;
	private String password;
	private String email;
	private String name;
	private String requestCode;

	/**
	 * Request code parameter passed in during phase 2.
	 */
	private String approval;

	/** Phase One of the forgotten password recovery.
	 */
	@Transactional
	public String requestReset() {

		if (forgetterBean == null) {
			throw new IllegalStateException("no forgetterBean!");
		}
		
		userName = forgetterBean.getUserName(); // One of these two may be null here
		email    = forgetterBean.getEmail();
		
		if ((userName == null || userName.length() == 0) && (email == null || email.length() == 0)) {
			facesMessages.add("Please specify a user name or email address.");
			return null;
		}

		if (userName != null && userName.length() > 0) {			
			// Make sure username is in use
			try {
				person = (Account) entityManager.createQuery("from Account p where p.username=#{forgetterBean.userName}").getSingleResult();
			} catch (NoResultException e) {
				facesMessages.add("User named #{forgetterBean.userName} was not found in our database.");
				return null;
			}
		}

		if (email != null && email.length() > 0) {
			try {
				person = (Account) entityManager.createQuery("from Account p where p.email=#{forgetterBean.email}").getSingleResult();
			} catch (NoResultException e) {
				this.facesMessages.add("Could not find an account with #{forgetterBean.email} as its email address.");
				return null;
			}
		}

		// Should not happen, but prevent great sadness later if it does.
		if (person == null) {
			facesMessages.add("I'm confused, no Person object");
			return null;
		}
		
		// User normally sets only one of these two, so update the other.
		// At this point, neither of these may be null.
		if (userName == null || userName.length() == 0)
			userName = person.getUsername();
		if (userName == null) {
			throw new NullPointerException("userName");
		}
		if (email == null || email.length() == 0)
			email = person.getEmail();
		if (email == null) {
			throw new NullPointerException("email");
		}
		
		name = person.getName();
		
		// Make sure the account is enabled (XXX when you implement this,
		// use Enums instead of STRINGS in the database).
		//if (!AccountStatus.Enabled.toString().equals(person.getAccountStatus())) {
		//	facesMessages.add("Sorry, your account is not currently enabled.");
		//	return null;
		//}

		ForgetterRequest forgetterRequest = new ForgetterRequest(person);
		requestCode = forgetterRequest.getRequestCode();
		entityManager.persist(forgetterRequest);

		// Send the notify email
		renderer.render("/account/forgotten-request-email.xhtml");

		// Tell user on-screen
		facesMessages.add(
				"We have sent an email to your registered email address " + 
					"with instructions on the next step; your password " + 
					"will not be changed until you complete this process.");

		// Send them back to the login page.
		return "login";
	}

	/** Called when the user clicks on the "approve"
	 * link in the email we sent them in "remember()"
	 */
	@End
	@Transactional
	public String approve() {

		// 1A Look up the Forgetter Request in the database by its string name
		ForgetterRequest verifyRequest = null;
		try {
			verifyRequest =  findForgetterRequest(approval);
		} catch (NoResultException e) {
			facesMessages.addFromResourceBundle("forget.approve.requestNotFound");
			return null;
		}

		// 1B Retrieve the Person named in the person field this forgetter_request
		try {
			person = 
				(Account) entityManager.createQuery(
					"from Account p where p.id = ?1").
					setParameter(1, verifyRequest.getPerson().getId()).
						getSingleResult();
		} catch (NoResultException e) {
			this.facesMessages.add(
				"There was an error in your password change request; please start over");
			return null;
		}
		
		name = person.getName();
		email = person.getEmail();
		userName = person.getUsername();
		password = PassPhrase.getNext(8);
		person.setPassword(password);
		entityManager.merge(person);	// write new password back
		
		// Send the notify email
		renderer.render("/account/forgotten-reset-done-email.xhtml");
		
		// Remove our in-memory reference to password
		password = null;
		
		removeRequest(verifyRequest.getRequestCode());

		// Tell user on-screen
		this.facesMessages.add(
				"We have sent an email to your registered email address " + 
				"indicating your new password.");		

		return "login";
	}
	
	/** Called if the user presses the Cancel button */
	@End
	public String cancel() {
		if (approval != null) {
			removeRequest(approval);
		}
		return "login";
	}

	private ForgetterRequest findForgetterRequest(String approvalName) {
		return (ForgetterRequest) entityManager.createQuery(
				"from ForgetterRequest fr where fr.requestCode=?1").
					setParameter(1, approvalName).getSingleResult();
	}

	/** The request is removed either if it is acted upon
	 * or the user who gets the email presses Cancel on
	 * the page the email links to. Must be by string name
	 * because the verifyRequest only exists in the approve() method.
	 * @param verifyRequest
	 */
	private void removeRequest(String verifyRequest) {
		System.out.print("ForgottenPassAction.removeRequest()");
		ForgetterRequest v = findForgetterRequest(verifyRequest);
		entityManager.remove(v);	// cannot be re-used.
		System.out.println(" DONE");
	}

	public Account getPerson() {
		return person;
	}
	
	public void setPerson(Account person) {
		this.person = person;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String newPassword) {
		this.password = newPassword;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ForgetterBean getForgetterBean() {
		return forgetterBean;
	}

	/**
	 * @param forgetterBean the forgetterBean to set
	 */
	public void setForgetterBean(ForgetterBean forgetterBean) {
		this.forgetterBean = forgetterBean;
	}

	/**
	 * @return the approval string used in Phase 2
	 */
	public String getApproval() {
		return approval;
	}

	/**
	 * @param approval The string set and used in Phase 2
	 */
	public void setApproval(String approval) {
		this.approval = approval;
	}

	public String getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(String requestCode) {
		this.requestCode = requestCode;
	}
}
