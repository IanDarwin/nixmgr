package model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;

/**
 * Account represents one external operating-system account
 */
@Entity
@Table(name = "account", schema = "public",
    uniqueConstraints = @UniqueConstraint(columnNames="username"))
public class Account implements java.io.Serializable {

	private static final long serialVersionUID = 8446621948625634088L;
	/** names etc. can contain letters, spaces, apostrophe for the irish */
	private final static String NAME_PATT = "[\\w '.]+";
	/** login names must be lowercase, fit this length range */
	private static final String USERNAME_PATTERN = "[a-z]{4,8}";
	/** passwords must have this minimu length */
	private static final String PASSWORD_PATT = ".{6,}";
	private int id;
	private String firstname;
	private String lastname;
	private Boolean systemAccountCreated;
	private String username;
	private String password;
	private Date lastlogin;
	private Float logincredits;
	private Float loginused;
	private Integer printcredits;
	private Integer printpagesused;
	private Set<UserInRole> userInRoles = new HashSet<UserInRole>(0);

	public Account() {
	}

	public Account(int id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	@NotNull
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "firstname", length = 15)
	@Length(max = 15)
	@NotNull
	@Pattern(regex=NAME_PATT)
	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	@Column(name = "lastname", length = 15)
	@Length(max = 15)
	@NotNull
	@Pattern(regex=NAME_PATT)
	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	@Column(name = "unixcreated")
	public Boolean getSystemAccountCreated() {
		return this.systemAccountCreated;
	}

	public void setSystemAccountCreated(Boolean unixcreated) {
		this.systemAccountCreated = unixcreated;
	}

	@Column(name = "username", length = 8)
	@Length(max = 8)
	@NotNull
	@Pattern(regex=USERNAME_PATTERN)
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "password", length = 8)
	@Length(max = 8)
	@Pattern(regex=PASSWORD_PATT)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	@Temporal(TemporalType.DATE)
	@Column(name = "lastlogin", length = 13)
	public Date getLastlogin() {
		return this.lastlogin;
	}

	public void setLastlogin(Date lastlogin) {
		this.lastlogin = lastlogin;
	}

	@Column(name = "logincredits", precision = 8, scale = 8)
	public Float getLogincredits() {
		return this.logincredits;
	}

	public void setLogincredits(Float logincredits) {
		this.logincredits = logincredits;
	}

	@Column(name = "loginused", precision = 8, scale = 8)
	public Float getLoginused() {
		return this.loginused;
	}

	public void setLoginused(Float loginused) {
		this.loginused = loginused;
	}

	@Column(name = "printcredits")
	public Integer getPrintcredits() {
		return this.printcredits;
	}

	public void setPrintcredits(Integer printcredits) {
		this.printcredits = printcredits;
	}

	@Column(name = "printpagesused")
	public Integer getPrintpagesused() {
		return this.printpagesused;
	}

	public void setPrintpagesused(Integer printpagesused) {
		this.printpagesused = printpagesused;
	}
	
	@Transient
	public Set<Userrole> getRoles() {
		Set<Userrole> results = new HashSet<Userrole>();
		for (UserInRole uir : getUserInRoles()) {
			results.add(uir.getUserrole());
		}
		return results;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "account")
	public Set<UserInRole> getUserInRoles() {
		return this.userInRoles;
	}

	public void setUserInRoles(Set<UserInRole> userInRoles) {
		this.userInRoles = userInRoles;
	}

	@Transient
	public String getFullName() {
		return getFirstname() + ' ' + getLastname();
	}

}
