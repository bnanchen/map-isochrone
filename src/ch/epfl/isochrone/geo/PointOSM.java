package ch.epfl.isochrone.geo;
import java.lang.Math;

/**             
 *           Classe permettant de représenter un point dans un système de coordonées OSM.
 *              
 *              
 * @author Brunner Loïc (235901)
 * @author Nanchen Bastian (237273)
 */

public final class PointOSM {


    private final int zoom;
    private final double x;
    private final double y;

    /**     Constructeur unique de PointOSM 
     * 
     * @param int zoom
     *          Le niveau de zoom dans lequel on stocke les points
     *          
     * @param double x
     *          Coordonnée x du point
     *          
     * @param double y
     *          Coordonnée y du point 
     * 
     * @throw IllegalArgumentException 
     *          Si le niveau de zoom est négatif, si x,y sont négatifs ou si x,y sont trop grands (+grands que 2^(8+zoom))
     * 
     */
    public PointOSM(int zoom, double x, double y) {


        if (zoom < 0 || x <0 || y< 0|| x > Math.pow(2,8 + zoom)|| y > Math.pow(2, 8 + zoom)){// si x,y not = [0,zoomMax] || zoom < 0
            throw new IllegalArgumentException();
        }  
        this.zoom = zoom;
        this.x = x;
        this.y = y;

    }

    /**
     * 
     * @param int zoom
     *          Niveau de zoom 
     * 
     * @return La taille maximale pour un x ou y dans le niveau de zoom passé en paramêtre
     * 
     * @throw IllegalArgumentException 
     *          Si le zoom est négatif
     */
    public static int maxXY(int zoom){

        if (zoom < 0){
            throw new IllegalArgumentException("zoom négatif");
        }
        return (int)Math.pow(2, 8 + zoom);
    }


    /**
     * @return La coordonée x du point (double)
     */
    public double x() {

        return x;
    }

    /**
     * @return La coordonée y du point (double)
     */
    public double y() {

        return y;
    }

    /** 
     * @return une valeur arrondie de la coordonée x (int)
     */
    public int roundedX() { 

        return Math.round(Math.round(x));
    }

    /** 
     * @return une valeur arrondie de la coordonée y (int)
     */
    public int roundedY() { 

        return Math.round(Math.round(y));
    }

    /**
     * @return le niveau de zomm du point (int)
     * 
     */
    public int zoom() {

        return zoom;
    }

    /**
     * Crée une copie du PointOSM à un autre niveau de zoom 
     * 
     * @param int newZoom
     *          Niveau de zoom de la copie
     * 
     * @throw IllegalArgumentException
     *          Si le zoom passé en parametre est négatif
     *          
     * @return une copie du PointOSM au niveau de zoom passé en parametre
     */
    public PointOSM atZoom(int newZoom) {

        if (newZoom < 0){
            throw new IllegalArgumentException("le zoom est négatif");
        }

        double newX = x*Math.pow(2,newZoom-zoom); 
        double newY = y*Math.pow(2,newZoom-zoom);

        return new PointOSM(newZoom,newX,newY);

    }

    /**
     *      Transforme le PointOSM en un PointWGS84
     * 
     * @return une copie du PointOSM dans un systeme de PointWGS84
     * 
     */
    public PointWGS84 toWGS84(){

        double longitude = (2*Math.PI/Math.pow(2, 8+zoom)*x-Math.PI);
        double latitude = Math.atan(Math.sinh(Math.PI-(2*Math.PI/Math.pow(2, 8+zoom)*y)));
        return new PointWGS84(longitude,latitude);
    }

    /**
     *      Retourne une représentation textuelle du PointOSM 
     * 
     */
    @Override
    public String toString(){

        return "("+ zoom+","+x+","+y+")";
    }

}
