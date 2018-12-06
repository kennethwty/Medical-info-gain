package project_2_1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Project_2_1 {

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
    	
    	ArrayList<String> People = new ArrayList<String>();
    	ArrayList<String> Survivors = new ArrayList<String>();
    	HashMap<String, ArrayList<String>> MutPpl = new HashMap<String, ArrayList<String>>();
    	
    	
    	
        Connection con;
        Statement stmt;
        ResultSet rs;
        ResultSetMetaData rsmd;
        
        /* Database credentials */
        String user = /* username */;
        String password = /* password */;
        String host = /* host name */;
        String port = /* port number */;
        String sid = /* sid */;
        String url = /* url */ + host + ":" + port + ":" + sid;
        
        
        try {
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();

            String sql = "select patient_id from clinical GROUP BY Patient_id";
            ResultSet ras = stmt.executeQuery(sql);
            ResultSetMetaData rasmd = ras.getMetaData();
            
            
            
            while (ras.next())
            {
            	People.add((String)ras.getObject(1));
            }

            PreparedStatement ps;
            for(String Mut : Mutations)
            {
            	ArrayList<String> Ppl = new ArrayList<String>();
            	ps = "select patient_id from Mutation WHERE Variant_Classification != 'Silent' AND GENE_Symbol = '?' GROUP BY patient_id";
            	ps.setString(1, Mut);
                rs = ps.executeQuery();
            	rsmd = rs.getMetaData();
            	while(rs.next())
            	{
            		Ppl.add((String)rs.getObject(1));
            	}
            	MutPpl.put(Mut, Ppl);
            }
            
            sql = "select * from Clinical";
            
            rs = stmt.executeQuery(sql);
            rsmd = rs.getMetaData();
            
            while (rs.next())
            {
            	if(rs.getObject(6).equals("LIVING"))
            	{
            		Survivors.add((String)rs.getObject(1));
            	}
            }
        } catch (SQLException ex) {
            Logger.getLogger(Project_2_1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ArrayList<ArrayList<String>> Final = new ArrayList<ArrayList<String>>();
        for(String Person : People)
        {
        	ArrayList<String> Tuple = new ArrayList<String>();
        	Tuple.add(Person);
        	for(String Mut : Mutations)
        	{
        		if(MutPpl.get(Mut).contains(Person+"-01"))
        		{
        			Tuple.add("1");
        		}
        		else
        		{
        			Tuple.add("0");
        		}
        	}
        	if(Survivors.contains(Person.substring(0, Person.length())))
        	{
        		Tuple.add("1");
        	}
        	else
        	{
        		Tuple.add("0");
        	}
        	Final.add(Tuple);
        }
        
        
        //SQL
        try {
        	DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        	Connection Conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cse4701", "cse4701_user", "cse4701_user");
        	Statement Stmt = Conn.createStatement();
        	
        	String sql = "CREATE TABLE IR_READY( Patient_ID VarCHAR(20), APC int, TP53 int, KRAS int, PIK3CA int, PTEN int, ATM int, MUC4 int, SMAD4 int, SYNE1 int, FBXW7 int, Status int, Primary KEY (Patient_ID) )";
        	Stmt.executeUpdate(sql);
        	
        	for(ArrayList<String> tuple : Final)
        	{
        		boolean First = true;
        		String S = "INSERT INTO IR_READY VALUES ('";
        		for(String field : tuple)
        		{
        			S += field;
        			if(First)
        			{
        				S += "'";
        				First = false;
        			}
        			S += ",";
        		}
        		S = S.substring(0, S.length() - 1);
        		S += ")";
        		Stmt.executeUpdate(S);
        	}
        	sql = "CREATE TABLE PROOF (APC int, TP53 int, KRAS int, PIK3CA int, PTEN int, ATM int, MUC4 int, SMAD4 int, SYNE1 int, FBXW7 int, Status int)";
        	Stmt.executeUpdate(sql);
        	sql = "INSERT INTO PROOF VALUES ("+MutPpl.get("APC").size()+","+MutPpl.get("TP53").size()+","+MutPpl.get("KRAS").size()+","+MutPpl.get("PIK3CA").size()+","+MutPpl.get("PTEN").size()+","+MutPpl.get("ATM").size()+","+MutPpl.get("MUC4").size()+","+MutPpl.get("SMAD4").size()+","+MutPpl.get("SYNE1").size()+","+MutPpl.get("FBXW7").size()+","+Survivors.size()+")";
        	Stmt.executeUpdate(sql);
        }catch (SQLException e)
        {
        	throw new IllegalStateException("Cannot connect", e);
        }
        
    }
}
