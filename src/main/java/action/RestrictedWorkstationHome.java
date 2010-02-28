package action;

import java.io.*;
import java.util.List;

import model.RestrictedWorkstation;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.framework.EntityHome;

import unix.SystemFile;

@Name("restrictedWorkstationHome")
@Restrict("#{identity.hasRole('admin')}")
public class RestrictedWorkstationHome extends EntityHome<RestrictedWorkstation>
{
	
	private static final long serialVersionUID = 92080918018L;
	
	// TUNE THIS
	private static final String NETWORK = "192.168.103";
	// TUNE THIS
	private static final int STARTING_HOST = 10;

	private static final String DHCPD_COMMAND = 
		"sudo pkill dhcpd; sudo /usr/sbin/dhcpd";
	private static final String SPLITTER_COMMENT = 
		"# =+ RESTRICTED LIST += DO NOT EDIT BELOW THIS LINE!! ==";
	
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
		// Shouldn't need this, but attempts to inject the List failed w/ "can't create"
		List<RestrictedWorkstation> rows = getEntityManager().
			createQuery("from RestrictedWorkstation ws").
			getResultList();
		File fTemp = null;
		try {
			BufferedReader is = new BufferedReader(new FileReader(
				SystemFile.DHCPDCONFIG.getName()));
			fTemp = File.createTempFile("dhcp", "tmp");
			System.out.println("RestrictedWorkstationHome: filename=" + fTemp);
			PrintWriter out = new PrintWriter(fTemp);
			String line = null;
			while ((line = is.readLine()) != null) {
				if (line.startsWith(SPLITTER_COMMENT))
					break;
				out.println(line);
			}

			// Now write our part
			out.println(SPLITTER_COMMENT);
			out.printf("subnet %s.0 netmask 255.255.255.0 {%n", NETWORK);
			int i = STARTING_HOST;
			for (RestrictedWorkstation row : rows) {
				out.printf("host H%s {%n", 
					row.getLocation().trim().replaceAll("\\W+", "_"));
				out.printf("hardware ethernet %s;%n", row.getMacAddress());
				out.printf("fixed-address 192.168.3.%d;%n", i++);
				out.println("}");
			}
			out.println("}");
			out.close();
			String copyCommand = String.format("sudo cp %s %s", 
					fTemp.getAbsolutePath(),
					SystemFile.DHCPDCONFIG.getName());
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
