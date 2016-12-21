package ch.epfl.isochrone.timetable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.io.*;
import java.nio.charset.*;

import ch.epfl.isochrone.geo.PointWGS84;

/**
 * Classe permettant de lire les fichiers contenant les horaires des tl et de les charger en mémoire. 
 * 
 * @author Brunner Loïc (235901)
 * @author Nanchen Bastian (237273)
 * 
 * 
 *
 */

public final class TimeTableReader {
    private final String baseResourceName;
    private Map<String, Service.Builder> services = new HashMap<String, Service.Builder>();
    private Map<String, Stop> stops = new HashMap<String, Stop>();
    private Graph.Builder graph;

    /**
     * Constructeur unique de la classe TimeTableReader, qui construit un lecteur d'horaire.
     * 
     * 
     * @param baseResourceName
     *        : une chaine de caractères (String) contenant le nom de la source des fichiers contenant les horaires des tl (par exemple: "/time-table/").
     *        
     * @throws IOException
     * 
     * 
     */

    public TimeTableReader(String baseResourceName) throws IOException{

        this.baseResourceName = baseResourceName;
        // on crée toutes les méthodes dans le constructeur ce qui permet de ne faire qu'une fois les opérations de lecture
        try {
            readStops();
        } catch (IOException e) {
            throw e;
        }
        try {
            readCalendar();
        } catch (IOException e){
            throw e;
        }
        try {
            readCalendarDates();
        } catch (IOException e) {
            throw e;
        }
        try {
            readStopTimes();
        } catch (IOException e) {
            throw e;
        }


    }



    private void readCalendarDates() throws IOException{
        InputStream stopsStream = getClass().getResourceAsStream(baseResourceName + "calendar_dates.csv");
        BufferedReader reader = new BufferedReader( new InputStreamReader(stopsStream, StandardCharsets.UTF_8));
        String line;
        try {
            while((line = reader.readLine()) != null){
                String[] text = line.split(";");

                // on fait les calculs de transformation en mois/ jour/ année
                int numberStartDate = Integer.parseInt(text[1]);
                int year = (numberStartDate / 10000);
                numberStartDate = numberStartDate - (10000 * year);
                int month = (numberStartDate / 100);
                numberStartDate = numberStartDate - (100 * month);
                int day = numberStartDate;
                // on crée une Date
                Date exception = new Date(day, month, year);
                
                // on ajoute les included/ excluded Date
                if(Integer.parseInt(text[2]) == 1){
                    services.get(text[0]).addIncludedDate(exception);
                } else {
                    services.get(text[0]).addExcludedDate(exception);
                }



            }     
        } catch (IOException e) {
            throw new IOException("exception dans le readCalendarDates.");
        }
        reader.close();

    }


    private void readCalendar() throws IOException{
        InputStream stopsStream = getClass().getResourceAsStream(baseResourceName + "calendar.csv");
        BufferedReader reader = new BufferedReader( new InputStreamReader(stopsStream, StandardCharsets.UTF_8));
        String line;
        try {
            while((line = reader.readLine()) != null){
                String[] text = line.split(";");

                // on crée les jours/ mois/ années de la date de début
                int numberStartDate = Integer.parseInt(text[8]);
                int year = (numberStartDate / 10000);
                numberStartDate = numberStartDate - (10000 * year);
                int month = (numberStartDate / 100);
                numberStartDate = numberStartDate - (100 * month);
                int day = numberStartDate;
                Date start = new Date(day, month, year);

                // on crée les jours/ mois/ années de la date de fin
                int numberEndDate = Integer.parseInt(text[9]);
                year = (numberEndDate / 10000);
                numberEndDate = numberEndDate - (10000 * year);
                month = (numberEndDate / 100);
                numberEndDate = numberEndDate - (100 * month);
                day = numberEndDate;
                Date fin = new Date(day, month, year);

                Service.Builder calendar = new Service.Builder(text[0], start, fin);

                // ajout des OperatingDays
                for(int i = 1; i < 8; i++){
                    if(Integer.parseInt(text[i]) == 1){
                        
                        calendar.addOperatingDay(Date.DayOfWeek.values()[i-1]);//amélioré après rendu intermédiaire.   
                    }
                }
                services.put(text[0], calendar);


            }     
        } catch (IOException e) {
            throw new IOException("exception dans le readCalendar.");
        }
        reader.close();


    }

