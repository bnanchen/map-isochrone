package ch.epfl.isochrone.timetable;
import ch.epfl.isochrone.math.Math;

/**
 * Classe permettant de stocker une date du calendrier grégorien
 * 
 * 
 * @author Brunner Loïc (235901)
 * @author Nanchen Bastian(237273)
 * 
 * 
 */
public final class Date implements Comparable<Date>{

    private final int days;// attribut (jours passés depuis le premier janvier de l'an 1 (compris))

    /**
     * énumération contenant les 7 jours de la semaine 
     */
    public enum DayOfWeek {

        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    };

    /**
     * énumération contenant les 12 mois de l'année
     * 
     */
    public enum Month {

        JANUARY, FEBRUARY, MARCH, APRIL, MAI, JUNE, JULY,AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
    };

    /**
     *      Constucteur principal de la classe Date
     * 
     * @param int day
     *          Jour du mois (compris entre 1 et 31)
     * @param Month month
     *          Mois de l'année (de JANUARY à DECEMBER)
     * @param int year 
     * 
     * @throw IllegalArgumentException
     *         Si le jour est négatif ou trop grand pour le mois passé en argument
     *     
     */
    public Date(int day, Month month, int year) {
        
        if(day < 1 || day > daysInMonth(month, year)) {
            throw new IllegalArgumentException(day +"n'est pas un jour.");
        }
        days = dateToFixed(day, month, year);// on utilise méthode privée qui nous transforme un jour, mois, heure en un nombre de jour depuis le premier janvier de l'an 1
    }

    /**
     *          Constuit la classe Date à l'aide d'entiers uniquement
     * 
     * @param int day
     *      Jour du mois(compris entre 0 et 31)
     *  
     * @param int month
     *      Mois de l'année (compris entre 1 et 12)
     * 
     * @param int year 
     *      Année de la date
     *
     * @throw IllegalArgumentException
     *          Si le jour est négatif ou invalide pour le mois en cours
     *          Si le mois n'est pas compris entre 0 et 12
     * 
     * 
     */
    public Date(int day, int month, int year) {

        this(day, intToMonth(month), year);

    }

    /**
     *          Construit une Date en transformant une Date de java
     * 
     * @param java.util.Date date
     *      Une Date java
     */
    @SuppressWarnings("deprecation")
    public Date(java.util.Date date) {

        this(date.getDate() , intToMonth(date.getMonth()+1), date.getYear() + 1900);

    }

    /**
     *     Accesseur pour le jour du mois
     * 
     * @return int 
     *      Entier représentant le jour du mois (entre 1 et 31)
     * 
     * 
     */
    public int day() {

        int n = days;
        int m = this.intMonth();// appel de la méthode permettant de connaître le mois de notre classe 
        int y = this.year();// appel de la méthode permettant de connaître l'année de notre classe

        int d = n - dateToFixed(1, intToMonth(m), y) + 1;
        return d;
    }

    /**
     *      Accesseur pour le mois de l'année
     * 
     * @return Month
     *      Le mois de la date 
     * 
     */
    public Month month() {

        int m = this.intMonth();// appel de la fonction qui retourne le mois de notre Objet en entier

        return intToMonth(m);// appel de la méthode de transformation d'entier en mois
    }

    /**     Accesseur pour le mois de l'année (sous forme d'entier)
     * 
     * @return int
     *       Le mois en la date 
     * 
     */
    public int intMonth() {

        int n = days;
        int y = this.year();// appel de la méthode permetant de retourner l'année de notre Date
        int m = 0;
        int c = 2;
        int p = n - dateToFixed(1, Month.JANUARY , y);//nombre de jours écoulés depuis le début de l'année
        boolean l = isLeapYear(y);// true si l'année est bissextile
        if (n < dateToFixed(1, Month.MARCH, y)){
            c = 0;
        } else if (n >= dateToFixed(1,Month.MARCH, y) && l){
            c = 1;
        }

        m = Math.divF((12 * (p + c) + 373), 367);
        return m;
    }

    /**
     *      Accesseur pour l'année de la date
     * 
     * @return int
     *      Année de la date
     * 
     */
    public int year() {

        int n = days;
        int y=0;

        int d0 = n - 1;
        int n400 = Math.divF(d0, 146097);
        int d1 = Math.modF(d0, 146097);
        int n100 = Math.divF(d1, 36524);
        int d2 = Math.modF(d1, 36524);
        int n4 = Math.divF(d2, 1461);
        int d3 = Math.modF(d2,  1461);
        int n1 = Math.divF(d3, 365);
        int y0 = (400 * n400) + (100 * n100) + (4 * n4) + n1;



        if (n100 == 4 || n1 == 4) {
            y = y0;
        } else {
            y = y0 + 1;
        }

        return y;
    }

    /**
     *     Permet de connaître le jours de la semaine (lundi-dimanche) de l'objet
     * 
     *  @return DayOfWeek
     *        Le jour de la semaine de la date
     * 
     */
    public DayOfWeek dayOfWeek(){

        int n = Math.modF(days, 7);


        assert n > 0: ("nombre de jours négatif à la sortie de la boucle 7");

        return DayOfWeek.values()[(n+6) % 7]; //modifié après rendu intermédiaire. 
    }

