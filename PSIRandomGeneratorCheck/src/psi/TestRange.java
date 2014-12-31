/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package psi;

/**
 *
 * @author Jan Kozak
 */
public enum TestRange {
    
        ONE(1, 10000, 33150, "0to1", "Sekvence ma rovnomerne rozdeleni na intervalu <0, 1>.", "6,63",
        "Obe tridy 5 000."),
        TEN(10, 11000, 23210, "0to10", "Sekvence ma rovnomerne rozdeleni na intervalu <0, 10>.", "23,21",
        "Vsechny tridy 1 000."), 
        HUNDRED(100, 101000, 135810, "0to100", "Sekvence ma rovnomerne rozdeleni na intervalu <0, 100>.", "135,81",
        "Vsechny tridy 1 000."),
        GAUSSIAN(10, 10000, 69630, "gaussian", "Sekvence ma normalni normovane rozdeleni.", "23,21",
        "Tridy 1 az 8: 1 000, tridy 0 a 9: 750, trida 10: 500.");
        
        public final int max;
        public final int seqLength;
        public final int chiSqCriticalValue;
        private final String filename;
        private final String H0;
        private final String crit;
        private final String expected;
        
        private TestRange(int max, int seqLength, int chi2CriticalValue, String filename, String H0,
                String crit, String expected) {
            this.max = max;
            this.seqLength = seqLength;
            this.chiSqCriticalValue = chi2CriticalValue;
            this.filename = filename;
            this.H0 = H0;
            this.crit = crit;
            this.expected = expected;
        }
        
        public String getCritValue() {
            return crit;
        }
        
        public String filename() {
            return filename;
        }
        
        public String getH0() {
            return this.H0;
        }
    
        public String getExpected() {
            return this.expected;
        }
}
