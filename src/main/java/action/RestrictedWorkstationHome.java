package action;

import java.util.List;

import model.RestrictedWorkstation;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.framework.EntityHome;

import unix.SystemFile;
import unix.SystemFileAccessor;

@Name("restrictedWorkstationHome")
@Restrict("#{identity.hasRole('admin')}")
public class RestrictedWorkstationHome extends EntityHome<RestrictedWorkstation>
{
	private static final long serialVersionUID = 1L;

	private static final String PF_COMMAND = "sudo pfctl -Tl -f /etc/pf.conf";
	
	public void setRestrictedWorkstationId(Integer id) {
		setId(id);
	}

	public Integer getRestrictedWorkstationId() {
		return (Integer) getId();
	}
	
	@Override
	protected RestrictedWorkstation createInstance() {
		return new RestrictedWorkstation();
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public RestrictedWorkstation getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
    @Override @Begin
    public void create() {
        super.create();
    }
    
    // When this entity is saved or updated,
    // we MUST sync to external files and
    // notify pf.
    
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
		// Sorry to do this, but attempts to inject the List failed w/ "can't create"
		List<String> names = getEntityManager().
			createQuery("select ws.macAddress from RestrictedWorkstation ws").
			getResultList();
		SystemFileAccessor.store(
			SystemFile.RESTRICTEDWORKSATATIONLIST, 
			"Workstations that can only access whitelisted sites",
			names);
		return UnixCommand.runCommand("reload PF", PF_COMMAND);
	}
}
