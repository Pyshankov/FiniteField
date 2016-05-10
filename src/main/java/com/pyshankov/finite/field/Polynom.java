package com.pyshankov.finite.field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by pyshankov on 01.05.2016.
 */
public class Polynom {
    private final int[] polymomCoefficient;
    private final int degree;
    private final int index;

    public Polynom(int[] polynomCoefficient) {
        this.polymomCoefficient = polynomCoefficient;
        degree=computeDegree(polynomCoefficient);
        index=toTenFromBinary();
    }

    public static int computeDegree(int[] v){

        if (isAllNull(v)) return 0;

        int length = v.length;
        int j = 0;
        for( int i=0  ; i < length ; i++){
            if(v[i]==1){
                j = i;
                break;
            }
        }
        return length-j-1;
    }

    public int[] getPolymomCoefficient() {
        return polymomCoefficient;
    }

    public int getDegree() {
        return degree;
    }

    public static Polynom addPolynoms(Polynom p1,Polynom p2){
        return new Polynom(xor(p1.getPolymomCoefficient(),p2.getPolymomCoefficient()));
    }

    public static Polynom mod(Polynom p,Polynom mod){
        mod = new Polynom(changeSize(mod.getPolymomCoefficient(),p.polymomCoefficient.length));
        while (p.degree>=mod.degree){
            int k = p.degree;
            p= new Polynom(xor(p.polymomCoefficient,binaryShift(mod.polymomCoefficient,k-mod.degree)));
        }
        return new Polynom(trimToSize(p.getPolymomCoefficient(),mod.degree));
    }

    private static int[] xor(int[] v1,int[] v2){
        if(v1.length>v2.length) v2=changeSize(v2,v1.length);
        if(v1.length<v2.length) v1=changeSize(v1,v2.length);
        int[] res = new int[v1.length];
        for(int i = 0 ; i < v1.length ; i++){
            res[i] = (v1[i]+v2[i]) % 2 ;
        }
        return res;
    }

    public static int[] changeSize(int[] v, int size){
        int[] res;
        if(v.length>=size) return v;
        res = new int[size];
        int j = 0 ;
        for (int i = size-v.length ; i < size ; i++){
            res[i] = v[j];
            j++;
        }
        return res;
    }

    public static int[] trimToSize( int[] v, int size){
        int[] res;
        res = new int[size];
        int j = size-1;
        for (int i = v.length-1 ; i >=v.length-size ; i--){
            res[j] = v[i];
            j--;
        }
        return res;
    }

    public static int[] binaryShift(int[] v,int shiftNum){
//        v= changeSize(v,initialSize);

        int[] res = new int[v.length];
        for(int i = 0 ; i < res.length ; i ++){
            if((i+shiftNum) < res.length) {
                res[i] = v[i + shiftNum];
            } else {
                res[i] = 0;
            }
        }
        return res;
    }

    public static List<Integer> toBinaryReverse(int a) {

        List<Integer> list = new ArrayList<>();
        int b;
        while (a != 0) {
            b = a % 2;
            list.add(b);
            a = a / 2;
        }
//        Collections.reverse(list);
        return list;
    }

    //compute a^n
    public static Polynom powerOf(Polynom a, int degree,Polynom mod){
        List<Integer> degreeBinaryList= toBinaryReverse(degree);
        int[] val = {1};
        Polynom b = new Polynom(changeSize(val,a.polymomCoefficient.length));
        Polynom c = a;
        for (int s = 0 ; s < degreeBinaryList.size() ; s++){
            if(degreeBinaryList.get(s)==1)
            {
                b = mod(multiply(b,c),mod);
            }
            c = mod(multiply(c,c),mod);
        }
        return b;
    }

    public static Polynom multiply(Polynom p1, Polynom p2) {
        int[] a = p1.getPolymomCoefficient();
        int[] b = p2.getPolymomCoefficient();
        int n = a.length;
        int m = b.length;
        int[] c = new int[n+m-1];
        for (int k = 0; k < n+m-1; ++k) {
            int s = 0;
            for (int i = 0; i <= k; ++i) {
                if (i >= n || k-i >= m) {
                    continue;
                }
                s+=(a[i]*b[k-i]);
            }
            c[k] = s %2;
        }

        return new Polynom(c);
    }

    public static boolean isAllNull(int[] mas){
        for (int i = 0 ; i < mas.length ; i++){
            if(mas[i]==1) return false;
        }
        return true;
    }

    public int numberOfOne(){
        int res = 0;
        for (int i = 0 ; i < this.polymomCoefficient.length ; i++){
            if(polymomCoefficient[i]==1) res+=1;
        }
        return res;
    }

    @Override
    public String toString() {
        return "Polynom{" +
                "polymomCoefficient=" + Arrays.toString(polymomCoefficient) +
                ", degree=" + degree +
                '}';
    }

    public int toTenFromBinary(){
        int res = 0;
        for (int i = 0 ; i < this.polymomCoefficient.length ; i++){
            if (this.polymomCoefficient[i]!=0) res = res + (int) Math.pow(2d,this.polymomCoefficient.length-i-1);
        }
        return  res;
    }

    public static Polynom generatePolynomWithOne(int size,int i){
        int[] vals = new int[size];
        vals[i]=1;
        return  new Polynom(vals);
    }

    public int getIndex() {
        return index;
    }
}
