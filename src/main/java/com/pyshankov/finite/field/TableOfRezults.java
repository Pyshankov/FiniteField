package com.pyshankov.finite.field;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Created by pyshankov on 03.05.2016.
 */
public class TableOfRezults {

    private final FiniteField finiteField;
    private final List<Polynom> fFuncl;
    private final List<Polynom> gFuncl;
    private final List<List<Integer>> algebraicNormalFormsF;
    private final List<List<Integer>> algebraicNormalFormsG;

    private final List<Integer> disbalanceF;
    private final List<Integer> disbalanceG;

    private final List<Integer> algebraicDegreeF;
    private final List<Integer> algebraicDegreeG;

    private final List<Double> nonlinearityF;
    private final List<Double> nonlinearityG;


    private final List<List<String>> KiF;
    private final List<List<String>> KiG;





    public TableOfRezults(FiniteField finiteField, Function<Polynom, Polynom> f, Function<Polynom, Polynom> g) {
        this.finiteField = finiteField;
        fFuncl = new ArrayList<>(finiteField.getElements().size());
        gFuncl = new ArrayList<>(finiteField.getElements().size());
        algebraicNormalFormsF = new ArrayList<>(finiteField.getModule().getDegree());
        algebraicNormalFormsG = new ArrayList<>(finiteField.getModule().getDegree());
        disbalanceF = new ArrayList<>();
        disbalanceG = new ArrayList<>();
        algebraicDegreeF = new ArrayList<>();
        algebraicDegreeG = new ArrayList<>();
        nonlinearityF = new ArrayList<>();
        nonlinearityG = new ArrayList<>();

        KiF = new ArrayList<>();
        KiG = new ArrayList<>();
        generateRezultsForFandG(f,g);
    }


    private void generateRezultsForFandG(Function<Polynom, Polynom> f, Function<Polynom, Polynom> g) {

        for (Polynom p : finiteField.getElements()) {
            fFuncl.add(f.apply(p));
            gFuncl.add(g.apply(p));
        }
        System.out.println("boolean functions have been build");

//        showRezultsOfBolleanFunction();
//        //build anf for functions
//        computeANF(f,g);
//        //degree
//        computeANFDegree();
//        //disbalance
//       computeDisbalance(f,g);
//        //nonlinearity
//       computenonlinearity(f,g);
//        //compute Ki and Eki
        algebraicNormalFormsF.clear();
        algebraicNormalFormsG.clear();
        algebraicDegreeF.clear();
        algebraicDegreeG.clear();
        nonlinearityF.clear();
        nonlinearityG.clear();
//
//        computeKi(f,g);

        //compute max df
        computeMaxDf(f,g);
    }

    public void showRezultsOfBolleanFunction() {

        try (FileWriter fw = new FileWriter("boolean_functions.txt")) {
           int length =  Arrays.toString(finiteField.getElements().get(0).getPolymomCoefficient()).length();
            fw.write(
                    changeSize("elements",length) + " " +
                    changeSize("F",length) + " " +
                    changeSize("G",length) + "\n"
            );
            for (int i = 0; i < finiteField.getElements().size(); i++) {
                    fw.write( Arrays.toString(finiteField.getElements().get(i).getPolymomCoefficient()) +
                            " " + Arrays.toString(fFuncl.get(i).getPolymomCoefficient()) +
                            " " + Arrays.toString(gFuncl.get(i).getPolymomCoefficient()) +
                            "\n"
                    );


            }

        }catch (IOException e) {}
    }

