package ch.epfl.isochrone.tiledmap;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

public class TestTile {

    @Test
    public void testTile() throws IOException {
        Tile tile = new Tile(2, 0, 0, ImageIO.read(getClass().getResource("/images/error-tile.png")));
    }

}
