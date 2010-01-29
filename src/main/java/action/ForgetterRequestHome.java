package action;

import model.*;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("forgetterRequestHome")
public class ForgetterRequestHome extends EntityHome<ForgetterRequest> {

	@In(create = true)
	AccountHome personHome;

	public void setForgetterRequestId(Integer id) {
		setId(id);
	}

	public Integer getForgetterRequestId() {
		return (Integer) getId();
	}

	@Override
	protected ForgetterRequest createInstance() {
		ForgetterRequest forgetterRequest = new ForgetterRequest();
		return forgetterRequest;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Account person = personHome.getDefinedInstance();
		if (person != null) {
			getInstance().setPerson(person);
		}
	}

	public boolean isWired() {
		if (getInstance().getPerson() == null)
			return false;
		return true;
	}

	public ForgetterRequest getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}