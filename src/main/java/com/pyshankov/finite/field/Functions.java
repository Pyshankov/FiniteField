package com.pyshankov.finite.field;

import java.util.*;
import java.util.concurrent.SynchronousQueue;
import java.util.function.Function;

/**
 * Created by pyshankov on 02.05.2016.
 */
public class Functions {

    public static int scalarProduct(Polynom p1,Polynom p2){
        int[] v1 = p1.getPolymomCoefficient();
        int[] v2 = p2.getPolymomCoefficient();
        int res = 0;
        for(int i = 0 ; i < v1.length ; i++){
            res+=v1[i]*v2[i];
        }
        return res;
    }

    //f- boolean function, W(a)
    public static int walshFunction(Polynom a, FiniteField GF, Function<Polynom,Integer> f){
        int wfa = 0;
        for(int i = 0 ; i < GF.getElements().size() ; i++){
            Polynom x = GF.getElements().get(i);
            wfa += Math.pow( -1d, (f.apply(x)+scalarProduct(a,x)) % 2 );
        }
        return Math.abs(wfa);
    }

    public static int maxWalshInField(FiniteField field, Function<Polynom,Integer> f){
        List<Integer> res = new ArrayList<>();

        for (int i = 0 ; i < field.getElements().size() ; i++){
            int j =i;
            res.add(
                    walshFunction(
                            field.getElements().get(j),
                            field,
                            f
                    )
            );
        }
        return Collections.max(res);
    }

    //compute disbalance for each coordinate function of F
    public static  List<Integer> disbal(Polynom a , FiniteField GF ,Function<Polynom,Polynom> F){
        List<Integer> res = new ArrayList<>(a.getPolymomCoefficient().length);
        for (int i = 0 ; i < a.getPolymomCoefficient().length ; i++){
            int j = i;
            res.add(
                    walshFunction(
                            a,
                            GF,
                            p->F.apply(p).getPolymomCoefficient()[j]
                    )
            );
        }
        return res;
    }

    //nonlinearity
    public static List<Double> nonlinearity(FiniteField GF,Function<Polynom,Polynom> F,List<Double> res1){
        long time = System.currentTimeMillis();
        List<Double> listMax = new ArrayList<>(GF.getElements().size());
        for( int i = 0 ; i < GF.getModule().getDegree() ; i++){
            int j = i;
            double r = Math.pow(2d,GF.getModule().getDegree()-1) - 0.5 * maxWalshInField(GF,p->F.apply(p).getPolymomCoefficient()[j]);
            listMax.add(r);
            res1.add(r);
        }
        System.out.println(System.currentTimeMillis()-time);
        return listMax;
    }

    public static List<Integer> getPolynomZygalnika(FiniteField field,Function<Polynom,Integer> f){
        List<Integer> results = new ArrayList<>(field.getElements().size());
        List<Integer> values = new ArrayList<>(field.getElements().size());
        List<Integer> medium ;
        for (Polynom p : field.getElements()){
            results.add(f.apply(p));
        }
        for(int i = 0 ; i < field.getElements().size() ; i++){
           values.add(results.get(0));
            medium = results;
            results = new ArrayList<>(medium.size()-1);
            for(int j = 0 ; j < medium.size()-1 ; j++){
               results.add((medium.get(j)+medium.get(j+1))%2);
            }
            medium=null;
        }

        return values;

    }
    //algebraic degree
    public static int computeDegreeOfANF(FiniteField field,List<Integer> anf){
        Map<Integer,Polynom> map = new HashMap<>();
        for(int i = 0 ; i < anf.size() ; i++){
            if(anf.get(i) == 1){
                map.putIfAbsent(field.getElements().get(i).numberOfOne(),field.getElements().get(i));
            }
        }
        return  Collections.max(map.keySet());
    }

    //logarithm base 2
    private static double log2(double a) {
        return Math.log(a) / Math.log(2);
    }

    private static double entropy(List<Integer> val){
        return -1 * val.stream().mapToDouble(i->i).reduce(0,(x,y)-> x + y*log2(y));
    }

    public static int Ki(FiniteField field,Function<Polynom,Integer> f,int i){
        Polynom e1 = Polynom.generatePolynomWithOne(field.getModule().getDegree(),i);
       int res = 0;
        for (Polynom p : field.getElements()){
               res=res + (( f.apply(p) + f.apply(Polynom.addPolynoms(p,e1)) ) % 2);
        }
        return res;
    }

    public static int KiF(FiniteField field,Function<Polynom,Polynom> f,int i){
        Polynom e1 = Polynom.generatePolynomWithOne(field.getModule().getDegree(),i);
        int res = 0;
        for (Polynom p : field.getElements()){
            res=res + (Polynom.addPolynoms(f.apply(p),f.apply(Polynom.addPolynoms(p,e1))).numberOfOne());
        }
        return res;
    }

    public static double Eki(int res,int n){
        return Math.abs(res-Math.pow(2d,n-1))/Math.pow(2d,n-1);
    }

    public static double EKi(int res,int n){
        return Math.abs(res-n*Math.pow(2d,n-1))/(n*Math.pow(2d,n-1));
    }

    public static Polynom DaF(Polynom x,Polynom a,Function<Polynom,Polynom> f){
        return Polynom.addPolynoms(f.apply(x),f.apply(Polynom.addPolynoms(x,a)));
    }

    public static int crockener(Polynom p1,Polynom p2){
        if (Arrays.equals(p1.getPolymomCoefficient(),p2.getPolymomCoefficient())) return 1;
        else return 0;
    }

    public static double dF(FiniteField field,Function<Polynom,Polynom> f, Polynom a,Polynom b){
        int res = 0;
        for(Polynom p : field.getElements()){
            res = res + crockener(DaF(p,a,f),b);
        }
        return res/Math.pow(2d,field.getModule().getDegree());
    }

    public static double computeMaxDf(FiniteField field,Function<Polynom,Polynom> f){
        List<Double> res = new ArrayList<>(field.getElements().size()*field.getElements().size());

        for(int i = 1 ; i < field.getElements().size() ; i++){
            for(int j = 1 ; j < field.getElements().size() ; j++){
                if(i!=j)
                res.add(dF(field,f,field.getElements().get(i),field.getElements().get(j)));
            }
        }
        return Collections.max(res);
    }

}
