package action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/** 
 * Trivial JavaBean to hold fields for both steps of ForgettenPassAction.
 */
@Name("forgetterBean") @Scope(ScopeType.EVENT)
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
	@Override
	public String toString() {
		return "ForgetterBean[" +
			getUserName() + ',' +
			getEmail() + ',' +
			getRequestCode() + ']';				
	}
}
