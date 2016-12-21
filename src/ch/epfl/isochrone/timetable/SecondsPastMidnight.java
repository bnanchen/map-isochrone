package ch.epfl.isochrone.timetable;


/**
 *              Classe qui représente les heures d'un jour
 * 
 * @author Brunner Loïc(235901)
 * @author Nanchen Bastian (237273)
 */

public final class SecondsPastMidnight {

    /**
     * Borne supérieure (secondes après minuit)
     */
    public final static int INFINITE = 200000;
    private final static int UP_LIMIT = 107999;


    private SecondsPastMidnight(){//constucteur privé qui empêche l'instanciation de la classe

    }


    /**     Méthode qui retourne une heure (heure, minute, secondes) en un entier représentant les secondes passées après minuit
     * 
     * @param int hours 
     *          Comprises entre [0,23]
     *         
     * @param int minutes 
     *          Comprises entre [0,59]
     *          
     * @param int seconds 
     *          Comprises entre [0,59]
     * 
     * @throw IllegalArgumentException 
     *          Si les heures ne sont pas dans l'intervalle: [0,23]
     * 
     * @throw IllegalArgumentException
     *          Si les minutes ne sont pas dans l'intervalle: [0,59]
     *          
     * @throw IllegalArgumentException
     *          Si les secondes ne sont pas dans l'intervalle: [0,59]
     * 
     * 
     * @return int 
     *          Représentation de l'heure sous forme de secondes (passée après minuit du même jour)
     * 
     */
    public static int fromHMS(int hours, int minutes, int seconds){



        if (seconds <0 || seconds >= 60){
            throw new IllegalArgumentException("le nombre de secondes est incorrect:"+ seconds);
        }
        if (minutes < 0 || minutes >=60){
            throw new IllegalArgumentException("le nombre de minutes est incorrect:"+minutes);
        }
        if(hours < 0 || hours >=30){
            throw new IllegalArgumentException("le nombre d'heure est incorrect:"+hours);
        }
        return ((60 * 60 * hours) + (60 * minutes) + (seconds));
    }

    /**         Donne les secondes passées après minuit d'un java.util.Date
     * 
     * 
     * @param java.util.Date date
     * 
     * @return int
     *          Les secondes passées après minuit du jour actuel de la java.util.Date
     */
    @SuppressWarnings("deprecation")
    public static int fromJavaDate(java.util.Date date){


        return (60 * 60 * date.getHours()) + (60 * date.getMinutes()) + (date.getSeconds());
    }

    /**
     *      renvoie l'heure d'une journée représentée par les secondes passées après minuit
     * 
     *  
     * @param int spm
     *          Les secondes passée après minuit d'un jour
     *          
     * @throw IllegalArgumentException
     *        Si le paramêtre est négatif ou est plus grand que 107999(nombre de secondes après minuit qui représente 23h 59m 59s)
     *          
     *  
     * @return int
     *      L'heure en fonction des secondes passée après minuit 
     * 
     */
    public static int hours(int spm){



        if(spm < 0 || spm > UP_LIMIT){//modifié après rendu intermédiaire. 
            throw new IllegalArgumentException("le nombre de secondes après minuit est invalide:"+spm);
        }
        return (spm/3600)%60; //modifié après rendu intermédiaire.

    }

    /**
     *          Retourne les minutes d'une heure donnée sous forme de secondes passée après minuit d'un jour
     * 
     * @param int spm
     *      Une représentation de l'heure sous forme de secondes passées depuis minuit
     * 
     * @throw IllegalArgumentException 
     *      Si spm est négatif ou si le paramêtre est plus grand que 107999 (représente 23h 59m 59s)
     * 
     * @return int
     *      Le nombre de minutes de l'heure en fonction des secondes passées après minuit
     */
    public static int minutes(int spm){



        if(spm < 0 || spm > UP_LIMIT){ //modifié après rendu intermédiaire. 
            throw new IllegalArgumentException("le nombre de secondes après minuit est invalide:"+spm);
        }

        return (spm/60)%60; //modifié après rendu intermédiaire. 

    }

    /**
     *          Retourne les secondes d'une heure donnée sous forme de secondes passée après minuit d'un jour
     * 
     * @param int spm
     *      Une représentation de l'heure sous forme de secondes passées depuis minuit
     * 
     * @throw IllegalArgumentException 
     *      Si spm est négatif ou si le parametre est plus grand que 107999 (représente 23h 59m 59s)
     * 
     * @return int
     *      Le nombre de secondes de l'heure en fonction des secondes passées après minuit
     */
    public static int seconds(int spm){


        if(spm < 0 || spm > UP_LIMIT){//modifié après rendu intermédiaire. 
            throw new IllegalArgumentException("le nombre de secondes après minuit est invalide:"+spm);
        }
        return spm%60; //modifié après rendu intermédiaire.
    }

    /**
     *          Renvoie une représentation textuelle de l'heure 
     * 
     * @param int spm
     *      Une heure représentée par le nombre de secondes passées après minuit
     *      
     * @trown IllegalArgumentException 
     *      Si spm est négatif ou si le paramêtre est plus grand que 107999 (représente 23h 59m 59s)     
     * 
     * @retrun String
     *      Représentation textuelle de l'heure
     * 
     * 
     */
    public static String toString(int spm){


        if(spm < 0 || spm > UP_LIMIT){//modifié après rendu intermédiaire. 
            throw new IllegalArgumentException("le nombre de secondes après minuit est invalide:"+spm);
        }


        return String.format("%02d:%02d:%02d", hours(spm), minutes(spm), seconds(spm));
    }




}