    private void readStops() throws IOException{
        InputStream stopsStream = getClass().getResourceAsStream(baseResourceName +"stops.csv");
        BufferedReader reader = new BufferedReader( new InputStreamReader(stopsStream, StandardCharsets.UTF_8));
        String line;
        try {
            while((line = reader.readLine()) != null){
                String[] text = line.split(";");

                // il faut transformer les angles en radian pour les utiliser dans les Stops
                double longitude = (Math.PI * ((Double.parseDouble(text[2])) / 180));
                double latitude = (Math.PI * ((Double.parseDouble(text[1])) / 180));
                stops.put(text[0], new Stop(text[0], new PointWGS84(longitude, latitude)));

            }   
        } catch (IOException e) {
            throw new IOException("exception dans le readStops.");
        }
        reader.close();

    }

    private void readStopTimes() throws IOException{
        InputStream stopsStream = getClass().getResourceAsStream(baseResourceName +"stop_times.csv");
        BufferedReader reader = new BufferedReader( new InputStreamReader(stopsStream, StandardCharsets.UTF_8));
        String line;
        try {

            // on crée un tableau contenant toutes les clés 
            Object[] indermediateTable = stops.keySet().toArray();
            String[] keyTable = new String[indermediateTable.length];
            for(int i = 0; i < keyTable.length; i++){
                keyTable[i] = (String)(indermediateTable[i]);
            }

            // on réccupère tous les stops de la table 
            Set<Stop> setStops = new HashSet<Stop>();
            for(int i = 0; i < keyTable.length; i++){
                setStops.add(stops.get(keyTable[i]));
            }
            Graph.Builder graph = new Graph.Builder(setStops);
            while((line = reader.readLine()) != null){
                String[] text = line.split(";");
                // on ajoute les trajets
                graph.addTripEdge(stops.get(text[1]), stops.get(text[3]), Integer.parseInt(text[2]), Integer.parseInt(text[4]));

            }   
            this.graph = graph;
        } catch (IOException e) {
            throw new IOException("exception dans le readStopTimes.");
        }
        reader.close();
    }

    /**
     * Méthode permettant de créer un horaire (TimeTable) à partir des fichiers donnés à lire.
     * 
     * @return TimeTable: l'horaire sous forme de TimeTable contenu dans les fichiers à lire. 
     */

    public TimeTable readTimeTable(){
        // on va utiliser le builder pour créer notre TimeTable
        TimeTable.Builder horaire = new TimeTable.Builder();
        Object[] indermediateStopTable = stops.keySet().toArray();
        String[] keyStopTable = new String[indermediateStopTable.length];

        // on réccupère les clés dans un tableau
        for(int i = 0; i < keyStopTable.length; i++){
            keyStopTable[i] = (String)(indermediateStopTable[i]);
        }

        // on ajoute tous les Stop dans le TimeTable
        for(int i = 0; i < keyStopTable.length; i++){
            horaire.addStop(stops.get(keyStopTable[i]));
        }

        Object[] indermediateServicesTable = services.keySet().toArray();
        String[] keyServicesTable = new String[indermediateServicesTable.length];

        // on réccupère les clés de services
        for(int i = 0; i < keyServicesTable.length; i++){
            keyServicesTable[i] = (String)(indermediateServicesTable[i]);
        }

        // on ajoute tous les services au TimeTable en les construisant
        for(int i = 0; i < keyServicesTable.length; i++){
            horaire.addService(services.get(keyServicesTable[i]).build());
        }

        return horaire.build();
    }

    /**
     * Méthode permettant de créer un un Graph (en passant par Graph.Builder) à partir des fichiers donnés à lire.
     * 
     * @param stops
     *        : un Set d'arrêts (Set<Stop>). 
     * @param services
     *        : un set de services (Set<Service>).
     * @param walkingTime
     *        : un entier (int) représentant le temps de marche.
     * @param walkingSpeed
     *        : un entier (int) représentant la vitesse de marche.
     *        
     * @return Graph: un nouveau Graph crée à partir des fichiers donnés à lire.
     * 
     * 
     */

    public Graph readGraphForServices(Set<Stop> stops, Set<Service> services, int walkingTime, double walkingSpeed){
        graph.addAllWalkEdges(walkingTime, walkingSpeed);
        // on construit le Graph
        return graph.build();
    }



}
