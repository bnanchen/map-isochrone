package ch.epfl.isochrone.tiledmap;

public abstract class FilteringTileProvider implements TileProvider {
    abstract public int transformARGB(int argb);
}
