package model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;

/**
 * One entry in the White List file.
 */
@Entity
@Table(name = "whitelist", schema = "public")
public class Whitelist implements java.io.Serializable {

	private static final long serialVersionUID = 29921238972178984L;
	private int id;
	private String name;

	public Whitelist() {
	}

	public Whitelist(int id) {
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

	@Column(name = "name", length = 255)
	@Length(max = 255)
	@Pattern(regex="[\\w\\d.-]+")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
