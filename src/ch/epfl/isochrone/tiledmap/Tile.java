package ch.epfl.isochrone.tiledmap;

import java.awt.image.BufferedImage;


/**
 * Classe représentant une tuile de carte.
 * 
 * @author Brunner Loïc (235901)
 * @author Nanchen Bastian (237273)
 *
 */

public final class Tile {
    private int zoom;
    private int x;
    private int y;
    private final BufferedImage picture;
    
    /**
     * Constructeur principal de la classe Tile.
     * 
     * @param zoom
     *        : entier (int) représentant le niveau de zoom.
     * @param x
     *        : entier (int) représentant le paramètre de coordonnée x.
     * @param y
     *        : entier (int) représentant le paramètre de coordonnée y.
     * @param picture
     *        : image (BufferedImage) représentant la tuile de carte. 
     *        
     *        
     */
   
    public Tile(int zoom, int x, int y, BufferedImage picture){
        this.zoom = zoom;
        this.x = x;
        this.y = y;
        this.picture = picture; //nous ne sommes pas certain, mais on pense qu'une image ne peut pas être modifiée après sa création...tout cela reste à tester!
    }
    
    public BufferedImage getBufferedImage(){
        return this.picture;
    }
   /**
    * 
    * @return int
    *           La coordonée x de la tuile.
    */
    public int getX(){
        return this.x;
    }
    
    /**
     * 
     * @return int
     *          La coordonée y de la tuile.
     */
    public int getY(){
        return this.y;
    }
    
    /**
     * 
     * @return int (Entre 0 et 20)
     *           Le zoom actuel de la tuile.
     */
    public int getZoom(){
        return this.zoom;
    }
    
}
