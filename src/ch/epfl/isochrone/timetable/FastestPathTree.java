package ch.epfl.isochrone.timetable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.epfl.isochrone.geo.PointWGS84;

/**
 * Classe permettant de modéliser un arbre (schéma) de trajets les plus rapides. 
 * 
 * 
 *  @author Brunner Loïc (235901)
 *  @author Nanchen Bastian (237273)
 *  
 *  
 */

public final class FastestPathTree {
    private final Stop startingStop;
    private final Map<Stop,Integer> arrivalTime;
    private final Map<Stop, Stop> predecessor;

    /**
     * Constructeur principal de la classe FastestPathTree.
     * 
     * @param startingStop
     *        : arrêt (Stop) représentant un arrêt de départ.
     * @param arrivalTime
     *        : table associative (Map<Stop, Integer>) qui associe un entier (Integer) à un arrêt (Stop).
     * @param predecessor
     *        : table associative (Map<Stop, Stop>) qui associe un arrêt (Stop) à un autre arrêt (Stop), qui est son successeur. 
     *        
     *        
     */

    public FastestPathTree(Stop startingStop, Map<Stop, Integer> arrivalTime, Map<Stop, Stop> predecessor){

        
        
        Set<Stop> setStopArrival = arrivalTime.keySet();
        Set<Stop> setStopPredecessor = predecessor.keySet();
        Set<Stop> setPredecessorTransition = new HashSet<Stop>(setStopPredecessor);
        setPredecessorTransition.add(startingStop);



        if(!setPredecessorTransition.containsAll(setStopArrival) || !setStopArrival.containsAll(setPredecessorTransition)){
            throw new IllegalArgumentException("l'ensemble des clés de la table n'est pas égale à l'ensemble des predecesseurs plus l'arret de départ.");
        }
        this.startingStop = new Stop(startingStop.name(), new PointWGS84(startingStop.position().longitude(), startingStop.position().latitude()));
        this.arrivalTime = new HashMap<Stop, Integer>(arrivalTime);
        this.predecessor = new HashMap<Stop, Stop>(predecessor);



    }

    /**
     * Méthode permettant de trouver l'arrêt de départ.
     * 
     * @return Stop: l'arrêt de départ.
     * 
     * 
     */

    public Stop startingStop(){
        return new Stop(this.startingStop.name(), new PointWGS84(this.startingStop.position().longitude(), this.startingStop.position().latitude()));
    }

    /**
     * Méthode permettant de trouver l'heure de départ.
     * 
     * 
     * @return int: l'heure de départ, qui n'est autre que l'heure de première arrivée à l'arrêt de départ.
     * 
     * 
     */

    public int startingTime(){

        return this.arrivalTime.get(startingStop);
    }

    /**
     * Méthode permettant de retourner l'ensemble des arrêts pour lesquels une heure de première arrivée existe.
     * 
     * @return Set<Stop>: l'ensemble des arrêts pour lesquels une heure de première arrivée existe.
     * 
     * 
     */

    public Set<Stop> stops(){

        //on crée une Set de Stop pour la remplir avec une boucle for
        Object[] stop = arrivalTime.keySet().toArray();
        Set<Stop> finalStop = new HashSet<Stop>();

        

        for(int i = 0; i < arrivalTime.size(); i++){
            if(arrivalTime.get(stop[i]) != null){
                finalStop.add(new Stop(((Stop) stop[i]).name(), new PointWGS84(((Stop) stop[i]).position().longitude(), ((Stop) stop[i]).position().latitude())));
            }
        }
        return finalStop;
    }

    /**
     * Méthode permettant de retourner l'heure d'arrivée à l'arrêt passé en argument.
     * 
     * @param stop
     *        : représentant un arrêt (Stop), dont à va trouver l'heure d'arrivée.
     *
     * @return int: l'heure d'arrivée à l'arrêt donné ou, si l'arrêt donné n'est pas dans la table des heures d'arrivée passée au constructeur, retourne SecondsPastMidnight.INFINITE.
     * 
     * 
     */

    public int arrivalTime(Stop stop){
        if(this.arrivalTime.get(stop) != null){
            return arrivalTime.get(stop);
        } else {
            return SecondsPastMidnight.INFINITE;
        }

    }

