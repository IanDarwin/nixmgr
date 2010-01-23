package action;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import model.RestrictedWorkstation;

@Name("restrictedWorkstationList")
public class RestrictedWorkstationList extends EntityQuery<RestrictedWorkstation>
{
	private static final long serialVersionUID = 1L;

	public RestrictedWorkstationList()
    {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
    }
	
	private static final String EJBQL = "select restrictedWorkstation from RestrictedWorkstation restrictedWorkstation";

	private static final String[] RESTRICTIONS = {
		"lower(restrictedWorkstation.macAddress) like concat(lower(#{restrictedWorkstationList.restrictedWorkstation.macAddress}),'%')",
		"lower(restrictedWorkstation.location) like concat(lower(#{restrictedWorkstationList.restrictedWorkstation.location}),'%')",
		};

	private RestrictedWorkstation restrictedWorkstation = new RestrictedWorkstation();

	public RestrictedWorkstation getRestrictedWorkstation() {
		return restrictedWorkstation;
	}
}