    public void computeANF(Function<Polynom, Polynom> f, Function<Polynom, Polynom> g) {

        try (FileWriter fw1 = new FileWriter("anf_F.txt") ; FileWriter fw2 = new FileWriter("anf_G.txt") ) {

            for (int i = 0 ; i < finiteField.getModule().getDegree() ; i++){
                int j = i;
                long timestamp = System.currentTimeMillis();
                algebraicNormalFormsF.add(Functions.getPolynomZygalnika(finiteField,p1->f.apply(p1).getPolymomCoefficient()[j]));
                fw1.write(algebraicNormalFormsF.get(i).toString()+"\n" );
                algebraicNormalFormsG.add(Functions.getPolynomZygalnika(finiteField,p1->g.apply(p1).getPolymomCoefficient()[j]));
                fw2.write(algebraicNormalFormsF.get(i).toString()+"\n" );
                System.out.println((System.currentTimeMillis()-timestamp)/1000);
            }

            System.out.println("anf have been build");

        }catch (IOException e) {

        }
    }


    public void computeANFDegree() {

        try (FileWriter fw1 = new FileWriter("anf_degree.txt") ) {
            fw1.write("degree:"+"\n");
            fw1.write("F:"+"\n");
            for (List<Integer> anf : algebraicNormalFormsF){
                int res = Functions.computeDegreeOfANF(finiteField,anf);
                algebraicDegreeF.add(res);
                fw1.write(res+"\n");
            }
            fw1.write("\n");
            fw1.write("G:"+"\n");
            for (List<Integer> anf : algebraicNormalFormsG){
                int res = Functions.computeDegreeOfANF(finiteField,anf);
                algebraicDegreeG.add(res);
                fw1.write(res+"\n");
            }
            fw1.write("\n");
            fw1.write("max F:"+ Collections.max(algebraicDegreeF)+"\n");
            fw1.write("max G:"+ Collections.max(algebraicDegreeG)+"\n");



            System.out.println("degree of functions have been computed");

        }catch (IOException e) {

        }
    }

    public void computeDisbalance(Function<Polynom, Polynom> f, Function<Polynom, Polynom> g) {

        try (FileWriter fw1 = new FileWriter("disbalance.txt") ) {
            fw1.write("F  G\n");

            for (int i = 0 ; i < finiteField.getModule().getDegree() ; i++) {
                int j = i;
                int resF =  Functions.walshFunction(
                        finiteField.getElements().get(0),
                        finiteField,
                        p -> f.apply(p).getPolymomCoefficient()[j]
                );
                int resG= Functions.walshFunction(
                        finiteField.getElements().get(0),
                        finiteField,
                        p -> g.apply(p).getPolymomCoefficient()[j]
                );
                disbalanceF.add(resF);
                disbalanceG.add(resG);
                fw1.write(resF+"  "+resG+"\n");
            }


            System.out.println("disbalance of functions have been computed");

        }catch (IOException e) {

        }
    }

    public void computenonlinearity(Function<Polynom, Polynom> f, Function<Polynom, Polynom> g){

        try (FileWriter fw1 = new FileWriter("nonlinearity.txt") ) {
         fw1.write("F    G\n");
            long timestamp = System.currentTimeMillis();
        Functions.nonlinearity(finiteField,f,nonlinearityF);
            System.out.println((System.currentTimeMillis()-timestamp));
            long timestamp1 = System.currentTimeMillis();
        Functions.nonlinearity(finiteField,g,nonlinearityG);
            System.out.println((System.currentTimeMillis()-timestamp1));
            for(int i = 0 ; i < nonlinearityF.size() ; i ++){
                fw1.write(nonlinearityF.get(i)+"  "+nonlinearityG.get(i)+"\n");
            }
            System.out.println("nonlinearity of functions have been computed");
        }catch (IOException e) {

        }

    }

