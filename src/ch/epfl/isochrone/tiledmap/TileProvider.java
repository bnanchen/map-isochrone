package ch.epfl.isochrone.tiledmap;


/**
 * Interface représentant un fournisseur de tuile.
 * 
 * @author Brunner Loïc (235901)
 * @author Nanchen Bastian (237273)
 *
 */
public interface TileProvider {
    
    /**
     * Méthode permettant de fournir la tuile représentée par les le niveau de zoom et les coordonnées(x,y) mises en arguments.
     * 
     @param zoom
     *        : entier (int) représentant le niveau de zoom.
     * @param x
     *        : entier (int) représentant le paramètre de coordonnée x.
     * @param y
     *        : entier (int) représentant le paramètre de coordonnée y.
     *        
     *        
     * @return Tile: une tuile de la carte. 
     * 
     * 
     */
    public Tile tileAt(int zoom, int x, int y);
}
