package action;

import java.util.Arrays;

import model.Userrole;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("userroleList")
public class UserroleList extends EntityQuery<Userrole> {

	private static final long serialVersionUID = -2879698878894039382L;

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
