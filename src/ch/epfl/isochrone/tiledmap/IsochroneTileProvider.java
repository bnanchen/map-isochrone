package ch.epfl.isochrone.tiledmap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import java.util.List;


import ch.epfl.isochrone.geo.PointOSM;
import ch.epfl.isochrone.timetable.FastestPathTree;
import ch.epfl.isochrone.timetable.Stop;

/**
 * Classe implémentant TileProvider permettant de fournir les tuiles pour une carte isochronique. 
 * 
 * @author Brunner Loïc (235901)
 * @author Nanchen Bastian (237273)
 * 
 *
 */

public final class IsochroneTileProvider implements TileProvider {

    private final int EARTH_CIRCUMFERENCE = 40075;
    private final FastestPathTree fastestPath;
    List<Color> table;
    private final double walkingSpeed;
    private static int layerSeconds = 300;
    
    /**
     * Constructeur principal de la classe IsochroneTileProvider.
     * 
     * @param fastestPath
     *        : (FastestPathTree) l'arbre des trajets les plus rapides.
     * @param table
     *        : (List<Color>) une liste de couleurs. 
     * @param walkingSpeed
     *        : (int) entier représentant la vitesse de marche à pied,qui est nécessaire au calcul du rayon des disques à dessiner.
     *        
     *        
     */
    
    public IsochroneTileProvider(FastestPathTree fastestPath, List<Color> table, double walkingSpeed){
        
        if(walkingSpeed < 0){
            throw new IllegalArgumentException("la vitesse de marche est négative.");
        }
        this.fastestPath = fastestPath;
        this.table = table;
        this.walkingSpeed = walkingSpeed;
    }

    @Override
    public Tile tileAt(int zoom, int x, int y) {

        BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);//on a remplacé 256 par 512 dans les 2 cas. 
        Graphics2D g = image.createGraphics();
        ColorTable color = new ColorTable(layerSeconds, this.table);
        
        g.setColor(color.getColor(0));
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
              
        for(int i = 1; i < color.numberOfLayers(); i++){
            g.setColor(color.getColor(i));
           
             

            for(Stop stop: fastestPath.stops()){

                double time = layerSeconds * (color.numberOfLayers()-(i)) - (fastestPath.arrivalTime(stop) - fastestPath.startingTime());
                
                double distanceForPixel = this.EARTH_CIRCUMFERENCE * Math.cos((stop).position().latitude())/Math.pow(2,zoom+8);
                double radius = (walkingSpeed*time/Math.pow(2, 19-zoom)); // on a enlevé: /Math.pow(2, 19 - zoom).
                
                double pixelRadius = (radius/distanceForPixel)/2; 
                if(radius > 0){
                PointOSM osm = (stop).position().toOSM(zoom);
                double xCircle = osm.x() - x * 256;
                double yCircle = osm.y() - y * 256;

                g.fill(new Ellipse2D.Double(xCircle - (pixelRadius/2),yCircle - (pixelRadius/2), pixelRadius, pixelRadius));
                
               }

                
            }
            
        }
        
       
        return new Tile(zoom, x ,y, image);
    }
    
    
    
    
}
