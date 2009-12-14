package action;

import model.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("whitelistHome")
public class WhitelistHome extends EntityHome<Whitelist> {

	public void setWhitelistId(Integer id) {
		setId(id);
	}

	public Integer getWhitelistId() {
		return (Integer) getId();
	}

	@Override
	protected Whitelist createInstance() {
		Whitelist whitelist = new Whitelist();
		return whitelist;
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public Whitelist getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
}