    /**
     * Retourne une date en fontion du nombre de jours de différence passé en paramêtre
     * 
     * @param int daysDiff
     *      Nombre de jours de différence avec l'objet Date, représente le jour qui sépare les deux dates
     * 
     * @return Date 
     *      La date à daysDiff jours de différence avec l'objet Date 
     * 
     *  
     */
    public Date relative(int daysDiff) {

        int daysTot = days + daysDiff;
        return fixedToDate(daysTot);
    }

    /**
     *          Retourne une représentation de l'Objet Date dans un Objet java.util.Date
     * 
     * @return java.util.Date
     *          Représentation de la date dans un Objet java.util.Date
     * 
     */
    @SuppressWarnings("deprecation")
    public java.util.Date toJavaDate() {


        return new java.util.Date(this.year() - 1900, this.intMonth() - 1, this.day());
    }

    /**
     *      Retourne une représentation textuelle de la date 
     * 
     * @return String
     *      Représentation textuelle de la classe  
     */
    @Override
    public String toString(){

        return String.format("%04d-%02d-%02d", year(), intMonth(), day()); //modifié après rendu intermédiaire.
    }

    /**
     *          Fonction qui permet de tester si deux Date sont les mêmes (même jour, même mois, même année)
     * 
     * @return boolean
     *      True: si les 2 dates on le même nombre de jour, mois, année
     */
    @Override
    public boolean equals(Object that) {


        int n = dateToFixed(((Date) that).day(), ((Date) that).month(), ((Date) that).year());// on transforme la date passée en argument en un nombre de jour pour tester l'égalité


        return n == days; //modifié après rendu intermédiaire.
    }

    /**
     *          Retourne le nombre de jours passé depuis le premier janvier de l'an 1(compris) jusqu'à Date
     * 
     * @return int
     *      Le nombre de jours passé depuis le début du calendrier grégorien
     */
    @Override
    public int hashCode() {

        return days;
    }

    /**
     *          Fonction qui permet de comparer 2 objets du type Date
     * 
     * 
     * @param Date that
     *      Date à comparer avec l'Objet
     * 
     * @returm int 
     *      0 si les Date sont égales 
     *      -1 si la Date est plus petite que la Date passée en argument
     *      1 si la Date est plus grande que la Date passée en argument
     * 
     * 
     */
    @Override
    public int compareTo(Date that) {
        
            int x = dateToFixed(this.day(), this.month(), this.year()) - dateToFixed((that).day(), (that).month(), (that).year()) ;
            if (x < 0) {
                return -1;
            } else if (x > 0) {
                return 1;
            } else {
                return 0;
            }
    }

    private static Month intToMonth(int m) {//méthode privée
        if(m < 1 || m > 12){
            throw new IllegalArgumentException(m +" ne représente aucun mois");
        }
        return Month.values()[m-1];//modifié après rendu intermédiaire. 
    }

    private static int monthToInt(Month m) {//méthode privée
        return m.ordinal() + 1; //améliorer après rendu intermédiaire. 
      }

    private static boolean isLeapYear(int y) {//méthode privée

        return ((Math.modF(y, 4) == 0 && Math.modF(y, 100) != 0) || (Math.modF(y, 400) == 0));  

    }

    private static int daysInMonth(Month m, int y){//méthode privée
        switch(m){
        case FEBRUARY:
            return isLeapYear(y)? 29:28;
        case APRIL:
        case JUNE:
        case SEPTEMBER:
        case NOVEMBER:
        return 30;
        default:
            return 31;
        
        }
    }

    private static int dateToFixed(int d, Month m, int y) {//méthode privée
        int y0 = y-1;
        int c = -2;// terme correctif.
        boolean l = isLeapYear(y); //test bissextile
        if (monthToInt(m) <= 2){ //modifié après rendu intermédiaire. 
            c = 0;
        } else if(l){
            c = -1;
        }

        return (365 * y0) + Math.divF(y0, 4) - Math.divF(y0, 100) + (Math.divF(y0, 400)) + (Math.divF(((367 * monthToInt(m)) - 362), 12) + c + d);
    }

    private static Date fixedToDate(int n) {//méthode privée
        // pour trouver l'année:
        int y = 0;

        int d0 = n - 1;
        int n400 = Math.divF(d0, 146097);
        int d1 = Math.modF(d0, 146097);
        int n100 = Math.divF(d1, 36524);
        int d2 = Math.modF(d1, 36524);
        int n4 = Math.divF(d2, 1461);
        int d3 = Math.modF(d2,  1461);
        int n1 = Math.divF(d3, 365);
        int y0 = (400 * n400) + (100 * n100) + (4 * n4) + n1;

        if (n100 == 4 || n1 == 4) {
            y = y0;
        } else {
            y = y0 + 1;
        }



        //pour trouver le mois:
        int m = 0;
        int c = 2;
        int p = n - dateToFixed(1, intToMonth(1), y);// jours écoulés depuis le début de l'année
        boolean l = isLeapYear(y);
        if (n < dateToFixed(1, Month.MARCH, y)){
            c = 0;
        } else if (n >= dateToFixed(1,Month.MARCH, y) && l){
            c = 1;
        }

        m = Math.divF(((12 * (p + c)) + 373), 367);






        //pour trouver le jour:
        int d = n - dateToFixed(1, intToMonth(m), y) + 1;

        assert d < 1 || d > 31: d +"n'est pas un jour.";
        assert m < 1 || m > 12: m +"ne correspond à aucun mois.";

        return new Date(d, m, y);
    }




}
