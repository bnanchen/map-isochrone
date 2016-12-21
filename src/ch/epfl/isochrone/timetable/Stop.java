package ch.epfl.isochrone.timetable;
import ch.epfl.isochrone.geo.*;
/**
 * Classe Stop modélise un arrêt nommé et positionné dans l'espace (en PointWGS84).
 * 
 * 
 * @author Brunner Loïc (235901)
 * @author Nanchen Bastian (237273)
 * 
 *
 */

public final class Stop {
    private final String name;
    private final PointWGS84 position;

    /**
     * Constructeur unique de la classe Stop
     * 
     * @param String name
     *        : représentant le nom de l'arrêt.
     * @param PointWGS84 position
     *        : représentant la position dans l'espace de l'arrêt en PointWGS84.
     * 
     * 
     */       

    public Stop(String name, PointWGS84 position){
        this.name = name;
        this.position = position;
    }

    /**
     * 
     * @return String name
     *         : représentant le nom de l'arrêt.
     *         
     */

    public String name() {
        return name;
    }

    /**
     * 
     * @return PointWGS84 position
     *         : représentant la position de l'arrêt en PointWGS84.
     *         
     */

    public PointWGS84 position() {
        return new PointWGS84(position);//on a crée un constructeur de copie PointWGS84 pour faire une copie profonde
    }

    /**
     * 
     * @return String
     *         : réprésentant le nom de l'arrêt.
     */

    @Override
    public String toString() {

        return name;
    }

    /**
     *      Méthode retournant un hashCode de l'objet (en fonction de son nom et de sa position)
     */
   @Override
    public int hashCode(){

        return this.name.hashCode() + (int)(this.position().latitude()) + (int)(this.position().longitude());
    }

    /**
     *      Méthode vérifiant l'égalité entre deux Stops
     */
    
    @Override
    public boolean equals(Object stop){
        if(stop instanceof Stop && this.hashCode() == stop.hashCode()){
            return true;
        } else{
            return false;
        }

    }
    
}
