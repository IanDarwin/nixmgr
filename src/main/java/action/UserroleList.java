package action;

import model.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("userroleList")
public class UserroleList extends EntityQuery<Userrole> {

	private static final String EJBQL = "select userrole from Userrole userrole";

	private static final String[] RESTRICTIONS = {"lower(userrole.name) like concat(lower(#{userroleList.userrole.name}),'%')",};

	private Userrole userrole = new Userrole();

	public UserroleList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Userrole getUserrole() {
		return userrole;
	}
}
