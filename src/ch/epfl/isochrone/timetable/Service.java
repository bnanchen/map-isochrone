package ch.epfl.isochrone.timetable;
import java.util.Set;
import java.util.HashSet;

/**
 * classe Service permettant de modéliser un service.
 * 
 * 
 * @author Brunner Loïc (235901)
 * @author Nanchen Bastian (237273)
 * 
 *
 */

public final class Service {
    private final String name;
    private final Date startingDate;
    private final Date endingDate;
    private final Set<Date.DayOfWeek> operatingDays;
    private final Set<Date> excludedDates;
    private final Set<Date> includedDates;

    /**
     * Constructeur unique de la classe Service. 
     * 
     * 
     * @param String name,
     *        représentant le nom du service.
     * @param Date startingDate,
     *         une date qui représente le début de la validité du service. 
     * @param endingDate,
     *        une date qui représente la fin de la validité du service.
     * @param Set<Date.DayOfWeek> operatingDays,
     *        un set constitué de jours (provenant de l'énumération DayOfWeek de la classe Date) qui sont les jours de la semaine de validité du service. 
     * @param Set <Date> excludedDates,
     *        un set constitué de dates qui sont les dates (exceptions) où il n'y a pas de circulation du service dans l'intervalle de validité du service.
     * @param Set <Date> includedDates,
     *        un set constitué de dates qui sont incluses dans la période de validité du service.
     *        
     * 
     * @throw IllegalArgumentException,
     *        la date de fin du service (Date endingDate) est antérieur à la date de début du service (Date startingDate). 
     * @throw IllegalArgumentException,
     *        les dates exclues (Set <Date> excludedDates) ne sont pas dans l'intervalle de validité du service (intervalle:[Date startingDate; Date endingDate]).       
     * @throw IllegalArgumentException,
     *        les dates inclues (Set <Date> includedDates) ne sont pas dans l'intervalle de validité du service (intervalle:[Date startingDate; Date endingDate]).  
     * @throw IllegalArgumentException,
     *        certaines dates inclues (Set <Date> includedDates) sont aussi des dates exclues (Set <Date> excludedDates).       
     * @throw IllegalArgumentException,
     *        le set des jours de validité du service (Set <Date.DayOfWeek> operatingDays) est vide.
     *        
     *                             
     */

    public Service(String name, Date startingDate, Date endingDate, Set<Date.DayOfWeek> operatingDays, Set<Date> excludedDates, Set<Date> includedDates){
        if(endingDate.compareTo(startingDate) < 0) {
            throw new IllegalArgumentException("la date de fin est antérieur à la date de début.");
        }

        //on vérifie que les Date exclue font partie de la plage de validité
        
        Object [] tableauDate1 = excludedDates.toArray();
        
        for(int i = 0; i < tableauDate1.length; i++) {
            if(((Date)tableauDate1[i]).compareTo(startingDate) < 0 || ((Date)tableauDate1[i]).compareTo(endingDate) > 0) { // modifié après rendu intermédiaire.
                throw new IllegalArgumentException("les dates exclues ne sont pas dans l'intervalle de validité du service.");
            }

        }
        

        // on vérifie que les Dates inclues font partie de l'intervalle de validité du service
        
        Object [] tableauDate2 = includedDates.toArray();
        
        for(int i = 0; i < tableauDate2.length; i++) {
            if(((Date)tableauDate2[i]).compareTo(startingDate) < 0 || ((Date)tableauDate2[i]).compareTo(endingDate) > 0) { // modifié après rendu intermédiaire. 
                throw new IllegalArgumentException("les dates inclues ne sont pas dans l'intervalle de validité du service.");
            }

        }

        // on vérifie que les date inclues ne sont pas en même temps exclues
        
        for(int i = 0; i < tableauDate2.length; i++) {
            
                if(excludedDates.contains(tableauDate2[i])){ //modifié après rendu intermédiaire. 
                    throw new IllegalArgumentException("Certaines dates inclues sont aussi exclues.");
                
            }
        }

        this.name = name;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
        this.operatingDays = new HashSet<Date.DayOfWeek> (operatingDays);
        this.excludedDates = new HashSet<Date> (excludedDates);
        this.includedDates = new HashSet<Date> (includedDates);



    }

    /**
     * 
     * 
     * @return String name,
     *         représentant le nom du service.
     * 
     * 
     */

    public String name() {
        return name;
    }

