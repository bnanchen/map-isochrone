package ch.epfl.isochrone;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import ch.epfl.isochrone.geo.PointWGS84;
import ch.epfl.isochrone.timetable.*;

/**
 * 
 *          Classe qui permet de lancer le projet à son étape intermédiaire.
 * 
 * @author Brunner Loïc (235901)
 * @author Nanchen Bastian (237273)
 *
 */
public class TimeTableSearch {

    public static void main(String[] args) throws IOException {
        if(!(args.length == 3)){// on lance l'exception si le tableau n'est pas de la bonne taille
            throw new IllegalArgumentException("le tableau contient"+ args.length +" donc pas 3 éléments.");
        }

        String tableDate [] = args[1].split("-");// on sépare les dates passées en argument
        String tableDeparture [] = args[2].split(":");// on sépare les minutes:heures: secondes passées en argument

        Date startingDate = new Date(Integer.parseInt(tableDate[2]), Integer.parseInt(tableDate[1]), Integer.parseInt(tableDate[0]));
        int departure = (Integer.parseInt(tableDeparture[0]) * 60 * 60) + (Integer.parseInt(tableDeparture[1]) * 60) + Integer.parseInt(tableDeparture[2]);
        TimeTableReader reader = new TimeTableReader("/time-table/");
        // le reader nous permet d'obtenir un Graph et un timeTable
        TimeTable timetable = reader.readTimeTable();
        Graph graph = reader.readGraphForServices(timetable.stops(), timetable.servicesForDate(startingDate), 300, 1.5);
        Stop departureStop = new Stop("void", new PointWGS84(0.0, 0.0));


        for(int i = 0; i < timetable.stops().size(); i++){

            //on fait la boucle jusqu'a ce que le stop aie le même nom que le nom passé en argument puis on sort immédiatement.
            if(timetable.stops().toArray()[i].hashCode() == args[0].hashCode()){
                departureStop = (Stop) timetable.stops().toArray()[i];
                i = timetable.stops().size();//permet de sortir de la boucle for sans continuer des tests inutiles
            }

        }
        FastestPathTree tree = graph.fastestPaths(departureStop, departure);

        
        Set<Stop> stops = tree.stops();
        for(int i = 0; i < stops.size(); i++){
            //boucle qui permet l'affichage de chaque Stop
            System.out.println(stops.toArray()[i] +" : "+ SecondsPastMidnight.toString(tree.arrivalTime((Stop)stops.toArray()[i])));
            System.out.print(" via: [");
            List<Stop> stopsPath = tree.pathTo((Stop)stops.toArray()[i]);
            for(int j = 0; j < (stopsPath.size()-1); j++){
                //boucle qui affice (tous les stop)-1 pathTo du Stop en écriture 
                System.out.print(stopsPath.get(j) +", ");
            }
            // affichage du dernier élément
            System.out.println(stopsPath.get(stopsPath.size()-1) +"]");

        }
        
        
    }

}
