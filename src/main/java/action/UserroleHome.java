package action;

import model.Userrole;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("userroleHome")
public class UserroleHome extends EntityHome<Userrole> {

	private static final long serialVersionUID = -5806479533396764768L;

	public void setUserroleId(Integer id) {
		setId(id);
	}

	public Integer getUserroleId() {
		return (Integer) getId();
	}

	@Override
	protected Userrole createInstance() {
		Userrole userrole = new Userrole();
		return userrole;
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public Userrole getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
}