    /**
     * Méthode retournant un boolean qui est vrai, si le service est opérationnel lors de la date passée en argument. 
     * 
     * 
     * @param Date date
     *        
     * @return boolean isOperating,
     *         qui est vrai si le service est opérationnel lors de la date passée en argument, sinon il est faux. 
     *         
     *         
     */

    public boolean isOperatingOn(Date date){
        boolean isOperating = false;



        if((date.compareTo(startingDate) >= 0 && date.compareTo(endingDate) <= 0) && operatingDays.contains(date.dayOfWeek())){
            return excludedDates.contains(date)? false : true; //modifié après rendu intermédiaire. 
          
        }
        if(includedDates.contains(date)){
            isOperating = true;
        }

        return isOperating;
    }    

    /**
     * 
     * 
     * @return String name,
     *         représentant le nom du service. 
     * 
     * 
     */

    @Override
    public String toString(){

        return name;
    }


    /**
     * La classe Builder est imbriquée statiquement dans la classe Service. Elle permet la construction d'un service par étapes. 
     * 
     * @author Brunner Loïc (235901)
     * @author Nanchen Bastian (237273)
     * 
     *
     */

    public final static class Builder{

        private final String name;
        private final Date startingDate;
        private final Date endingDate;
        private final Set<Date> excludedDate = new HashSet<Date>();
        private final Set<Date> includedDate = new HashSet<Date>();
        private final Set<Date.DayOfWeek> operatingDays= new HashSet<Date.DayOfWeek>();

        /**
         * Constructeur unique de la classe Builder.
         * 
         * @param String name,
         *        représentant le nom du service.
         * @param Date startingDate,
         *        date représentant le début de la validité du service. 
         * @param Date endingDate,
         *        date représentant la fin de la validité du service.
         *        
         * @throw IllegalArgumentException, 
         *        la date de fin du service (Date endingDate) est antérieur à celle de début du service (Date startingDate).       
         * 
         * 
         */

        public Builder(String name, Date startingDate, Date endingDate){
            if(endingDate.compareTo(startingDate) < 0) {
                throw new IllegalArgumentException("la date de fin est antérieure à celle de début.");
            }

            this.name = name;
            this.startingDate = startingDate;
            this.endingDate = endingDate;
        }

        public String name(){
            return this.name;
        }

        public Builder addOperatingDay(Date.DayOfWeek day){
            operatingDays.add(day); 
            return this;
        }

        /**
         * Méthode qui permet d'ajouter une date au set des dates exclues (Set <Date> excludedDates).
         * 
         * @param Date date,
         *        date à ajouter aux dates exclues (Set <Date> excludedDates).
         *        
         * @return Builder,
         *         le même objet avec la dates exclue (Date date) ajoutée au set des dates exclues (Set <Date> excludedDates).
         *         
         *          
         */

        public Builder addExcludedDate(Date date){

            if(startingDate.compareTo(date) > 0 || endingDate.compareTo(date) < 0 ){
                throw new IllegalArgumentException("la date passée en argument n'est pas dans l'intervalle de validité.");
            }

            if(includedDate.contains(date)){
                throw new IllegalArgumentException("la date fait parti des dates inclues.");
            }

            excludedDate.add(date);

            return this;
        }

        /**
         * Méthode qui permet d'ajouter une date au set des dates incluses (Set <Date> includedDates).
         * 
         * @param Date date,
         *        date à ajouter aux dates incluses (Set <Date> includedDates.
         *        
         * @return Builder,
         *          le même objet avec la date incluse (Date date) ajoutée au set des dates incluses (Set <Date> includedDates).
         *          
         *          
         */

        public Builder addIncludedDate(Date date){

            if(startingDate.compareTo(date) > 0 || endingDate.compareTo(date) < 0 ){
                throw new IllegalArgumentException("la date passée en argument n'est pas dans l'intervalle de validité.");
            }

            if(excludedDate.contains(date)){
                throw new IllegalArgumentException("la date fait parti des dates exclues.");
            }

            includedDate.add(date);

            return this;
        }

        /**
         * Méthode qui permet de construire un nouveau service avec le nom, la plage de validité, les jours de circulation et les exceptions ajoutées jusqu'ici au Builder.
         * 
         * @return Service,
         *          un nouveau service.
         *          
         *          
         */

        public Service build(){

            return new Service( name, startingDate, endingDate, operatingDays, excludedDate, includedDate);
        }
    }
}

