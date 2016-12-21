package ch.epfl.isochrone.timetable;



import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Classe permettant de représenter les arcs du graphe de l'horaire.
 * 
 * 
 * @author Brunner Loïc (235901)
 * @author Nanchen Bastian (237273)
 *
 */

public final class GraphEdge {


    private final Stop destination;
    private final int walkingTime;
    private final Object[] packedTrips;

    /**
     * Constructeur principal de la classe GraphEdge.
     * 
     * 
     * @param Stop destination:
     *        stop représentant un arrêt qui est la destination de l'arc du graphe. 
     * @param int walkingTime:
     *        entier représentant le temps de marche jusqu'à la destination (Stop destination).
     * @param Set<Integer> packedTrips: 
     *        set d'entier représentant l'heure du départ du bus (en secondes après minuit) multipliée par 10000 + la durée du voyage (en secondes).
     * 
     */


    protected GraphEdge(Stop destination, int walkingTime, Set<Integer> packedTrips){//améliorer après rendu intermédiaire. 

        if(walkingTime < -1){
            throw new IllegalArgumentException("walkingTime est invalide (<-1)");
        }

        this.destination = destination; //amélioré après rendu intermédiaire. 
        this.walkingTime = walkingTime;

        // on remplis un tableau avec tous les voyages possibles
        Object[] tableau = packedTrips.toArray();
        
        this.packedTrips = tableau;

    }  


    /**
     * Méthode permettant d'obtenir l'arrêt (Stop) de destination de l'arc.
     * 
     * @return Stop: l'arrêt de destination de l'arc.
     * 
     * 
     */

    public Stop destination(){//garantir immuabilité
        return this.destination; //amélioré après rendu intermédiaire. 
    }

    /**
     * Méthode permettant de trouver la première heure d'arrivée possible à la destination, étant donné l'heure de départ. 
     * 
     * 
     * @param int departureTime: 
     *        entier représentant l'heure de départ (en secondes après minuit).
     *        
     * @return int 
     *         entier représentant la première heure d'arrivée possible à la destination, étant donné l'heure de départ.
     */

    public int earliestArrivalTime(int departureTime){
      //méthode complètement modifiée après le rendu intermédiaire
        
        
        int[]departureTable = new int[packedTrips.length];
        Map<Integer, Integer> arrivalMap = new HashMap<Integer, Integer>();
        
        
        for(int i = 0; i < departureTable.length; i++){
            departureTable[i] = unpackTripDepartureTime((int)packedTrips[i]);
            // quand on a un doublon, on prend que le meilleur
            if(!arrivalMap.containsKey(departureTable[i])){
            arrivalMap.put(departureTable[i], unpackTripArrivalTime((int)packedTrips[i]));
            }
            else if(arrivalMap.get(departureTable[i]) > unpackTripArrivalTime((int)packedTrips[i])){
                arrivalMap.put(departureTable[i], unpackTripArrivalTime((int)packedTrips[i]));
            }
        }
        Arrays.sort(departureTable);
        
        boolean bool = true;
        for(int i = 0; i < departureTable.length; i++){
            if(departureTable[i] > departureTime){
                bool = false;
                break;
            }
        }
        if((walkingTime == -1 && bool)){
            return SecondsPastMidnight.INFINITE;
        } else {
            int walkArrivalTime = walkingTime + departureTime;
            
            int departurePosition = Arrays.binarySearch(departureTable, departureTime);
            
            if(departurePosition < 0){
                departurePosition = - departurePosition - 1;
            }
            
            int bestArrival = 0;
            
            if(departurePosition == departureTable.length){
                bestArrival = SecondsPastMidnight.INFINITE;
            } else {
                bestArrival = arrivalMap.get(departureTable[departurePosition]);
                }
        

            if(walkingTime == -1){

                return bestArrival;
            }else if(bestArrival < walkArrivalTime){

                return bestArrival;
            } else {

                return walkArrivalTime;
            }
            
        }
        
        
        
        /*
        boolean bool = true;
        
        int arrivalWalking = walkingTime + departureTime;
        for(int i=0; i< packedTrips.length;i++){//vérif si l'heure de départ est plus petit qu'un moins une des heures du tableau
            if(departureTime <= packedTrips[i]){
                bool = false;
            }
        }

        if (walkingTime == -1 || bool || arrivalWalking  > SecondsPastMidnight.INFINITE){
            arrivalWalking = SecondsPastMidnight.INFINITE;
           
        }

            int best = 0;

            int [] departure = new int[packedTrips.length];


            // on dépack tous les départs possibles 
            for(int i = 0; i < packedTrips.length; i++){
                departure[i] = unpackTripDepartureTime(packedTrips[i]);
            }
            int [] arrival = new int[packedTrips.length];

            // on dépack toutes les arrivées possibles
            for(int i = 0; i < packedTrips.length; i++){
                arrival[i] = unpackTripArrivalTime(packedTrips[i]);
            }
            Map<Integer, Integer>departureArrival = new HashMap<Integer, Integer>();
            for(int i = 0; i < departure.length; i++){
                departureArrival.put(departure[i], arrival[i]);
            }
            Arrays.sort(departure);

            int nextDeparture = Arrays.binarySearch(departure, departureTime);
            
            // nous permet de remettre à 0 si l'index est à -1 (ce qui peut arriver si la prochain départ est avant tous les éléments de la liste
            if (nextDeparture < 0){
                nextDeparture = - nextDeparture-1;
            }
            
            // si le prochain départ est plus grand que tous les prochains départs possible, on ne peut pas le faire en transport en commun
            if(nextDeparture < departure.length){
                int bestBusStation = departureArrival.get(departure[nextDeparture]);
                
                // on cherche le trajet le plus rapide en transport en commun
                for(int i = nextDeparture; i < departure.length; i++){
                    if(departureArrival.get(departure[i]) < bestBusStation){
                        bestBusStation = departureArrival.get(departure[i]);
                    }
                }
                // Si le trajet en transport en commun est plus long que la marche, on le fait à pied
                if(arrivalWalking >= bestBusStation){
                    best = bestBusStation;
                } else {
                    best = arrivalWalking;

                }
                return best;
            }else{
                return SecondsPastMidnight.INFINITE;
            }
            */
           
        }
    




