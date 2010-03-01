package action;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import model.RestrictedWorkstation;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.framework.EntityHome;
import javax.faces.application.FacesMessage;
import org.jboss.seam.faces.FacesMessages;

import unix.SystemFile;

/**
 * Manages the "restricted workstation" list via DHCP and PF entries.
 * This version puts the restricted workstations in an upper range of the
 * subnet NETWORK, from STARTING_HOST *down* to but not including END_OF_DYNAMIC_RANGE.
 * NOTE: 1) first time  you run this, you *must* balance the braces - usually this
 * means adding a "}" after the stuff we add, but your mileage may vary :-) ;
 * 2) Must also have a rule in pf.conf to restrict workstations above 
 * END_OF_DYNAMIC_RANGE; 3) THis would be MUCH easier if dhcpd had an include file
 * mechanism (but that would require a rewrite of the parser, probably requiring
 * use of yacc or other parser generator - maybe not a bad thing in its own right).
 */
@Name("restrictedWorkstationHome")
@Restrict("#{identity.hasRole('admin')}")
public class RestrictedWorkstationHome extends EntityHome<RestrictedWorkstation>
{
	
	private static final long serialVersionUID = 92080918018L;
	
	// TUNE THIS
	private static final String NETWORK = "192.168.100";
	// TUNE THIS
	private static final int STARTING_HOST = 250;	// COUNTS DOWN FROM THIS
	// TUNE THIS
	private static final int END_OF_DYNAMIC_RANGE = 127; // end of dynamic range

	private static final String DHCPD_COMMAND = 
		"sudo pkill dhcpd; sudo /usr/sbin/dhcpd";
	private static final String SPLITTER_START_COMMENT = 
		"# =+ RESTRICTED LIST += DO NOT EDIT BELOW THIS LINE!! ==";
	private static final String SPLITTER_END_COMMENT = 
		"# == !!ENIL SIHT WOLEB TIDE TON OD =+ TSIL DETCIRTSER +=";

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

	/** Sync model to file; restart DHCPD.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean synchModelToFile() {
		System.out.println("RestrictedWorkstationHome.synchModelToFile()");
		// Shouldn't need this, but attempts to inject the List failed w/ "can't create"
		List<RestrictedWorkstation> rows = getEntityManager().
			createQuery("from RestrictedWorkstation ws order by ws.id").
			getResultList();
		File fTemp = null;
		try {
			BufferedReader is = new BufferedReader(new FileReader(
				SystemFile.DHCPDCONFIG.getName()));
			fTemp = File.createTempFile("dhcp", "tmp");
			PrintWriter out = new PrintWriter(fTemp);
			String line = null;
			while ((line = is.readLine()) != null) {
				if (line.startsWith(SPLITTER_START_COMMENT))
					break;
				out.println(line);
			}

			// Now look for the end marker; if we don't find it, 
			// will later write the entire tail of the file; if we do 
			// find it, will write from there to the end.
			List<String> lines = new ArrayList<String>(250);
			while ((line = is.readLine()) != null) {
				if (line.startsWith(SPLITTER_END_COMMENT)) {
					lines.clear();
					break;
				}
			}

			// Now write our part
			out.println(SPLITTER_START_COMMENT);
			int i = STARTING_HOST;
			for (RestrictedWorkstation row : rows) {

				out.printf("\thost Host%s {%n", 
					row.getLocation().trim().replaceAll("\\W+", "_"));
				out.printf("\t\thardware ethernet %s;%n", row.getMacAddress());
				out.printf("\t\tfixed-address %s.%d;%n", NETWORK, i--);
				out.println("\t}");
			}

			out.println(SPLITTER_END_COMMENT);
			for (String l : lines) {
				out.println(l);
			}

			while ((line = is.readLine()) != null) {
				out.println(line);
			}

			// Bit of after-the-fact error checking
			if (i < END_OF_DYNAMIC_RANGE) {
				FacesMessages.createFacesMessage(
					FacesMessage.SEVERITY_WARN,
						"RESTRICTED AND DYNAMIC RANGES COLLIDE");
			}

			// Now close, and copy back.
			out.close();
			String copyCommand = String.format("sudo cp %s %s", 
					fTemp.getAbsolutePath(),
					SystemFile.DHCPDCONFIG.getName());
			System.out.println("copyCommand="+copyCommand);
			return UnixCommand.runCommand("Copy file back", copyCommand) &&
				UnixCommand.runCommand("reload DHCPD", DHCPD_COMMAND);
		} catch (IOException e) {
			throw new RuntimeException("I/O Error Writing dhcpd file", e);
		} finally {
			if (fTemp != null)
				fTemp.delete();	// deleteOnExit() is not your friend in a web app		
		}
	}
}
