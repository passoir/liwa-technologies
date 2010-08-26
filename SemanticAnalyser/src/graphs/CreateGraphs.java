/**
 * 
 */
package graphs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import de.l3s.database.Connect;
import de.l3s.database.LiwaDatabase;

/**
 * @author tereza
 *
 */
public class CreateGraphs {

	public static String dbUsed;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String date1;
		String date2;
		
		String date3;
		
		
		if(args.length < 3){
			System.out.println("Usage date1 date2 date3 [db=o] (where date like yyyy-mm-dd and database p is default)");
	//	 date1 = "2006-10-01";
		// date2 = "2006-12-31";
		
		
		//	date3 = "2006-12-09";
			return;
		}else{
			 date1 = args[0];
			 date2 = args[1];
			 date3 = args[2];
			 
		}
		if(args.length>2){
		System.out.println(args[3]);
			dbUsed =args[3]; 
		
		}
		
	/*	String date1 = "2009-01-01";
			String date2 = "2009-12-31";
			
			String date3 = "2009-12-09";
		*/
		String graphout = "";
		String outFile = date3+"graph_march.txt";
		try {
			HashMap<String,Integer> graph = LiwaDatabase.getTuplesInDateRange(date1,date2);
			LiwaDatabase.insertTuples(graph, date1, date2);
			
			graphout = LiwaDatabase.getGraphDate(date3);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			FileWriter fw = new FileWriter(new File(outFile));
			fw.write(graphout);
			
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
