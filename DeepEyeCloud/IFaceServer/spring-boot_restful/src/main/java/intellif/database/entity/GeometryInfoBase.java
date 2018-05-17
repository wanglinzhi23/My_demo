package intellif.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.hibernate.annotations.Type;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Created by yangboz on 11/28/15.
 */
@MappedSuperclass
public class GeometryInfoBase extends InfoBase implements Cloneable, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 7768896068288916004L;
    //
    @JsonIgnore
//    @Transient
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
/*    	 if(null != geoString){
    		 WKTReader wktReader = new WKTReader();
    		 Geometry geometry = wktReader.read(geoString);//'POINT(-105 40)','POLYGON((-107 39, -102 39, -102 41, -107 41, -107 39))'
    		 this.setGeometry(geometry);
    	 }*/
        //@see: https://thespatialperspective.wordpress.com/2015/06/20/spring-boot-jpa-and-hibernate-spatial/#comment-18
        this.geoString = geoString;
    }
}
