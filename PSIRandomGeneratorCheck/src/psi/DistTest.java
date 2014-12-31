/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package psi;

import java.io.IOException;
import java.util.Random;

/**
 *
 * @author Jan Kozak
 */
public class DistTest {
    private static final Random RANDOM_CLASS = new Random();
    private static final SeqProtocolCreator SEQ_PROT = new SeqProtocolCreator();
    private final RandomGeneratorType rgt;
    private final TestRange testRange;
    private final int[] seqTestResults;
    private final boolean[] rejected;
    private final int numOfSequences;
    
    public DistTest(RandomGeneratorType rgt, TestRange testRange, int numOfSequences) {
        this.rgt = rgt;
        this.testRange = testRange;
        this.numOfSequences = numOfSequences;
        this.rejected = new boolean[numOfSequences];
        this.seqTestResults = new int[numOfSequences];
    }

    public int[] getSeqTestResults() {
        return seqTestResults;
    }

    public boolean[] getRejected() {
        return rejected;
    }
    
    public void testDistribution() throws IOException {
        int range = testRange.max;
        SequenceGenerator seqg = new SequenceGenerator(range + 1);
        for (int i = 0; i < numOfSequences; i++) {
            seqg.generateSequence();
            int stat = seqg.calculateStatistic();
            seqTestResults[i] = stat;
            rejected[i] = stat > testRange.chiSqCriticalValue ? true : false;
            SEQ_PROT.createProtocol(this, stat, seqg.chiSquaredClasses, 
                    seqg.intSeq, rejected[i], i);
            seqg.reset();
        }
        SEQ_PROT.printOverall(this, rejected, seqTestResults);
    }

    public RandomGeneratorType getRgt() {
        return rgt;
    }

    public TestRange getTestRange() {
        return testRange;
    }
    
    private class SequenceGenerator {
        private final int[] chiSquaredClasses;
        private int[] intSeq;
        
        private SequenceGenerator(int classes) {
            if (rgt == RandomGeneratorType.NEXT_GAUSSIAN) {
                this.chiSquaredClasses = new int[11];
            } else {
                this.chiSquaredClasses = new int[classes];
            }
            intSeq = new int[testRange.seqLength];
        }
        
        private void generateSequence() {
            for (int i = 0; i < testRange.seqLength; i++) {
                int num = nextNumber(testRange.max + 1);
                intSeq[i] = num;
            }
        }
        
        /**
         * Calculates numerator of test-statistics.
         * @return 
         */
        private int calculateStatistic() {
            int toReturn = 0;
            if (rgt != RandomGeneratorType.NEXT_GAUSSIAN) {
                //expected frequencies should be same for each test class
                //in non-gaussian case
                int expected = testRange.seqLength / (testRange.max + 1);
                for (int i = 0; i < chiSquaredClasses.length; i++) {
                    int diff = chiSquaredClasses[i] - expected;
                    toReturn += diff * diff;
                }
            } else {
                //expected frequencies are 10 %, 7,5 % and 5 %, and these are 
                //denominators in each part of test-statistics
                //I multiply observed values so they have all same denominator 30 %
                //of total length to avoid calculations with float numbers
                //(critical value for chi square test in Gaussian case is also 
                //multiplied by 3)
                int common = 3 * testRange.seqLength / 10;
                for (int i = 0; i < chiSquaredClasses.length; i++) {
                    int expected = calculateExpectedGaussian(i);
                    int norm = common / expected;
                    int diff = chiSquaredClasses[i] - expected;
                    toReturn += norm * diff * diff;
                }
            }
            return toReturn;
        }
        
        private int nextNumber(int bound) {
            int nextClass;
            double gaussian = 0;
            switch (rgt) {
                case NEXT_INT:
                    nextClass = RANDOM_CLASS.nextInt(bound);
                    break;
                case MATH_RAND:
                    nextClass = (int) (bound * Math.random());
                    break;
                case NEXT_GAUSSIAN:
                    gaussian = RANDOM_CLASS.nextGaussian();
                    nextClass = findClassForGaussian(gaussian);
                    break;
                default: 
                    throw new IllegalArgumentException("Unknown generator.");
            }
            chiSquaredClasses[nextClass]++;
            if (rgt == RandomGeneratorType.NEXT_GAUSSIAN) {
                return doubleToInt(gaussian);
            }
            return nextClass;
        }
        
        private int findClassForGaussian(double gaussian) {
            int rounded = doubleToInt(gaussian);
            if (rounded < -1960 || rounded > 1959) return 10;
            if (rounded < 0) {
                if (rounded < -843) {
                    if (rounded < -1282) return 0;
                    else return 1;
                } else if (rounded < -253) {
                    if (rounded < -524) return 2;
                    else return 3;
                } else return 4;
            } else {
                if (rounded < 524) {
                    if (rounded < 253) return 5;
                    else return 6;
                } else if (rounded < 1282) {
                    if (rounded < 842) return 7;
                    else return 8;
                } else return 9;
            }
        }
        
        private int calculateExpectedGaussian(int classNum) {
            if (classNum == 10) {
                return testRange.seqLength / 20;
            }
            if (classNum == 0 || classNum == 9) {
                return 3 * testRange.seqLength / 40;
            }
            return testRange.seqLength / 10;
        }
        
        private int doubleToInt(double d) {
            int k = (int) (d * 10000);
            int pom = k % 10;
            int ret = k / 10;
            if (pom > 4 && ret > -1) ret++;
            else if (pom < -4 && ret < 0) ret--;
            return ret;
        }
        
        private void reset() {
            for (int i = 0; i < chiSquaredClasses.length; i++) {
                chiSquaredClasses[i] = 0;
            }
        }
        
    }
    
    public enum RandomGeneratorType {
        NEXT_INT("next_int"), MATH_RAND("math_random"), NEXT_GAUSSIAN("gaussian");
        
        private final String filename;
        
        private RandomGeneratorType(String filename) {
            this.filename = filename;
        }
        
        public String filename() {
            return filename;
        }
    }

}
