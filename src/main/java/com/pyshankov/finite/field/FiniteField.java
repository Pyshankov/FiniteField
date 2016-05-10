package com.pyshankov.finite.field;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pyshankov on 01.05.2016.
 */
public class FiniteField {

    private final Polynom module;

    private final List<Polynom> elements;

    public Polynom getModule() {
        return module;
    }

    public FiniteField(Polynom module) {
        this.module = module;
        elements = new ArrayList<>((int)Math.pow(2.d,module.getDegree()));
        generateElements(module,elements);

    }

    private void generateElements(Polynom module,List<Polynom> polynomList) {



        int[] elem = {1,0};
        int[] zero = {0};
        Polynom generator = new Polynom(Polynom.changeSize(elem,module.getDegree()));
        Polynom zeroElement = new Polynom(Polynom.changeSize(zero,module.getDegree()));
        polynomList.add(zeroElement);
        polynomList.add(generator);

        int numberOfElem = ((int) Math.pow(2.d,module.getDegree()))-1;

        for (int i = 2 ; i <= numberOfElem  ; i++  ){
            Polynom p = new Polynom(Polynom.trimToSize(Polynom.powerOf(generator,i,module).getPolymomCoefficient(),module.getDegree()));
            polynomList.add(p);
            System.out.println(p);
        }

        elements.sort((p1, p2) -> {
            if (p1.toTenFromBinary() > p2.toTenFromBinary()) return 1;
            if (p1.toTenFromBinary() < p2.toTenFromBinary()) return -1;
            else return 0;
        });


        try(FileWriter fw=new FileWriter("finiteField.txt")) {
           for(Polynom p : elements){
               fw.write(p.toString()+"\n");
           }
        }catch (IOException e) {

        }

    }

    public List<Polynom> getElements() {
        return elements;
    }


}
