package ch.epfl.isochrone.geo;
import java.lang.Math;

/**
 *  Classe permettant de représenter un point dans un système de coordonnée WGS84.
 * 
 * @author Brunner Loïc (235901)
 * @author Nanchen Bastian (237273)
 * 
 */
public final class PointWGS84 {

    private final double longitude;
    private final double latitude;
    private final static int EARTH_RADIUS = 6378137; //ajouté après rendu intermédiaire. 

    /**
     *      unique constructeur du PointWGS84
     *
     *@param double lo
     *      la longitude du point
     *
     *@param double la 
     *      la latitude du point 
     *
     *@throw IllegalArgumentException 
     *      si la longitude n'est pas comprise entre [-PI,PI] ou si la latitude n'est pas comprise entre [-PI/2,PI/2]
     * 
     */
    public PointWGS84(double lo, double la) {

        longitude = lo;
        latitude = la;


        if ((lo > Math.PI || lo < -Math.PI) || (la < (-Math.PI / 2)) || (la > (Math.PI / 2))){ //lo not = [-pi, pi] et la not = [-pi/2; pi/2]
            throw new IllegalArgumentException("longitude ou latitude n'est pas dans son intervalle");
        }
    }

    /**
     *        constructeur de copie du PointWGS84
     *        
     * @param PointWGS84 point
     *        point à recopier
     */
    public PointWGS84(PointWGS84 point){
        longitude = point.longitude();
        latitude = point.latitude();
    }

    /**
     * @return la longitude du point dans un système WGS84
     */
    public double longitude() {

        return longitude;
    }

    /**
     * @return la latitude du point dans un système WGS84
     */
    public double latitude() {

        return latitude;
    }

    /**
     *          Permet de connaitre la distance entre deux points dans un système de coordonnée WGS84
     * 
     * @param PointWGS84 that
     *          Prends un autre point en paramêtre
     * 
     * @return La distance entre le point passé en paramêtre et le point actuel (double)
     */
    public double distanceTo( PointWGS84 that) {


        return 2 * EARTH_RADIUS * Math.asin(Math.sqrt(ch.epfl.isochrone.math.Math.haversin(latitude-that.latitude)+Math.cos(latitude)*Math.cos(that.latitude)*ch.epfl.isochrone.math.Math.haversin(longitude-that.longitude))) ; 
    }

    /**
     *      Permet de transformer le point actuel dans un système de coordonnée OSM 
     *      
     * @param int zoom
     *      Le zoom du point OSM sorti par la fonction
     * 
     * @throw IllegalArgumentException 
     *      Si le niveau de zoom est négatif
     * 
     * @return Un point dans le système de coordonnée OSM (PointOSM)
     */
    public PointOSM toOSM(int zoom) {

        if(zoom < 0) {
            throw new IllegalArgumentException("Le zoom est négatif");
        }

        double x = Math.pow(2, 8+zoom)/(2*Math.PI)*(longitude+Math.PI);
        double y = Math.pow(2, 8+zoom)/(2*Math.PI)*(Math.PI-ch.epfl.isochrone.math.Math.asinh(Math.tan(latitude)));

        return new PointOSM(zoom, x,y);
    }

    /**
     * @return une représentation textuelle du PointWGS84
     */
    @Override
    public String toString() {


        return "("+ 180*longitude/ Math.PI +","+ 180*latitude/ Math.PI +")"; 
    }

}