    //méthodes statiques 

    /**
     * Méthode permettant de packer un trajet en bus, c'est à dire: l'heure du départ du bus (en secondes après minuit) multipliée par 10000 + la durée du voyage (en secondes).
     * 
     * 
     * @param int departureTime:
     *        entier représentant l'heure de départ en secondes. 
     * @param int arrivalTime
     *        entier représentant l'heure d'arrivée en secondes.
     *        
     *         
     * @return int
     *        un entier représentant le trajet en bus packé.
     * 
     */
    public static int packTrip(int departureTime, int arrivalTime){

        if(departureTime <0 || departureTime > SecondsPastMidnight.fromHMS(29, 59, 59)){ //modifié après rendu intermédiaire.
            throw new IllegalArgumentException("departureTime n'est pas compris dans [0,107999]");
        }
        if (arrivalTime- departureTime < 0 || arrivalTime - departureTime > 9999){
            throw new IllegalArgumentException("la différence entre departureTime et arrivalTime n'est pas comprise dans [0,9999]");
        }

        return departureTime*10000 + (arrivalTime - departureTime);
    }

    /**
     * Méthode permettant de "sortir" l'heure de départ du bus (en secondes après minuit) d'un nombre packé.
     * 
     * 
     * @param int packedTrip:
     *        entier représentant le trajet d'un bus packé.
     *        
     * @return int
     *         entier représentant l'heure de départ du bus (en secondes après minuit).
     */

    public static int unpackTripDepartureTime(int packedTrip){


        return (packedTrip / 10000); //division avec int
    }

    /**
     * Méthode permettant de "sortir" la durée d'un voyage en bus (en secondes) d'un nombre packé.
     * 
     * 
     * @param int packedTrip:
     *        entier représentant le trajet d'un bus packé.
     *        
     * @return int
     *         entier représentant la durée du trajet en bus (en secondes). 
     */

    public static int unpackTripDuration(int packedTrip){

        return packedTrip % 10000;// reste de la division
    }
    public static int unpackTripArrivalTime(int packedTrip){

        int departure = unpackTripDepartureTime(packedTrip);
        int duration = unpackTripDuration(packedTrip);

        return (departure + duration);
    }

    /**
     * Classe imbriquée de la classe GraphEdge, permettant de construire les arcs du graphe de l'horaire quand on a pas tous les arguments pour le constructeur de la classe GraphEdge.
     * 
     * 
     * @author Brunner Loïc (235901)
     * @author Nanchen Bastian (237273)
     *
     */

    //Builder de la classe 
    public final static class Builder{

        private Stop destination = null;
        private int walkingTime = 0;
        private Set<Integer> packedTrips = new HashSet<Integer>(); 

        /**
         * Constructeur principal de la classe Builder imbriquée 
         * 
         * @param Stop destination:
         *        stop rerpésentant l'arrêt qui est aussi la destination. 
         *        
         *        
         */

        public Builder(Stop destination){

            this.destination = destination;//amélioré après rendu intermédiaire. 
        }

        public GraphEdge.Builder setWalkingTime(int newWalkingTime){

            if(newWalkingTime < -1){
                throw new IllegalArgumentException("newWalkingTime est <-1");
            }

            this.walkingTime = newWalkingTime;

            return this;
        }

        /**
         * Méthode permettant d'ajouter un trajet en bus (un arc du graphe).
         * 
         * @param int departureTime:
         *        entier représentant l'heure de départ (en secondes après minuit).     
         * @param int arrivalTime:
         *        entier représentant l'heure d'arrivée (en secondes après minuit). 
         *        
         * @return GraphEdge.Builder
         *          le GraphEdge.Builder avec en plus le trajet dans le Set<Integer>packedTrips.
         *          
         */

        GraphEdge.Builder addTrip(int departureTime, int arrivalTime){

            if(departureTime <0 || departureTime > SecondsPastMidnight.fromHMS(29, 59, 59)){ //modifié après rendu intermédiaire.
                throw new IllegalArgumentException("departureTime n'est pas compris dans [0,107999]");
            }
            if (arrivalTime- departureTime < 0 || arrivalTime - departureTime > 9999){
                throw new IllegalArgumentException("la différence entre departureTime et arrivalTime n'est pas comprise dans [0,9999]");
            }

            packedTrips.add(GraphEdge.packTrip(departureTime, arrivalTime));

            return this;
        }

        /**
         * Méthode permettant de construire un GraphEdge.
         * 
         * @return un nouveau GraphEdge avec en argument Stop destination, int walkingTime et packedTrips.
         */

        public GraphEdge build(){
            return new GraphEdge(destination, walkingTime, packedTrips);
        }


    }




}
