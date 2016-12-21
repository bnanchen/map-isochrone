package ch.epfl.isochrone.tiledmap;

import static org.junit.Assert.*;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import ch.epfl.isochrone.geo.PointWGS84;
import ch.epfl.isochrone.timetable.Date;
import ch.epfl.isochrone.timetable.Date.Month;
import ch.epfl.isochrone.timetable.FastestPathTree;
import ch.epfl.isochrone.timetable.Graph;
import ch.epfl.isochrone.timetable.SecondsPastMidnight;
import ch.epfl.isochrone.timetable.Stop;
import ch.epfl.isochrone.timetable.TimeTable;
import ch.epfl.isochrone.timetable.TimeTableReader;

public class TestIsochroneTileProvider {

    @Test
    public void test() throws IOException {
        
        TimeTableReader reader = new TimeTableReader("/time-table/");
        // le reader nous permet d'obtenir un Graph et un timeTable
        TimeTable timetable = reader.readTimeTable();
        Graph graph = reader.readGraphForServices(timetable.stops(), timetable.servicesForDate(new Date(1, Month.OCTOBER, 2013)), 300, 1.5);
        Stop departureStop = new Stop("void", new PointWGS84(0.0, 0.0));


        for(int i = 0; i < timetable.stops().size(); i++){

            //on fait la boucle jusqu'a ce que le stop aie le même nom que le nom passé en argument puis on sort immédiatement.
            if(timetable.stops().toArray()[i].hashCode() == new String("Lausanne-Flon").hashCode()){
                departureStop = (Stop) timetable.stops().toArray()[i];
                i = timetable.stops().size();//permet de sortir de la boucle for sans continuer des tests inutiles
            }

        }
        FastestPathTree tree = graph.fastestPaths(departureStop, SecondsPastMidnight.fromHMS(6,8, 0));

        Set<Stop> stops = tree.stops();
        
       

        List<Color> color = new ArrayList<Color>();
        color.add(Color.black);
        color.add(new Color(0, 0, 128));
        color.add(Color.blue);
        color.add(new Color(0, 255, 255));
        color.add(Color.green);
        color.add(new Color(128, 255, 0));
        color.add(Color.yellow);
        color.add(new Color(255, 128, 0));
        color.add(Color.red);
        
        
        IsochroneTileProvider tile = new IsochroneTileProvider(tree, color, 1);
        tile.tileAt(11,1061,724);
        
    }


}