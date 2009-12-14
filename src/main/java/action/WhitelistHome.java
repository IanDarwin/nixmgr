package action;

import java.util.ArrayList;
import java.util.List;

import model.Whitelist;

import org.jboss.seam.annotations.In;
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

	@In(required=true)
	private WhitelistList whitelistList;

	private boolean synchModelToFile() {
		List<Whitelist> all = whitelistList.getResultList();
		List<String> names = new ArrayList<String>();
		for (Whitelist w : all) {
			names.add(w.getName());
		}
		SystemFileAccessor.store(FFF, names);
		return true;	// store() will blow up if it fails.
	}
}
