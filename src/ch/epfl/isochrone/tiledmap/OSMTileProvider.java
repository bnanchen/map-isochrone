package ch.epfl.isochrone.tiledmap;

import java.awt.image.BufferedImage;
import java.io.IOError;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * Classe représentant un fournisseur de tuiles, qui les obtient depuis un serveur utilisant les conventions de nommage des tuiles du projet OpenStreetMap. 
 * 
 * @author Brunner Loïc (235901)
 * @author Nanchen Bastian (237273)
 * 
 */
public class OSMTileProvider implements TileProvider {
    private final String servorAddress;
    
    /**
     * Constructeur principal de la classe OSMTileProvider. 
     * 
     * @param servorAddress
     *        : (String) représentant l'adresse de base du serveur à utiliser. 
     */
    
    public OSMTileProvider(String servorAddress){
        this.servorAddress = servorAddress;
    }
   
    
    @Override
    
    public Tile tileAt(int zoom, int x, int y) {
        
        BufferedImage buffer = null;
        String address = servorAddress + "/"+ String.valueOf(zoom) + "/"+ String.valueOf(x) +"/"+ String.valueOf(y) +".png";
        try {
            URL url = new URL(address);
            buffer = ImageIO.read(url);
            
            
        } catch (MalformedURLException e) {
            try {
                buffer = ImageIO.read(getClass().getResource("/images/error-tile.png"));
            } catch (IOException e1) {
                throw new IOError(e1);
            }
            
        } catch (IOException e) {
            try {
                buffer = ImageIO.read(getClass().getResource("/images/error-tile.png"));
            } catch (IOException e1) {
                throw new IOError(e1);
            }
            
        }
        
        
        return new Tile(zoom, x, y, buffer);
    }
    

}
