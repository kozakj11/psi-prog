/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package psi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Jan Kozak
 */
public class SeqProtocolCreator {
    private static final String NEXT_GAUSSIAN = "gaussian";
    private static final String NEXT_INT = "next_int";
    private static final String MATH_RAND = "math_random";
    private static final String OVERALL = "overall";
    private static final String DIR = "../protocols/";
    private static final String[] STRINGS = {DIR, DIR + OVERALL, DIR + NEXT_GAUSSIAN, 
        DIR + NEXT_INT, DIR + MATH_RAND};
    private static final String OK = "Nezamitame nulovou hypotezu.";
    private static final String REJECTED = "Nulovou hypotezu zamitame na hladine vyznamnosti 1 %.";
    
    static {
        File file;
        for (String str : STRINGS) {
            file = new File(str);
            file.mkdir();
        }
        String[] intern = {DIR + NEXT_INT, DIR + MATH_RAND};
        String[] types = {"/0to1", "/0to10", "/0to100"};
        for (String str : intern) {
            for (String typ : types) {
                file = new File(str + typ);
                file.mkdir();
            }
        }
        file = new File(DIR + NEXT_GAUSSIAN + "/" + NEXT_GAUSSIAN);
        file.mkdir();
    }
    
    public void createProtocol(DistTest udt, int stat, int[] classes, 
                    int[] sequence, boolean rejected, int seqNum) throws IOException {
        //DIR + type /
        String fname = getSubdirName(udt);
        String testRangeFn = udt.getTestRange().filename();
        File file = new File(DIR + fname + "/" + testRangeFn +"/seq" + seqNum + ".txt");
        file.createNewFile();
        PrintWriter pw = new PrintWriter(file);
        printHead(pw, udt, rejected, stat);
        printClasses(pw, classes);
        file = new File(DIR + fname + "/" + testRangeFn + "/sequences");
        file.mkdir();
        file = new File(DIR + fname + "/" + testRangeFn + "/sequences/seq" + seqNum + ".txt");
        file.createNewFile();
        pw = new PrintWriter(file);
        printSequence(pw, sequence, udt.getRgt());
    }
    
    public void printOverall(DistTest udt, boolean[] rejected, int[] stats) throws IOException {
        String name = getSubdirName(udt);
        File file = new File(DIR + OVERALL + "/" + name + "_" + udt.getTestRange().filename() + ".txt");
        file.createNewFile();
        PrintWriter pw = new PrintWriter(file);
        pw.println("H0: " + udt.getTestRange().getH0());
        pw.println("Kriticka hodnota: " + udt.getTestRange().getCritValue());
        pw.println("Zamitnuto: " + countRejected(rejected) + " z " + rejected.length);
        pw.println("%nSekvence |Statistika |Zaver");
        pw.println("---------|-----------|----------");
        for (int i = 0; i < stats.length; i++) {
            int statInt = countIntStat(stats[i], udt.getTestRange());
            int statFloat = countFloatStat(stats[i], udt.getTestRange());
            pw = pw.printf("%-9d|%6d,%02d  |", i, statInt, statFloat);
            if (rejected[i]) pw = pw.printf("Zamitame%n");
            else pw = pw.printf("Nezamitame%n");
            pw.flush();
        }
    }
    
    private int countRejected(boolean[] rejected) {
        int rej = 0;
        for (int i = 0; i < rejected.length; i++) {
            if (rejected[i]) rej++;
        }
        return rej;
    }
    
    private void printClasses(PrintWriter pw, int[] classes) {
        for (int i = 0; i < classes.length ; i+=10) {
            pw.println();
            pw.printf("Trida    |");
            for (int j = 0; j < 10 && i + j < classes.length; j++) {
                pw.printf("%5d|", i + j);
            }
            pw.flush();
            pw.println();
            pw.printf("Cetnost  |");
            for (int j = 0; j < 10 && i + j < classes.length; j++) {
                pw.printf("%5d|", classes[i + j]);
            }
            pw.flush();
            pw.println();
        }
    }
    
    private void printHead(PrintWriter pw, DistTest udt, boolean rejected, int stat) {
        TestRange tr = udt.getTestRange();
        pw.println("H0: " + tr.getH0());
        int first = countIntStat(stat, tr);
        int flt = countFloatStat(stat, tr);
        pw.printf("Hodnota statistiky: %d,%02d%n", first, flt);
        pw.printf("Kriticka hodnota: %s%n", tr.getCritValue());
        pw.flush();
        String result = rejected ? REJECTED : OK;
        pw.println("Zaver: " + result);
        pw.println("Ocekavane hodnoty: " + tr.getExpected());
    }
    
    private int countFloatStat(int stat, TestRange tr) {
        switch (tr) {
            case GAUSSIAN:
                return (stat % 3000) / 30;
            case ONE:
                return (stat % 5000) / 50;
            default:
                return (stat % 1000) / 10;
        }
    }
    
    private int countIntStat(int stat, TestRange tr) {
        switch (tr) {
            case GAUSSIAN:
                return stat / 3000;
            case ONE:
                return stat / 5000;
            default: return stat / 1000;
        }
    }
    
    private void printSequence(PrintWriter pw, int[] sequence, DistTest.RandomGeneratorType rgt) {
        for (int i = 0; i < sequence.length; i += 20) {
            for (int j = 0; j < 20 && i + j < sequence.length; j++) {
                if (rgt != DistTest.RandomGeneratorType.NEXT_GAUSSIAN) {
                    pw = pw.printf("%d; ", sequence[i + j]);
                } else {
                    int full = sequence[i + j] / 1000;
                    int part = Math.abs(sequence[i + j]) % 1000;
                    pw = pw.printf("%01d,%03d; ", full, part);
                }
            }
            pw.flush();
            pw.println();
        }
    }
    
    private String getSubdirName(DistTest udt) {
        TestRange tr = udt.getTestRange();
        DistTest.RandomGeneratorType rgt = udt.getRgt();
        String subdirn;
        switch (rgt) {
            case MATH_RAND:
                subdirn = MATH_RAND;
                break;
            case NEXT_GAUSSIAN:
                subdirn = NEXT_GAUSSIAN;
                break;
            default:
                subdirn = NEXT_INT;
        }
        return subdirn;
    }

}
