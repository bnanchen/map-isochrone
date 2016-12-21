package ch.epfl.isochrone.timetable;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;



/**
 * Classe permettant de représenter les graphes de l'horaire.
 * 
 * 
 * @author Brunner Loïc (235901)
 * @author Nanchen Bastian (237273)
 *
 */
public final class Graph {
    final private Set<Stop> stops;
    final private Map<Stop, List<GraphEdge>> outgoingEdges;
    private Map<Stop, Integer> arrivalTime = new HashMap<Stop, Integer>();

    private Graph(Set<Stop> stops, Map<Stop, List<GraphEdge>> outgoingEdges){
        this.stops = new HashSet<Stop>(stops);
        this.outgoingEdges = new HashMap<Stop, List<GraphEdge>>(outgoingEdges);
        
        Object[] tabStops = this.stops.toArray();
        
        for(int i = 0; i < tabStops.length; i++){
            arrivalTime.put((Stop)tabStops[i], SecondsPastMidnight.INFINITE);
        }
    }
    /**
     *      Méthode permettant de retourner le chemin le plus rapide pour un Stop de Graph.
     * 
     * @param startingStop
     *        : Le Stop depuis lequel on va calculer le chemin le plus rapide.
     * @param departureTime
     *        : L'heure de départ du trajet.
     * @return
     *        : FastestPathTree: chemin le plus rapide depuis le Stop  passé en argument à partir de l'heure passée en argument.
     * 
     */
    public FastestPathTree fastestPaths(Stop startingStop, int departureTime){
        if(departureTime < 0){
            throw new IllegalArgumentException("l'heure de départ est négative");
        }
        if(!this.stops.contains(startingStop)){
            throw new IllegalArgumentException("le Stop passé en argument ne fait pas partie du Graph");
        }

        FastestPathTree.Builder builder = new FastestPathTree.Builder(startingStop, departureTime);
        // la queue de priorité nous permet de stocker les Stop dans l'ordre de leur élément associé dans la table associative
        PriorityQueue<Stop> stops = new PriorityQueue<Stop>(this.stops.size(), new ComparatorStops<Stop>());
        
        Object[] tabStops = this.stops.toArray();
        
        for(int i = 0; i < tabStops.length; i++){
            stops.add((Stop)tabStops[i]);
        }
        //on est obligé d'enlever le stop de la queue puis de le remettre, cela permet de mettre l'ordre à jour
        stops.remove(startingStop);
        arrivalTime.put(startingStop, departureTime);
        stops.add(startingStop);

        while(!(stops.isEmpty())){



            Stop presentStop = (Stop)stops.remove();

            
            departureTime = arrivalTime.get(presentStop);

            if(arrivalTime.get(presentStop) != SecondsPastMidnight.INFINITE){ 
                if(outgoingEdges.containsKey(presentStop)){
                    
                    for(int i = 0; i < outgoingEdges.get(presentStop).size(); i++){
                        Stop nextStop = outgoingEdges.get(presentStop).get(i).destination();

                        // on réccupère l'arrivée la plus rapide en fonction de l'heure de départ
                        int earliestArrivalTime = outgoingEdges.get(presentStop).get(i).earliestArrivalTime(departureTime);
                        // si l'heure d'arrivée la plus rapide est plus rapide que l'heure d'arrivée précédente pour le Stop, on effectue un remplacement
                        if(earliestArrivalTime < arrivalTime.get(nextStop)){
                            arrivalTime.put(nextStop, earliestArrivalTime);
                            builder.setArrivalTime(nextStop, earliestArrivalTime, presentStop);

                            //mise à jour de la queue
                            
                            stops.remove(nextStop);
                            stops.add(nextStop); 
                            

                        }

                    }
                    
                }


            }
            else{//modifié après le rendu intermédiaire
                break;
            }
            


        }



        return builder.build();
    }


    public final static class Builder {

        private final Set<Stop> stops;//c'est une suite de noeuds (= stops). 
        private final Map<Stop, Map<Stop, GraphEdge.Builder>> outgoingEdges = new HashMap<Stop, Map<Stop, GraphEdge.Builder>>();

        /**
         *      Constructeur du builder de la classe Graph, permet de construire un Graph par étapes.
         *      
         * @param Set<Stop> stops:
         *          Set de Stop contenant tous les stops présents dans le Graph à construire.
         */
        public Builder(Set<Stop> stops){ 
           this.stops = new HashSet<Stop>(stops);
        }


