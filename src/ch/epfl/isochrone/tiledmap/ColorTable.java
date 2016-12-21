package ch.epfl.isochrone.tiledmap;

import java.awt.Color;
import java.util.List;

/**
 * Classe permettant de modéliser une table de couleurs à utiliser pour dessiner une carte isochronique. 
 * 
 * @author Brunner Loïc (235901)
 * @author Nanchen Bastian (237273)
 * 
 *
 */

public final class ColorTable {
    
    private final int secondsLayer;
    private final List<Color> list;
    
    /**
     * Constructeur principal de la classe.
     * 
     * @param secondsLayer
     *        : (int) entier représentant la tranche de temps (en secondes) entre chaque changement de couche de couleur. 
     * @param list
     *        : (List<Color>) une liste de couleurs représentant chaque couche de couleurs dans un l'ordre de la couleur correspondant au temps le plus petit à la couleur correspondant au temps le plus grand.
     *        
     *          
     */
    
    public ColorTable(int secondsLayer, List<Color> list){
        
        if(list.isEmpty()){
            throw new IllegalArgumentException("la liste ne contient aucune couleur.");
        }
        this.secondsLayer = secondsLayer;
        this.list = list; 
    }
    
    /**
     * Méthode permettant d'obtenir la durée (en secondes) de chaque tranche. 
     * 
     * @return int: un entier représentant la durée en secondes. 
     * 
     * 
     */
    
    public int duration(){
        return this.secondsLayer;
    }
    
    /**
     * Méthode permettant d'obtenir le nombre de tranches.
     * 
     * @return int: un entier représentant le nombre de tranches. 
     * 
     * 
     */
    
    public int numberOfLayers(){
        return this.list.size();
    }
    
    /**
     * Méthode permettant d'obtenir la couleur de la tranche. 
     * 
     * @param layer
     *        : (int) un entier représentant la tranche dont on veut connaitre la couleur associée.
     *         
     * @return Color: la couleur associée à la tranche passée en argument.
     * 
     * 
     */
    
    public Color getColor(int layer){
        return this.list.get(layer);
    }
    
    

}
