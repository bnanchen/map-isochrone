package ch.epfl.isochrone.tiledmap;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestTileProvider {

    @Test
    public void testTileProvider() {
       OSMTileProvider provider = new OSMTileProvider("http://a.tile.openstreetmap.org/");
       Tile tile = provider.tileAt(1, 0, 0);
       CachedTileProvider cachedProvider = new CachedTileProvider(provider);
       cachedProvider.tileAt(1, 0, 0);
       cachedProvider.tileAt(1, 0, 0);
       
    }

}
