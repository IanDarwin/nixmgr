package action;

import model.Account;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.annotations.security.Restrict;

import unix.SystemAccountAccessor;

@Name("chPassAction")
@Restrict("#{identity.loggedIn}")
public class ChPassAction {
	
	private String oldPassword;
	private String newPassword1;
	private String newPassword2;
	@In
	private Account loggedInUser;

	private String fail(String mesg) {
		FacesMessages.instance().add(mesg);
		return "failure";
	}

	public String change() {
		if (loggedInUser == null)
			return fail("loggedInUser is null");
		final String userPassword = loggedInUser.getPassword();
		if (!oldPassword.equals(userPassword)) {
			return fail("Old password incorrect");
		}
		if (!newPassword1.equals(newPassword2)) {
			return fail("New passwords do not match");
		}

		loggedInUser.setPassword(newPassword1);

		// Update the system account first!
		if (!SystemAccountAccessor.getInstance().updateAccount(loggedInUser)) {
			return fail("System error: Could not update password");
		}

		// No need to explicitly update the database.
		// since loggedInUser is known to JPA and all
		// Seam methods are Transactional, the above
		// setPassword() call is all that's needed.
		
		return "updated";
	}

	public String getNewPassword1() {
		return newPassword1;
	}

	public void setNewPassword1(String newPassword1) {
		this.newPassword1 = newPassword1;
	}

	public String getNewPassword2() {
		return newPassword2;
	}

	public void setNewPassword2(String newPassword2) {
		this.newPassword2 = newPassword2;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
}
