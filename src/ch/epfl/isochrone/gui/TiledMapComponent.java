package ch.epfl.isochrone.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import ch.epfl.isochrone.tiledmap.*;



import javax.swing.JComponent;

public final class TiledMapComponent extends JComponent   {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 
     */
    
    private int zoom;
    private int positionX =0;
    private int positionY =0;

    private List<TileProvider> listedTileProvider = new ArrayList<TileProvider>();
    public TiledMapComponent(int initialZoom){
        if(initialZoom < 10 || initialZoom > 19){
            throw new IllegalArgumentException("Le zoom "+ initialZoom +" ne se trouve dans l'intervalle [10;19].");
        }

        
        zoom = initialZoom;
        this.setPreferredSize(this.getPreferredSize());
    }
    

    public int zoom(){
        return this.zoom;
    }
    
    public void setIsochronePosition(int x,int y){
        this.positionX = x;
        this.positionY = y;

    }
    
    public void setZoom(int zoom){
        if(zoom < 10 || zoom > 19){
            throw new IllegalArgumentException("Le zoom "+ zoom +" ne se trouve dans l'intervalle [10;19].");
        }
        this.zoom = zoom;
    }
    
    //la fonction doit encore être écrite proprement en fonction de la transparence
    // la gestion du cache se fait depuis l'extérieur
    public void setProviderList(List<TileProvider> tileProvider){

        tileProvider.set(0, new TransparentTileProvider(1,tileProvider.get(0)));
        for(int i=1; i < tileProvider.size(); i++){
            tileProvider.set(i, new TransparentTileProvider(0.5,tileProvider.get(i)));
        }
        

        this.listedTileProvider = tileProvider;
        this.repaint();
    }
    
    @Override
    public Dimension getPreferredSize(){
        
        return new Dimension((int)java.lang.Math.pow(2, zoom + 8),(int)java.lang.Math.pow(2, zoom + 8));
    }
    
        
     
     public void paintComponent(Graphics g0){
            Graphics2D g1 = (Graphics2D) g0;
            Rectangle visibleRect = this.getVisibleRect();

            //il faut définir plus clairement la manière de choisir la position à laquelle on dessine l'image
            
            int positionXFinale = positionX - positionX %256;
            int positionYFinale = positionY - positionY% 256;
            
            for(int i=0 ; i < java.lang.Math.ceil(visibleRect.width/256)+2; i++){
                
                for(int j=0; j < java.lang.Math.ceil(visibleRect.height/256)+2; j++){
                        
                    g1.drawImage(this.listedTileProvider.get(0).tileAt(zoom,positionX/256+i,positionY/256+j).getBufferedImage(), positionXFinale+i*256,positionYFinale+ j*256,this);
                    g1.drawImage(this.listedTileProvider.get(1).tileAt(zoom,positionX/256+i,positionY/256+j).getBufferedImage(), positionXFinale+i*256,positionYFinale+ j*256,this);
                }
            }

            
        }
     
    
                
            
}
