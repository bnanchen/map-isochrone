package ch.epfl.isochrone.tiledmap;

import java.awt.image.BufferedImage;

import ch.epfl.isochrone.math.Math;

public final class TransparentTileProvider extends FilteringTileProvider {
    private final double opacity;
    private TileProvider provider;
    
    
    public TransparentTileProvider(double opacity, TileProvider tileProvider){
        if(opacity < 0.0 || opacity > 1.0){
            throw new IllegalArgumentException("Le nombre de l'opacité "+ opacity +" n'est pas contenu dans l'intervalle [0;1]");
        }
        this.opacity = opacity;
        this.provider = tileProvider;
    }
    
    @Override
    public int transformARGB(int argb){
        //System.out.println(opacity);
        
        //double a = ((argb/ java.lang.Math.pow(2, 24))% java.lang.Math.pow(2, 8));
        
        //System.out.println(a);
        //argb = (int) java.lang.Math.abs(argb-((java.lang.Math.pow(2, 24) * java.lang.Math.round(255* a))));
        
        //argb = (int) (argb-a);
        
        /*
        double rgb =  (Math.modF(argb, (int)java.lang.Math.pow(2, 24)));
        
        argb = (int)((java.lang.Math.pow(2, 24) * java.lang.Math.round(255* opacity)) + rgb); 
        System.out.println(argb);
        */
        
        return 0x1000000 *(int)java.lang.Math.round(opacity*0xFF)+Math.modF(argb,0x1000000);
    }

    @Override
    public Tile tileAt(int zoom, int x, int y) {
        
        BufferedImage image = provider.tileAt(zoom, x, y).getBufferedImage();
        BufferedImage newImage = new BufferedImage(256,256,BufferedImage.TYPE_INT_ARGB);
        
        for(int i=0; i < 256;i++){
            for(int j =0; j < 256; j++){
                
                newImage.setRGB(i, j, this.transformARGB(image.getRGB(i, j)));

            }
        }
        
        
        return new Tile(zoom,x,y,newImage);
    }
    
}
