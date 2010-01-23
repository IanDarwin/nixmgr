package model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
public class RestrictedWorkstation implements Serializable
{
	private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer version;
    private String macAddress;
    private String location;

    // add additional entity attributes

    // seam-gen attribute getters/setters with annotations (you probably should edit)

	@Id @GeneratedValue
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Version
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @NotNull @Length(min = 18, max = 18)
    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String name) {
        this.macAddress = name;
    }

    @NotNull @Length(max = 18)
    public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