        // méthode privée qui permet de réccupérer un GraphEdgeBuilder particulier
        private GraphEdge.Builder getGraphEdgeBuilder(Stop fromStop, Stop toStop){

            if(outgoingEdges.containsKey(fromStop)){

                Map<Stop, GraphEdge.Builder> access = this.outgoingEdges.get(fromStop);
                if (outgoingEdges.get(fromStop).containsKey(toStop)){
                    return access.get(toStop);// Si le GraphEdgeBuilder existe deja, on le retourne

                }
                else {

                    return new GraphEdge.Builder(toStop);// on crée et on retourne un nouveau GraphBuilder entre le fromStop et le ToStop
                }
            }
            else {

                return new GraphEdge.Builder(toStop);// on crée une nouvelle Map ET un nouveau GraphBuilder entre le fromStop et le toStop
            }



        }

        /**
         *      Méthode qui permet d'ajouter un trajet au Graph en construction.
         * 
         * @param fromStop
         *          Stop: Arrêt de départ du trajet.
         *          
         * @param toStop
         *          Stop: Arrêt d'arrivée du trajet.
         *          
         * @param departureTime
         *          int: Heure du départ du trajet. (en secondes après minuit)
         *          
         * @param arrivalTime
         *          int: Heure d'arrivée du trajet. (en secondes après minuit)
         * @throws IllegalArgumentException
         *          Si l'un des arrêt ne fait pas partie de la suite du stop du Graph en construction, si l'une des deux heures est négative ou si le d'arrivée est antiérieure à l'heure de départ.
         *             
         * @return Builder (this)
         *          Retourne le constructeur lui-même pour permettre les appels chaînés.
         */
        public Builder addTripEdge(Stop fromStop, Stop toStop, int departureTime, int arrivalTime){

            if(departureTime < 0 || arrivalTime < 0){
                throw new IllegalArgumentException("soit l'heure de départ soit l'heure d'arrivée est négative.");
            }

            if(departureTime > arrivalTime){
                throw new IllegalArgumentException("L'heure de départ est plus grande que l'heure d'arrivée.");
            }

            if(!(this.stops.contains(toStop))){
                throw new IllegalArgumentException("L'argument toStop n'est pas dans la liste des stops.");
            }

            if(!(this.stops.contains(fromStop))){
                throw new IllegalArgumentException("L'argument fromStop n'est pas dans la liste des stops.");
            }



            GraphEdge.Builder graphConstruction = getGraphEdgeBuilder(fromStop,toStop);// on récuppère un graphBuilder
            graphConstruction.addTrip(departureTime, arrivalTime);

            Map<Stop, GraphEdge.Builder> mapConstruc = new HashMap<Stop, GraphEdge.Builder>();
            if(this.outgoingEdges.containsKey(fromStop)){// si la clé est deja dedans, on récupère le graph Builder
                mapConstruc = this.outgoingEdges.get(fromStop);
            }

            mapConstruc.put(toStop, graphConstruction);// ono ajoute une map 


            this.outgoingEdges.put(fromStop, mapConstruc);// en mettant je pense qu'on écrase ce qu'il y a avant


            return this;
        }