    public void computeKi(Function<Polynom, Polynom> f, Function<Polynom, Polynom> g){
        boolean isFStrong = true;
        boolean isGStrong = true;
        boolean isFStrongAverage = true;
        boolean isGStrongAverage = true;

        try (FileWriter fw1 = new FileWriter("KiandEki.txt") ) {
            fw1.write("\n");
            for (int i = 0; i < finiteField.getModule().getDegree(); i++) {
                KiF.add(new ArrayList<>());
                KiG.add(new ArrayList<>());
                for (int j = 0; j < finiteField.getModule().getDegree(); j++) {
                    int k = j;
                    int res = Functions.Ki(
                            finiteField,
                            p -> f.apply(p).getPolymomCoefficient()[k],
                            i
                    );

                    if(isFStrong==true){
                        isFStrong = (res == Math.pow(2d,finiteField.getModule().getDegree()-1));
                    }

                    KiF.get(i).add(res
                            + "(" +
                            new BigDecimal(Functions.Eki(res, finiteField.getModule().getDegree())).setScale(2, RoundingMode.HALF_DOWN).floatValue()+")"
                    );

                    int res2 = Functions.Ki(
                            finiteField,
                            p -> g.apply(p).getPolymomCoefficient()[k],
                            i
                    );
                    if(isGStrong==true){
                        isGStrong = (res2 == Math.pow(2d,finiteField.getModule().getDegree()-1));
                    }

                    KiG.get(i).add(res2
                            + "(" +
                            new BigDecimal(Functions.Eki(res2, finiteField.getModule().getDegree())).setScale(2, RoundingMode.HALF_DOWN).floatValue()
                              +")"
                    );


                }
                fw1.write(changeSize(KiF.get(i).toString(),finiteField.getModule().getDegree()*2)+" "+KiG.get(i).toString()+"\n");
            }
            fw1.write(changeSize("Ki(f)(Eki)",KiF.get(0).toString().length())+" Ki(g)(Eki)\n");
            fw1.write("\n");
            fw1.write("does F have strong avalanche effect? : "+isFStrong);
            fw1.write("\ndoes G have strong avalanche effect? : "+isGStrong);

            fw1.write("\n");
            for (int i = 0; i < finiteField.getModule().getDegree(); i++) {

                int kf= Functions.KiF(finiteField,f,i);
                if(isFStrongAverage==true){
                    isFStrongAverage = (kf == finiteField.getModule().getDegree()*Math.pow(2d,finiteField.getModule().getDegree()-1));
                }
                float Ekf= new BigDecimal(Functions.EKi(kf,finiteField.getModule().getDegree())).setScale(2, RoundingMode.HALF_DOWN).floatValue();
                int kg= Functions.KiF(finiteField,g,i);
                if(isGStrongAverage==true){
                    isGStrongAverage = (kg == finiteField.getModule().getDegree()*Math.pow(2d,finiteField.getModule().getDegree()-1));
                }
                float Ekg= new BigDecimal(Functions.EKi(kg,finiteField.getModule().getDegree())).setScale(2, RoundingMode.HALF_DOWN).floatValue();
                fw1.write(
                        changeSize(kf+"("+ Ekf
                                +")",KiF.get(0).toString().length())+" "+
                                kg+"(" +
                                Ekg+")\n"
                );
            }
            fw1.write(changeSize("Ki(F)(Eki)",KiF.get(0).toString().length())+" Ki(G)(Eki)\n");

            fw1.write("\n");
            fw1.write("does F have strong average avalanche effect? : "+isFStrongAverage);
            fw1.write("\ndoes G have strong average avalanche effect? : "+isGStrongAverage);

            fw1.write("\n");

            System.out.println("Ki and ki of functions have been computed");

        }catch (IOException e){}
    }

    public void computeMaxDf(Function<Polynom, Polynom> f, Function<Polynom, Polynom> g){

        try (FileWriter fw1 = new FileWriter("maxDf.txt") ) {

            fw1.write("F : "+Functions.MDP2(finiteField,fFuncl)+"\n");
            fw1.write("G : "+Functions.MDP2(finiteField,gFuncl)+"\n");

        }catch (IOException e) {}
//        System.out.println(Functions.computeMaxDf(finiteField,f));
//        System.out.println(Functions.computeMaxDf(finiteField,g));

        System.out.println("max Df of functions have been computed");
    }

    private static String changeSize(String s,int size){
        int toLength = size-s.length();
        StringBuilder builder = new StringBuilder(s);
        for(int i = 0 ; i < toLength ; i++){
            builder.append(" ");
        }
        return builder.toString();
    }



}
