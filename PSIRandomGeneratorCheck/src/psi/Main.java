/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package psi;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 *
 * @author Jan Kozak <kozakj11@fel.cvut.cz>
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        for (DistTest.RandomGeneratorType rgt : DistTest.RandomGeneratorType.values()) {
            for (TestRange tr : TestRange.values()) {
                if (tr == TestRange.GAUSSIAN && rgt != DistTest.RandomGeneratorType.NEXT_GAUSSIAN) {
                    continue;
                }
                if (tr != TestRange.GAUSSIAN && rgt == DistTest.RandomGeneratorType.NEXT_GAUSSIAN) {
                    continue;
                }
                DistTest udt = new DistTest(rgt, tr, 100);
                udt.testDistribution();
            }
        }
    }
    
    
}
