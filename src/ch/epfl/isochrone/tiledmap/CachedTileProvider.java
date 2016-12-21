package ch.epfl.isochrone.tiledmap;

/**
 * Classe, implémentant l'interface TileProvider, qui est un transformateur de fournisseur de tuiles qui garde en mémoire un certain nombre de tuiles afin d'accélérer leur obtention. 
 * 
 * 
 * @author Brunner Loïc (235901)
 * @author Nanchen Bastian (237273)
 *
 */

public class CachedTileProvider implements TileProvider {
    
    private final OSMTileProvider provider;
    private TileCache cache = new TileCache();
    
    /**
     * Constructeur principal de la classe CachedTileProvider. 
     * 
     * @param provider
     *        : (OSMTileProvider) le fournisseur de tuiles. 
     *        
     *        
     */
    
    public CachedTileProvider(OSMTileProvider provider){
        this.provider = provider;
    }
    

    @Override
    public Tile tileAt(int zoom, int x, int y) {
        if(cache.get(zoom, x, y) != null){
            return cache.get(zoom, x, y);
        } else {
            Tile newTile = this.provider.tileAt(zoom, x, y);
            cache.put(zoom, x, y, newTile);
            
            return newTile;
        }
        
    }
    

}