        /**
         *      Méthode qui ajoute un temps de trajet à pied à tous trajets en construction. Il faut appeler cette méthode quand tous les trajets ont été ajoutés au constructeur.
         *      
         * 
         * @param maxWalkingTime
         *          int: Le temps maximal de trajet à pied tolléré. (en secondes)
         *          
         * @param walkingSpeed
         *          int: Vitesse de marche estimée. (en mêtres par seconde)
         *          
         * @throws IllegalArgumentException
         *          Si le temps maximum de marche est négatif ou si la vitesse de marche est négative ou nulle.
         *          
         * @return Graph.Builder (this)
         *          Retourne le constructeur lui-même pour permettre les appels chaînés.
         */
        public Builder addAllWalkEdges(int maxWalkingTime, double walkingSpeed){

            if(maxWalkingTime < 0){
                throw new IllegalArgumentException("La temps maximum de marche est inférieur à zéro.");
            }

            if(walkingSpeed <= 0){
                throw new IllegalArgumentException("La vitesse de marche est négative ou nulle.");
            }

            
            Object[] tableStop1 = stops.toArray();
            

            for(int i = 0; i < tableStop1.length; i++){
                for(int j = i ; j < tableStop1.length; j++){
                    double distance = (((Stop) tableStop1[i]).position()).distanceTo(((Stop) tableStop1[j]).position());
                    int walkingTime = (int)(distance / walkingSpeed);//on cast en int car setWalkingTime() demande un int en argument donc on perd moins d'une seconde (tout ce qu'il y a après la virgule).
                    if(walkingTime > maxWalkingTime){
                        walkingTime = -1;
                    }

                    if(this.outgoingEdges.containsKey((Stop)tableStop1[i])){
                        if(this.outgoingEdges.get((Stop)tableStop1[i]).containsKey((Stop)tableStop1[j])){
                            GraphEdge.Builder graphBuild1 = getGraphEdgeBuilder((Stop)tableStop1[i], (Stop)tableStop1[j]);
                            graphBuild1.setWalkingTime(walkingTime);
                            Map<Stop, GraphEdge.Builder> transition1= this.outgoingEdges.get((Stop)tableStop1[i]);
                            transition1.put((Stop)tableStop1[j], graphBuild1);
                            this.outgoingEdges.put((Stop)tableStop1[i], transition1);// on ajoute à this.outgoingEdge si les deux clés existent
                        }
                        else{
                            GraphEdge.Builder graphBuild1 = new GraphEdge.Builder((Stop)tableStop1[j]);
                            graphBuild1.setWalkingTime(walkingTime);
                            Map<Stop, GraphEdge.Builder> transition1= this.outgoingEdges.get((Stop)tableStop1[i]);
                            transition1.put((Stop)tableStop1[j], graphBuild1);
                            this.outgoingEdges.put((Stop)tableStop1[i], transition1);
                        }

                    }
                    
                    //on ajoute dans les deux sens si le trajet va dans les deux sens 

                    if(this.outgoingEdges.containsKey((Stop)tableStop1[j])){
                        if(this.outgoingEdges.get((Stop)tableStop1[j]).containsKey((Stop)tableStop1[i])){
                            GraphEdge.Builder graphBuild2 = getGraphEdgeBuilder((Stop)tableStop1[j], (Stop)tableStop1[i]);
                            graphBuild2.setWalkingTime(walkingTime);
                            Map<Stop, GraphEdge.Builder> transition2= this.outgoingEdges.get((Stop)tableStop1[j]);
                            transition2.put((Stop)tableStop1[i], graphBuild2);
                            this.outgoingEdges.put((Stop)tableStop1[j], transition2);// on ajoute à this.outgoingEdge si les deux clés existent
                        }
                        else{
                            GraphEdge.Builder graphBuild1 = new GraphEdge.Builder((Stop)tableStop1[i]);
                            graphBuild1.setWalkingTime(walkingTime);
                            Map<Stop, GraphEdge.Builder> transition1= this.outgoingEdges.get((Stop)tableStop1[j]);
                            transition1.put((Stop)tableStop1[i], graphBuild1);
                            this.outgoingEdges.put((Stop)tableStop1[j], transition1);
                        }


                    }
                    
                }
            }

            return this;
        }
        /**
         *         Méthode qui permet de construire un Graph une fois que tous les trajets ont été mis en place.
         *         
         * @return Graph
         *         Le Graph final qui ne peut plus être modifié.
         */
        public Graph build(){

            //on commence par remplir un tableau avec notre Set de Stop
            Stop[] tableStop = new Stop[stops.size()];
            stops.toArray(tableStop); //ajouté après rendu intermédiaire. 
            Map<Stop, List<GraphEdge>> map = new HashMap<Stop ,List<GraphEdge>>();

            for(int i = 0; i < tableStop.length; i++){
                List<GraphEdge> list = new ArrayList<GraphEdge>();
                if(this.outgoingEdges.containsKey(tableStop[i])){
                    for(int j = 0; j < tableStop.length; j++){//on vérifie avec toute la liste d'où le j = 0 et non i+1.


                        if(this.outgoingEdges.get(tableStop[i]).containsKey(tableStop[j])){
                            list.add(getGraphEdgeBuilder(tableStop[i], tableStop[j]).build());
                            //on ajoute a la liste si les deux clés existent
                        }
                    }
                    map.put(tableStop[i], list);// on ajoute la liste et le stop dans notre map
                }
            }
            return new Graph(this.stops, map);
        }
    }

    private class ComparatorStops<Stop> implements java.util.Comparator<Stop>{

        @Override
        public int compare(Stop stop1, Stop stop2) {

        return Integer.compare(arrivalTime.get(stop1), arrivalTime.get(stop2)); //modifié après rendu intermédiaire.

        }

    }

}


