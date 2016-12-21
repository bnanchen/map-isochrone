package ch.epfl.isochrone.tiledmap;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Classe représentant une table associative associant des tuiles à leurs coordonnées.
 * 
 * @author Brunner Loïc (235901)
 * @author Nanchen Bastian (237273)
 * 
 *
 */

public class TileCache {
    
    private static int MAX = 100;
    private LinkedHashMap<Long, Tile> associativeMap = new LinkedHashMap<Long, Tile>() {
    /**
         * 
         */
        private static final long serialVersionUID = 1L;

    @Override
    protected boolean removeEldestEntry(Map.Entry<Long,Tile> e){
    return size() > MAX;
         }
    };
     
    
    /**
     * Méthode permettant d'ajouter une association entre des tuiles et ses coordonnées (mises en arguments) à la table associative. 
     * 
     * 
     @param zoom
     *        : entier (int) représentant le niveau de zoom.
     * @param x
     *        : entier (int) représentant le paramètre de coordonnée x.
     * @param y
     *        : entier (int) représentant le paramètre de coordonnée y.
     * @param tile
     *        : une tuile de la carte (Tile): 
     *        
     *        
     */
    
    public void put(int zoom, int x, int y, Tile tile){
        long key = (long) (x + (y * Math.pow(10, 7)) + (zoom * Math.pow(10, 14)));
        associativeMap.put(key, tile);
        
    }
    
    /**
     * Méthode permettant d'obtenir la tuile de la carte associées aux coordonnées mises en arguments.
     * 
     * 
     @param zoom
     *        : entier (int) représentant le niveau de zoom.
     * @param x
     *        : entier (int) représentant le paramètre de coordonnée x.
     * @param y
     *        : entier (int) représentant le paramètre de coordonnée y.
     *        
     *        
     * @return Tile: la tuile de la carte associées aux coordonnées mises en arguments. 
     */
    
    public Tile get(int zoom, int x, int y){
        long key = (long) (x + (y * Math.pow(10, 7)) + (zoom * Math.pow(10, 14)));
        return associativeMap.containsKey(key)? associativeMap.get(key) : null;
    }

}
