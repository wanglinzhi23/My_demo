package intellif.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import intellif.zoneauthorize.itf.Zone;

@Entity
public class CameraGeometryInfoDto implements Serializable, Zone {

	private static final long serialVersionUID = -2922498032437127358L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
	private String name;
	@JsonIgnore
	@Column(columnDefinition = "Geometry", nullable = true)
	@Type(type = "org.hibernate.spatial.GeometryType")
	protected Geometry geometry;
	protected String geoString;

	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
	    this.geometry = geometry;
	}
	
	public String getGeoString() {
	    return geoString;
	}
	
	public void setGeoString(String geoString) throws ParseException {
		WKTReader wktReader = new WKTReader();
		Geometry geometry = wktReader.read(geoString);//'POINT(-105 40)','POLYGON((-107 39, -102 39, -102 41, -107 41, -107 39))'
	    this.setGeometry(geometry);
	    this.geoString = geoString;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    @Autowired
    public Long zoneId() {
        return id;
    }
}
