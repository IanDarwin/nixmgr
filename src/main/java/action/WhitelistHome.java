package action;

import java.util.List;

import model.Whitelist;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.framework.EntityHome;

import unix.SystemFile;
import unix.SystemFileAccessor;

@Name("whitelistHome")
@Restrict("#{identity.hasRole('admin')}")
public class WhitelistHome extends EntityHome<Whitelist> {

	private static final long serialVersionUID = 3385939853354450988L;
	private final static SystemFile FFF = SystemFile.WHITELIST;

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

	@Override
	/** Save the instance to the DB, then rewrite the file */
	public String persist() {
		String resp = super.persist();
		if (resp == null) {
			return null;
		}
		return synchModelToFile() ? resp : null;
	}

	@Override
	/** Delete the instance from the DB, then rewrite the file */
	public String remove() {
		String resp = super.remove();
		if (resp == null) {
			return null;
		}
		return synchModelToFile() ? resp : null;
	}

	@Override
	/** Update the instance in the DB, then rewrite the file */
	public String update() {
		String resp = super.update();
		if (resp == null) {
			return null;
		}
		return synchModelToFile() ? resp : null;
	}

	@SuppressWarnings("unchecked")
	private boolean synchModelToFile() {
		// Sorry to do this, but attempts to inject the WhitelistList failed w/ "can't create"
		List<String> names = getEntityManager().
			createQuery("select wl.name from Whitelist wl").
			getResultList();
		SystemFileAccessor.store(FFF, 
				"List of sites that is allowed for all hosts",
				names);
		return true;	// store() will blow up if it fails.
	}
}