    /**
     * Méthode permettant de retourner le chemin pour aller de l'arrêt de départ à celui passé en argument. 
     * 
     * @param stop
     *        : arrêt (Stop) représentant l'arrêt d'arrivée.
     *        
     * @return List<Stop>: le chemin pour aller de l'arrêt de départ à celui passé en argument. 
     * 
     * 
     */

    public List<Stop> pathTo(Stop stop){
        if(!(this.arrivalTime.containsKey(stop))){
            throw new IllegalArgumentException("L'arrêt passé en argument ne fait pas partie des arrivées possibles.");
        }
        

        List<Stop> listStop = new ArrayList<Stop>();
        while(predecessor.containsKey(stop)){
            listStop.add(stop);
            stop = predecessor.get(stop);
        }

        listStop.add(stop);

        List<Stop> finalList = new ArrayList<Stop>();
        for(int i = (listStop.size() - 1); i >= 0; i--){
            finalList.add(listStop.get(i));
        }
        
        
        

        return finalList;
    }

    /**
     * Classe imbriquée de la classe FastestPathTree, servant de batisseur à cette dernière.
     * 
     * @author Brunner Loïc (235901)
     * @author Nanchen Bastian (237273)
     *
     */

    public final static class Builder {
        private Stop startingStop;
        private Map<Stop, Integer> arrivalTime = new HashMap<Stop, Integer>();
        private Map<Stop, Stop> predecessor = new HashMap<Stop, Stop>();

        /**
         * Constructeur principal de la classe Builder imbriquée dans FastestPathTree.
         * 
         * @param startingStop
         *        : arrêt (Stop) représentant un arrêt de départ.
         * @param startingTime
         *        : entier (int) représentant le temps de départ.
         *        
         *        
         */

        public Builder(Stop startingStop, int startingTime){

            if (startingTime < 0){
                throw new IllegalArgumentException("le stratingTime est négatif");
            }
            this.startingStop = new Stop(startingStop.name(), new PointWGS84(startingStop.position().longitude(), startingStop.position().latitude()));
            this.arrivalTime.put(startingStop, startingTime);
        }

        /**
         * Méthode permettant de de définir l'heure de première arrivée et le prédecesseur de l'arrêt donné dans l'arbre de construction.
         * 
         * @param stop
         *        : arrêt (Stop).
         * @param time
         *        : entier (int) représentant l'heure de première arrivée à l'arrêt stop (Stop).
         * @param predecessor
         *        : arrêt (Stop) représentant le prédecesseur de l'arrêt stop (Stop) passé en argument.
         *        
         * @return Builder: ajoute un élément dans la table associative arrivalTime et la table associative predecessor.
         * 
         * 
         */

        public Builder setArrivalTime(Stop stop, int time, Stop predecessor){
            if (time < this.arrivalTime.get(startingStop)){
                throw new IllegalArgumentException("l'heure donnée est antérieure à l'heure de départ.");
            }
            this.predecessor.put(new Stop(stop.name(), new PointWGS84(stop.position().longitude(), stop.position().latitude())), new Stop(predecessor.name(), new PointWGS84(predecessor.position().longitude(), predecessor.position().latitude())));
            this.arrivalTime.put(stop, time);
            return this;
        }

        /**
         * Méthode permettant de retourner l'heure de première arrivée à l'arrêt donné, ou SecondsPastMidnight.INFINITE si aucune heure d'arrivée n'a été attribuée à cet arrêt jusqu'ici.
         * 
         * @param stop
         *       : arrêt auquel on "chercher" l'heure de première arrivée.
         *       
         * @return int: l'heure de première arrivée à l'arrêt donné ou ou SecondsPastMidnight.INFINITE si aucune heure d'arrivée n'a été attribuée à cet arrêt jusqu'ici.
         * 
         * 
         */

        public int arrivalTime(Stop stop){
            if(arrivalTime.keySet().contains(stop)){
                if(arrivalTime.containsKey(stop)){
                    return arrivalTime.get(stop);
                }
                else{
                    return SecondsPastMidnight.INFINITE;
                }

            } else {
                return SecondsPastMidnight.INFINITE;
            }

        }

        /**
         * Méthode permettant de construire un FastestPathTree.
         * 
         * @return un nouveau FastestPathTree.
         */

        public FastestPathTree build(){

            return new FastestPathTree(this.startingStop, this.arrivalTime, this.predecessor);
        }

    }

}
