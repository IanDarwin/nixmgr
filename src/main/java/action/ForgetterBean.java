package action;

import org.jboss.seam.annotations.Name;

/** 
 * Trivial JavaBean to hold fields for both steps of ForgettenPassAction.
 */
@Name("forgetterBean")
public class ForgetterBean {
	
	private String userName;
	private String email;
	public String requestCode;
	
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
	public String getRequestCode() {
		return requestCode;
	}
	public void setRequestCode(String requestCode) {
		this.requestCode = requestCode;
	}
	
}
