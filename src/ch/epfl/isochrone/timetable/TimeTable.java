package ch.epfl.isochrone.timetable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

/**
 * La classe TimeTable qui modélise un horaire. 
 * 
 * @author Brunner Loïc (235901)
 * @author Nanchen Bastian (237273)
 * 
 * 
 */

public final class TimeTable {

    private final Set<Stop> stops ;
    private final Collection<Service> services ;

    /**
     * Constructeur unique de la classe TimeTable.
     * 
     * @param Set <Stop> stops,
     *        les arrêts de l'horaire. 
     * @param Set<Service> services,
     *        les services de cet horaire. 
     *        
     *        
     */

    public TimeTable(Set<Stop> stops, Collection <Service> services){
        this.stops = new HashSet<Stop> (stops);
        this.services = new HashSet<Service> (services);
    }

    /**
     * Méthode qui retourne la collection des arrêts du set (Set<Stop> stops).
     * 
     * @return Collections.unmodifiableSet(stops),
     *          la collection des arrêts du set (Set<Stop> stops). 
     *          
     *          
     */

    public Set<Stop> stops(){

        return Collections.unmodifiableSet(stops);
    }

    /**
     * Méthode qui retourne l'ensemble des services actifs lors la date donnée (celle mise en argument: Date date). 
     * 
     * @param Date date,
     *        date durant laquelle la méthode va voir quels services sont actifs.
     *        
     * @return Set<Service> servicesForDate,
     *         un set contenant les services qui sont actifs lors de la date mise en argument (Date date).
     *         
     *         
     */

    public Set<Service> servicesForDate(Date date){

        Set<Service> servicesForDate = new HashSet<Service>();

        Object[] tabServices = services.toArray();
        
        for(int i = 0; i < services.size(); i++){
            if(((Service)tabServices[i]).isOperatingOn(date)){
                servicesForDate.add((Service)tabServices[i]);
            }
        }

        return servicesForDate;

    }

    /**
     * La classe Builder est imbriquée statiquement dans la classe TimeTable. Elle permet la construction d'un horaire par étapes. 
     * 
     * @author Brunner Loïc (235901)
     * @author Nanchen Bastian (237273)
     * 
     * 
     *
     */

    public final static class Builder{
        private final Set<Stop> sto = new HashSet<Stop>();
        private final Collection<Service> serv = new HashSet<Service>();


        /**
         * Méthode qui permet d'ajouter un arrêt (Stop newStop) à l'ensemble des arrêts de l'horaire (Set <Stop> stops).
         * 
         * @param Stop newStop,
         *        arrêt à ajouter à l'ensemble des arrêts de l'horaire (Set<Stop> stops).  
         * @return Builder,
         *         le même objet avec l'arrêt (Stop newStop) ajouté au set des arrêts (Set <Stop> stops).
         *         
         *         
         */

        public Builder addStop(Stop newStop){

            sto.add(newStop);



            return this;
        }

        /**
         * Méthode qui permet d'ajouter une service (Service newService) à l'ensemble des services de l'horaire (Set<Service> services).
         * 
         * @param Service newService,
         *        service à ajouter à l'ensemble des arrêts de l'horaire (Set<Service> services). 
         * @return Builder,
         *         le même objet avec le service (Service newService) ajouté au set des services (Set<Service> services).
         *         
         *         
         */

        public Builder addService(Service newService){

            serv.add(newService);

            return this;
        }

        /**
         *  Méthode qui permet de construire un nouvel horaire (TimeTable) avec les arrêts et services ajoutés jusqu'ici au Builder. 
         * 
         * @return new TimeTable(stops, services)
         *         : un nouvel horaire (TimeTable) construit par rapport au Set de stops et la Collection de services de la classe imbriquée Builder.
         *         
         *         
         */

        public TimeTable build(){
            return new TimeTable(sto, serv);
        }

    }
}
