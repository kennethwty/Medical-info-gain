/*
 * Name: Tsz Yan Wong
 * CSE 4701 Database
 *
 * This project finds the gene mutation that is mostly likely the cause of death in patients by calculating the information gains in a real medical data set hosted in an Oracle database.
 * The result is also in the word document in this repository.
 *
 */

package Project_2_2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Project_2_2 {
    public static void main(String args[]) {
        ArrayList<String> Mutations = new ArrayList<String>();
    	Mutations.add("APC");
    	Mutations.add("TP53");
    	Mutations.add("KRAS");
    	Mutations.add("PIK3CA");
    	Mutations.add("PTEN");
    	Mutations.add("ATM");
    	Mutations.add("MUC4");
    	Mutations.add("SMAD4");
    	Mutations.add("SYNE1");
    	Mutations.add("FBXW7");

        Connection con;
        Statement stmt;
        ResultSet rs;
        ResultSetMetaData rsmd;

        /* Database credentials */

        String user = "cse4701";
        String password = "datamine";
        String host = "query.engr.uconn.edu";
        String port = "1521";
        String sid = "BIBCI";
        String url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;

        double A_Y1 = 0;
        double B_Y0 = 0;
        double C_N1 = 0;
        double D_N0;
        double infoNeed;


        double[] APC = new double[1];
        double[] TP53 = new double[1];
        double[] KRAS = new double[1];
        double[] PIK3CA = new double[1];
        double[] PTEN = new double[1];
        double[] ATM = new double[1];
        double[] MUC4 = new double[1];
        double[] SMAD4 = new double[1];
        double[] SYNE1 = new double[1];
        double[] FBXW7 = new double[1];

        double[][] arrays = new double[][] {APC, TP53, KRAS, PIK3CA, PTEN, ATM, MUC4, SMAD4, SYNE1, FBXW7};

        double ig;

        //Create two new arrays for the final result
        double[] gains = new double[10]; // Store the IGs for the 10 genes in descending order (later after the try block)
        int[] overlap_A = new int[10];

        try {
            // Make a connection to the Oracle Database
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();

            /**
             * Dead = 497, Alive = 130
             * Entropy is calculated by Info(D) = I(130, 497)
             */
            double original_entropy = compute_entropy(130, 497);

            //***************************//
            // Get A,B,C,D for all the genes
            for(int i = 0; i < Mutations.size(); i++) {
                String sql = "SELECT COUNT(" + Mutations.get(i) + ") FROM IG_READY WHERE STATUS = 'Y' AND " + Mutations.get(i) + " = 1";
                rs = stmt.executeQuery(sql);
                rsmd = rs.getMetaData();
                while(rs.next()) {
                    A_Y1 = rs.getDouble(1);
                }
                overlap_A[i] = (int)A_Y1;

                sql = "SELECT COUNT(" + Mutations.get(i) + ") FROM IG_READY WHERE STATUS = 'Y' AND " + Mutations.get(i) + " != 1";
                rs = stmt.executeQuery(sql);
                rsmd = rs.getMetaData();
                while(rs.next()) {
                    B_Y0 = rs.getDouble(1);
                }

                sql = "SELECT COUNT(" + Mutations.get(i) + ") FROM IG_READY WHERE STATUS = 'N' AND " + Mutations.get(i) + " = 1";
                rs = stmt.executeQuery(sql);
                rsmd = rs.getMetaData();
                while(rs.next()) {
                    C_N1 = rs.getDouble(1);
                }

                D_N0 = 627 - (A_Y1 + B_Y0 + C_N1);
                infoNeed = compute_infoD(A_Y1, B_Y0, C_N1, D_N0);
                ig = info_gain(original_entropy, infoNeed);

                arrays[i][0] = ig;
                gains[i] = ig; //Store the IGs in another array
                System.out.println("The Information Gain for " + Mutations.get(i) + " is " + arrays[i][0]);
            }
        }
        catch (SQLException ex) {
            System.out.println("Error Occured");
        }

        String[] genes = {"APC", "TP53", "KRAS", "PIK3CA", "PTEN", "ATM", "MUC4", "SMAD4", "SYNE1", "FBXW7"};

        //Using Selection Sort to sort the gains array as well as the genes array
        int minIndex;
        double minValue;
        String minString;
        int minOcnt;
        int index;

        for(int startScan = 0; startScan < gains.length - 1; startScan++) {
            minIndex = startScan;
            minValue = gains[startScan];
            minString = genes[startScan];
            minOcnt = overlap_A[startScan];
            for(index = startScan + 1; index < gains.length; index++) {
                if(gains[index] < minValue) {
                    minValue = gains[index]; //For the gains array
                    minString = genes[index]; //For the genes array
                    minOcnt = overlap_A[index]; //For O CNT
                    minIndex = index;
                }
            }
            gains[minIndex] = gains[startScan];
            gains[startScan] = minValue;
            genes[minIndex] = genes[startScan];
            genes[startScan] = minString;
            overlap_A[minIndex] = overlap_A[startScan];
            overlap_A[startScan] = minOcnt;
        }


        //Print out the data, top 5 information gain ranked
        System.out.println();
        System.out.println(" ------------------------------------------------------");
        System.out.println("|      Gene ID    |        IG        |       O CNT     |");
        System.out.println(" ------------------------------------------------------");
        //Data
        System.out.printf("|      %6s     |    %.8f    |     %5d       |\n", genes[9], gains[9], overlap_A[9]);
        System.out.println(" ------------------------------------------------------");
        System.out.printf("|      %6s     |    %.8f    |     %5d       |\n", genes[8], gains[8], overlap_A[8]);
        System.out.println(" ------------------------------------------------------");
        System.out.printf("|      %6s     |    %.8f    |     %5d       |\n", genes[7], gains[7], overlap_A[7]);
        System.out.println(" ------------------------------------------------------");
        System.out.printf("|      %6s     |    %.8f    |     %5d       |\n", genes[6], gains[6], overlap_A[6]);
        System.out.println(" ------------------------------------------------------");
        System.out.printf("|      %6s     |    %.8f    |     %5d       |\n", genes[5], gains[5], overlap_A[5]);
        System.out.println(" ------------------------------------------------------");

        System.out.println();
        System.out.println("The End");
        System.out.println();
        System.exit(0);
    }

    /* A method that calculates the information gain and return it for ranking */
    public static double info_gain(double entropy, double info_needed) {
        // calculate information gain
        return entropy - info_needed;
    }

    /* A method that computes the entropy */
    public static double compute_entropy(double x, double y) {
        double entropy;

        // compute the entropy
        double x1 = x / (x+y);
        double y1 = y / (x+y);

        // logb(n) = loge(n) / loge(b)
        entropy = -x1 * (Math.log(x1)/Math.log(2)) - (y1 * (Math.log(y1)/Math.log(2)));

        return entropy;
    }

    /* A method that computes the Information needed, aka InfoA(D) */
    public static double compute_infoD(double A, double B, double C, double D) {
        /**
         * A = Dead with mutation
         * B = Dead with no mutation
         * C = Alive with mutation
         * D = Alive with no mutation
         */

        // Modify the data if they are 0 so that the NaN situation doesn't occur
        if(A==0)
            A = 0.00000000001;
        if(B==0)
            B = 0.00000000001;
        if(C==0)
            C = 0.00000000001;
        if(D==0)
            D = 0.00000000001;

        double infoD_mutation;
        double infoD_noMutation;

        double mutation = A + C;
        double no_mutation = B + D;
        double total = mutation + no_mutation;

        infoD_mutation = (mutation/total) * compute_entropy(A, C);
        infoD_noMutation = (no_mutation/total) * compute_entropy(B, D);

        return infoD_mutation + infoD_noMutation;
    }
}
