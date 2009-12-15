package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * Userrole - one role a user can be in.
 */
@Entity
@Table(name = "userrole", schema = "public")
public class Userrole implements java.io.Serializable {

	private static final long serialVersionUID = 7297100880725209484L;
	private int id;
	private String name;

	public Userrole() {
	}

	public Userrole(int id) {
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

	@Column(name = "name", length = 8)
	@Length(max = 8)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
