package ch.epfl.isochrone.math;
import java.lang.Integer;

/**
 * Une classe Math contenant des méthodes de calcul:
 * 1- arcsinh(x)
 * 2- sin^2(x/2) (haversin)
 * 3- divF(n,d)
 * 4- modF(n,d) 
 * 
 * @author Brunner Loïc (235901)
 * @author Nanchen Bastian (237273)
 */
public final class Math {

    private Math(){};//constructeur privé qui empêche l'instanciation de la classe

    /**
     *      Effectue la fonciton inverse du sinus hyperbolique
     *      
     * @param double x
     * @return double: arcsinh(x)
     */
    public static double asinh(double x){

        return java.lang.Math.log(x + java.lang.Math.sqrt(1+x*x));//log = logarithme naturel  
    }


    /**     Fonction Haversin
     * 
     * @param double x
     * @return double: sin^2(x/2)
     */
    public static double haversin(double x){

        return java.lang.Math.pow(java.lang.Math.sin(x/2),2.0);
    }

    /**     Permet d'obtenir le quotien de la division par défaut de deux nombres
     * 
     * @param int n
     *          dividende
     * @param int d
     *           diviseur
     * 
     * @return le quotient de la division par défaut de n par d (int)
     */
    public static int divF(int n, int d){

        int I = 0;
        int rt = n%d;
        int qt= n/d;
        if(Integer.signum(rt) == -Integer.signum(d) ){
            I = 1;
        } else {
            I = 0;
        }
        return (qt - I);
    }

    /**
     *      Permet d'obtenir le reste de la division par défaut de deux nombres
     * 
     * @param int n
     *          dividende
     * @param int d
     *          diviseur
     * 
     * @return le reste de la division par défaut de n par d
     * 
     */
    public static int modF(int n, int d){

        int I = 0;
        int rt = n%d;
        if(Integer.signum(rt) == -Integer.signum(d) ){
            I = 1;
        } else {
            I = 0;
        }

        return (rt + (I * d));
    }



}
