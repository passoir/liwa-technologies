/**
 * 
 */
package de.l3s.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import collectionreader.classes.TimesDocumentStatistics;
import de.tudarmstadt.ukp.dkpro.core.type.Sentence;

/**
 * @author tereza
 * 
 */
public class LiwaDatabase {

	private static Connect db;
	public static String dbUsed;

	static {
		db = new Connect();
		
		db.dbbegin();
	}

	public static void endConnection() throws Throwable {
		db.dbend();

	}

	/*
	 * arc processing
	 */

	public static int getLastCrawlId() throws SQLException {
		Statement stmt = db.makeStatement();

		ResultSet rs = stmt.executeQuery("select max(id) as 'id' from arc_crawl");
		int id = 0;
		if (rs.next()) {
			id = rs.getInt("id");
		}
		rs.close();
		stmt.close();

		return id;
	}

	public static boolean existsCrawlId(int cid) {
		Statement stmt = db.makeStatement();
		boolean exists = false;
		ResultSet rs;
		try {
			rs = stmt.executeQuery("select * from arc_crawl where id=" + cid);

			// int id = 0;

			if (rs.next()) {
				int id = rs.getInt("id");
				if (cid == id) {
					exists = true;
				}
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			exists = false;
		}
		return exists;
	}
	public static int existsCrawl(String crName) {
		Statement stmt = db.makeStatement();
		int id = 0;
		ResultSet rs;
		String query ="select * from arc_crawl where filename='"+crName+"'";
		try {
			
			rs = stmt.executeQuery(query);

			

			if (rs.next()) {
				String filename = rs.getString("filename");
				
				
				if (crName.equals(filename)) {
					
					id = rs.getInt("id");
				}
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			
		}
		return id;
	}
	
	public static int existsDocument(String url) {
		Statement stmt = db.makeStatement();
		int id = 0;
		ResultSet rs;
		url = url.replaceAll("'", "");
		try {
			rs = stmt.executeQuery("select * from arc_document where url='" + url+"'");

			

			if (rs.next()) {
				String filename = rs.getString("url");
				if (url == filename) {
					
					id = rs.getInt("id");
				}
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			
		}
		return id;
	}

	public static int insertCrawlMetadata(int id, String filename)
			throws SQLException {
		Statement stmt = db.makeStatement();

		String query = "insert into arc_crawl (id,filename) values (" + id
				+ ",'" + filename + "')";

		stmt = db.makeStatement();
		stmt.execute(query);
		stmt.close();

		return getCrawlId(filename);
		
	}
	
	public static void lastDocumentCleanul() throws SQLException {
		Statement stmt = db.makeStatement();

		String query = "update  low_priority arc_document_lemma dc set dc.id_lemma=(select l.id from arc_lemma l where l.value=dc.lemma), dc.lemma=null where dc.id_lemma=0";

		stmt = db.makeStatement();
		stmt.execute(query);
		stmt.close();

	}

	
	public static int getCrawlId(String filename) throws SQLException {
		Statement stmt = db.makeStatement();

		ResultSet rs = stmt.executeQuery("select id from arc_crawl where filename='"+filename+"'");
		int id = 0;
		if (rs.next()) {
			id = rs.getInt("id");
		}
		rs.close();
		stmt.close();

		return id;
	}

	public static int insertArcDocumentMetadata(int id_crawl,
			String document_filename, String sqlDate) throws SQLException {
		Statement stmt = db.makeStatement();
		document_filename = document_filename.replaceAll("'","");
		String query = "insert into arc_document (url,date) values ('"
				+ document_filename + "','"+sqlDate+"')";

		stmt = db.makeStatement();
		//System.out.println(query);
		stmt.executeUpdate(query);
		stmt.close();
		int id_listitem = 0;
		query = "select max(id) as id  from arc_document where url='"+document_filename+"'";
		stmt = db.makeStatement();

		ResultSet rs = stmt.executeQuery(query);
		if (rs.next()) {
			id_listitem = rs.getInt("id");
		}
		rs.close();
		stmt.close();

		// insert crawl-doc

		query = "insert into arc_crawl_document (id_crawl,id_document,date) values ("
				+ id_crawl + "," + id_listitem + ",'" + sqlDate + "')";
		stmt = db.makeStatement();

		stmt.executeUpdate(query);
		stmt.close();

		return id_listitem;
	}
	
	public static int insertArcDocumentCrawlMetadata(int id_crawl,
			int document_id, String sqlDate) throws SQLException {
		
		// insert crawl-doc

		String query = "insert into arc_crawl_document (id_crawl,id_document,date) values ("
				+ id_crawl + "," + document_id + ",'" + sqlDate + "')";
		Statement stmt = db.makeStatement();

		stmt.executeUpdate(query);
		stmt.close();

		return document_id;
	}

	public static Hashtable<Integer, Integer> insertArcDocumentSentences(
			int doc_id, ArrayList<Sentence> sentences) throws SQLException {
		if (sentences.size() < 1)
			return null;
		Statement stmt = db.makeStatement();

		// dealing with special characters

		String query = "insert into arc_document_sentence (id_document,begin,end,text) values ";
		int count = 0;
		for (Sentence sentence : sentences) {
			String text = sentence.getCoveredText();
			text = text.replace("'", " ");
			// sent = sent.replaceAll("'", " ");
			text = text.replaceAll("\"", " ");
			text = text.replaceAll("[(]|[)]", " ");
			text = text.replaceAll("(\\s|\\n|\\t)+", " ");
			if (count == 0) {
				query += "(" + doc_id + "," + sentence.getBegin() + ","
						+ sentence.getEnd() + ",'" + text + "') ";
				// query_names += "("+l.getId()+",'"+name+"') ";
			} else {
				query += ", (" + doc_id + "," + sentence.getBegin() + ","
						+ sentence.getEnd() + ",'" + text + "') ";
				// query_names += ", ("+l.getId()+",'"+name+"') ";
			}
			count++;
		}

		stmt = db.makeStatement();

		stmt.executeUpdate(query);

		stmt.close();

		Hashtable<Integer, Integer> inserted = new Hashtable<Integer, Integer>();
		query = "select begin,id from arc_document_sentence where id_document="
				+ doc_id;
		stmt = db.makeStatement();

		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			inserted.put(rs.getInt("begin"), rs.getInt("id"));
		}
		rs.close();
		stmt.close();
		return inserted;

	}
	
	//no text is inserted
	public static Hashtable<Integer, Integer> insertArcDocumentSentencesIDs(
			int doc_id, ArrayList<Sentence> sentences) throws SQLException {
		if (sentences.size() < 1)
			return null;
		Statement stmt = db.makeStatement();

		// dealing with special characters

		String query = "insert into arc_document_sentence (id_document,begin,end,text) values ";
		int count = 0;
		for (Sentence sentence : sentences) {
			
			
			if (count == 0) {
				query += "(" + doc_id + "," + sentence.getBegin() + ","
						+ sentence.getEnd() + ",'') ";
				// query_names += "("+l.getId()+",'"+name+"') ";
			} else {
				query += ", (" + doc_id + "," + sentence.getBegin() + ","
						+ sentence.getEnd() + ",'') ";
				// query_names += ", ("+l.getId()+",'"+name+"') ";
			}
			count++;
		}

		stmt = db.makeStatement();

		stmt.executeUpdate(query);

		stmt.close();

		Hashtable<Integer, Integer> inserted = new Hashtable<Integer, Integer>();
		query = "select begin,id from arc_document_sentence where id_document="
				+ doc_id;
		stmt = db.makeStatement();

		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			inserted.put(rs.getInt("begin"), rs.getInt("id"));
		}
		rs.close();
		stmt.close();
		return inserted;

	}
	
	public static int getCommonArcLemmas(
			HashSet<String> lemmas, int id_document) throws SQLException {
		if (lemmas.size() < 1)
			return 0;
		Statement stmt = db.makeStatement();

		String selectQuery = "select count(l.value) as 'count' from arc_lemma l,arc_document_lemma dl where dl.id_document="+id_document+" and dl.id_lemma=l.id and l.value in (";

		int count = 0;
		Iterator<String> lIt = lemmas.iterator();
		while (lIt.hasNext()) {
			String lemma = lIt.next();
			if (count == 0) {
				// query += "('" + lemma + "') ";
				selectQuery += "'" + lemma + "'";
			} else {
				// query += ", ('" + lemma + "') ";
				selectQuery += ",'" + lemma + "'";
			}

			count++;
		}
		selectQuery += ")";

		int size=0;
		
		ResultSet rs = stmt.executeQuery(selectQuery);
		while (rs.next()) {
			try{
				size = rs.getInt("count");
			}catch(NullPointerException e){
				System.out.println("no lemmas retrieved");
				continue;
			}
		}
		rs.close();
		stmt.close();
		return size;

	}
	// first select them to check if they were inserted, then insert the missing

	public static Hashtable<String, Integer> insertArcLemmas(
			HashSet<String> lemmas) throws SQLException {
		if (lemmas.size() < 1)
			return null;
		Statement stmt = db.makeStatement();

		String selectQuery = "select * from arc_lemma where value in (";

		int count = 0;
		Iterator<String> lIt = lemmas.iterator();
		while (lIt.hasNext()) {
			String lemma = lIt.next();
			if (count == 0) {
				// query += "('" + lemma + "') ";
				selectQuery += "'" + lemma + "'";
			} else {
				// query += ", ('" + lemma + "') ";
				selectQuery += ",'" + lemma + "'";
			}

			count++;
		}
		selectQuery += ")";

		Hashtable<String, Integer> lemmasHash = new Hashtable<String, Integer>();
		//System.out.println(selectQuery);
		ResultSet rs = stmt.executeQuery(selectQuery);
		while (rs.next()) {
			try{
				lemmasHash.put(rs.getString("value"), rs.getInt("id"));
			}catch(NullPointerException e){
				System.out.println("no lemmas retrieved");
				continue;
			}
		}
		rs.close();
		stmt.close();
		
		String query = "insert into arc_lemma (value) values ";
		selectQuery = "select * from arc_lemma where value in (";
		lIt = lemmas.iterator();
		count = 0;
		while (lIt.hasNext()) {
			String lemma = lIt.next();
			if (!lemmasHash.containsKey(lemma)) {
				if (count == 0) {
					query += "('" + lemma + "') ";
					selectQuery += "'" + lemma + "'";
				} else {
					query += ", ('" + lemma + "') ";
					selectQuery += ",'" + lemma + "'";
				}
				count++;
			}

		}
		selectQuery += ")";

		if (count > 0) {
			stmt = db.makeStatement();
		//	System.out.println(query);
			stmt.executeUpdate(query);
			
			stmt.close();

			stmt = db.makeStatement();
			rs = stmt.executeQuery(selectQuery);
		//	System.out.println(selectQuery);
			while (rs.next()) {
				try{
				if (!lemmasHash.containsKey(rs.getString("value"))) {
					lemmasHash.put(rs.getString("value"), rs.getInt("id"));
				}
				}catch(NullPointerException e){
					System.out.println("no lemmas retrieved");
					continue;
				}
			}
			stmt.close();

		}
	//	System.out.println("ended successfully");
		return lemmasHash;

	}
	
	public static void insertArcLemmasSimple(
			HashSet<String> lemmas) throws SQLException {
		if (lemmas.size() < 1)
			return;
		Statement stmt = db.makeStatement();

		

		
		
		
		String query = "insert  delayed ignore into arc_lemma (value) values ";
		
		Iterator<String> lIt = lemmas.iterator();
		int count = 0;
		while (lIt.hasNext()) {
			String lemma = lIt.next();
			
				if (count == 0) {
					query += "('" + lemma + "') ";
					//selectQuery += "'" + lemma + "'";
				} else {
					query += ", ('" + lemma + "') ";
				//	selectQuery += ",'" + lemma + "'";
				}
				count++;
			

		}
		//selectQuery += ")";

		if (count > 0) {
			stmt = db.makeStatement();
		//	System.out.println(query);
			stmt.executeUpdate(query);
			
			stmt.close();

			

		}
	//	System.out.println("ended successfully");
	
	}


	public static Hashtable<String, Integer> getArcConjunctions()
			throws SQLException {
		Hashtable<String, Integer> conj = new Hashtable<String, Integer>();

		Statement stmt = db.makeStatement();
		ResultSet rs = stmt.executeQuery("select * from arc_conj");
		while (rs.next()) {
			conj.put(rs.getString("value"), rs.getInt("id"));
		}
		rs.close();
		stmt.close();
		return conj;
	}

	public static void insertArcDocumentLemmas(ArrayList<String> tokenstrings)
			throws SQLException {
		if (tokenstrings.size() < 1)
			return;
		Statement stmt = db.makeStatement();

		// dealing with special characters

		String query = "insert into arc_document_lemma (id_document,id_lemma,begin,end,pos,id_sentence) values ";
		int count = 0;
		for (String tokenstring : tokenstrings) {

			if (count == 0) {
				query += tokenstring;
				// query_names += "("+l.getId()+",'"+name+"') ";
			} else {
				query += ", " + tokenstring;
				// query_names += ", ("+l.getId()+",'"+name+"') ";
			}
			count++;
		}

		stmt = db.makeStatement();

		stmt.executeUpdate(query);

		stmt.close();

	}

	public static void insertArcDocumentLemmasNoIds(
			ArrayList<String> tokenstrings) throws SQLException {
		if (tokenstrings.size() < 1)
			return;
		Statement stmt = db.makeStatement();

		// dealing with special characters

		String query = "insert delayed into arc_document_lemma (id_document,id_lemma,begin,end,pos,id_sentence,lemma) values ";
		int count = 0;
		for (String tokenstring : tokenstrings) {

			if (count == 0) {
				query += tokenstring;
				// query_names += "("+l.getId()+",'"+name+"') ";
			} else {
				query += ", " + tokenstring;
				// query_names += ", ("+l.getId()+",'"+name+"') ";
			}
			count++;
		}

		stmt = db.makeStatement();

		stmt.executeUpdate(query);

		stmt.close();

	}

	public static void insertArcDocumentConj(ArrayList<String> tokenstrings)
			throws SQLException {
		if (tokenstrings.size() < 1)
			return;
		Statement stmt = db.makeStatement();

		// dealing with special characters

		String query = "insert delayed into arc_document_conj (id_document,id_conj,begin,end,id_sentence) values ";
		int count = 0;
		for (String tokenstring : tokenstrings) {

			if (count == 0) {
				query += tokenstring;
				// query_names += "("+l.getId()+",'"+name+"') ";
			} else {
				query += ", " + tokenstring;
				// query_names += ", ("+l.getId()+",'"+name+"') ";
			}
			count++;
		}

		stmt = db.makeStatement();

		stmt.executeUpdate(query);

		stmt.close();

	}
	
	
	/*
	 * graph selecting part
	 * 
	 */
	
	public static void insertTuples(HashMap<String,Integer> graph, String date1,String date2) throws SQLException{
		String query = "insert into arc_graph (id_lemma1,id_lemma2,count,date1,date2) values ";
		
		System.out.println("Inserting tuples in range: "+date1+" to "+date2);
		Iterator it = graph.keySet().iterator();
		int c = 0;
		ArrayList<String> queryChunks = new ArrayList<String>();
		String queryChunk=query;
		while(it.hasNext()){
			String tuple = (String)it.next();
			int count = graph.get(tuple);
			String id_l1 = tuple.substring(0,tuple.indexOf("_"));
			String id_l2 = tuple.substring(tuple.indexOf("_")+1);
			String tokenstring = "("+id_l1+","+id_l2+","+count+",'"+date1+"','"+date2+"')";
			if (c == 0) {
				queryChunk += tokenstring;
				c++;
			} else if(c<5000){
				queryChunk += ", " + tokenstring;
				c++;
			}else if(c==5000){
				queryChunk += ", " + tokenstring;
				queryChunks.add(queryChunk);
				queryChunk=query;
				System.out.println(c);
				c=0;
				
			}
		}
		if(c>0){
			System.out.println(c);
			queryChunks.add(queryChunk);
		}
		System.out.println(queryChunks.size());
		for(String q:queryChunks){
			
		Statement stmt = db.makeStatement();
			stmt = db.makeStatement();
			//System.out.println(q);
			stmt.executeUpdate(q);

			stmt.close();
		}
		
		
		
	}
	
	public static HashMap<String,Integer> getTuplesInDateRange(String date1, String date2) throws SQLException{
		Statement stmt = db.makeStatement();
		System.out.println("Getting tuples in range: "+date1+" to "+date2);
		// dealing with special characters

		
		//String query = "SELECT distinct a1.id_lemma as 'id1',a1.begin as 'b1',a2.id_lemma as 'id2',a2.begin as 'b2',a1.id_document FROM arc_document_lemma a1,arc_document_lemma a2,arc_document_conj ac,arc_document cd where cd.id=a1.id_document and cd.date>='"+date1+"'and cd.date<='"+date2+"' and a1.id_document=a2.id_document and a1.id_document=ac.id_document and a1.end=ac.begin-1 and ac.end = a2.begin-1 ";
	//ss	String query = "SELECT distinct  a1.id_lemma as 'id1',a1.begin as 'b1',a2.id_lemma as 'id2',a2.begin as 'b2',a1.id_document FROM arc_document_lemma a1,arc_document_lemma a2,arc_document_conj ac where a1.id_document in (select id from arc_document where date>='"+date1+"'and date<='"+date2+"') and a1.id_document=a2.id_document and a1.id_document=ac.id_document and a1.end=ac.begin-1 and ac.end = a2.begin-1";
	//	SELECT distinct a1.id_lemma as 'id1',a1.begin as 'b1',a2.id_lemma as 'id2',a2.begin as 'b2',a1.id_document FROM arc_document_lemma a1,arc_document_lemma a2,arc_document_conj ac where a1.id_document in (select id from arc_document where date>='2006-10-01'and date<='2006-12-31') and a1.id_document=a2.id_document and a1.id_document=ac.id_document and a1.end=ac.begin-1 and ac.end = a2.begin-1
	//	String query = "SELECT  distinct a1.id_lemma as 'id1',a1.begin as 'b1',a2.id_lemma as 'id2',a2.begin as 'b2',a1.id_document FROM arc_document_lemma a1,arc_document_lemma a2,arc_document_conj ac, arc_document d where a1.id_lemma <> a2.id_lemma and a1.id_document = d.id and d.date>='"+date1+"'and d.date<='"+date2+"' and a1.id_document=a2.id_document and a1.id_document=ac.id_document and a1.end=ac.begin-1 and ac.end = a2.begin-1";
		String query = "SELECT  distinct a1.id_lemma as 'id1',a1.begin as 'b1',a2.id_lemma as 'id2',a2.begin as 'b2',a1.id_document FROM arc_document_lemma a1,arc_document_lemma a2, arc_document d where a1.id_lemma <> a2.id_lemma and a1.id_document = d.id and d.date>='"+date1+"'and d.date<='"+date2+"' and a1.id_document=a2.id_document and a2.begin-a1.end<=4 and a1.id_sentence=a2.id_sentence";
		System.out.println(query);
		stmt = db.makeStatement();
		stmt.setQueryTimeout(100000);
		ResultSet rs = stmt.executeQuery(query);
		
		
		HashMap<String,Integer> graph = new HashMap<String, Integer>();
		
		HashMap<String,String> auxTuplesLR = new HashMap<String, String>();
		HashMap<String,String> auxTuplesRL = new HashMap<String, String>();
		
		
		while(rs.next()){
			int id1= rs.getInt("id1");
			int id2 = rs.getInt("id2");
			int beg1 = rs.getInt("b1");
			int beg2 = rs.getInt("b2");
			int doc = rs.getInt("id_document");
		
			
			String tuple = id1+"_"+id2;
			int count = 1;
			if(graph.containsKey(tuple)){
				count += graph.get(tuple);
			}
			graph.put(tuple, count);
			
			
			String l = id1+"_"+beg1+"_"+doc;
			String r = id2+"_"+beg2+"_"+doc;
			
			auxTuplesLR.put(l, r);
			auxTuplesRL.put(r, l);
		}
		stmt.close();
		Iterator rightIt = auxTuplesRL.keySet().iterator();
		while(rightIt.hasNext()){
			String b = (String)rightIt.next();
			
			
			if(auxTuplesLR.containsKey(b)){
				String c = auxTuplesLR.get(b);
				String a = auxTuplesRL.get(b);
				
				c = c.substring(0, c.indexOf("_"));
				a = a.substring(0, a.indexOf("_"));
				String tuple = a+"_"+c;
				int count = 1;
				if(graph.containsKey(tuple)){
					count += graph.get(tuple);
				}
				graph.put(tuple, count);
			}
		}

		
		return graph;
		
	}
	
	public static String getGraphDate(String date) throws SQLException{
		Statement stmt = db.makeStatement();
		System.out.println("Getting graph around: "+date);
		// dealing with special characters

		String query = "SELECT distinct l1.value as 'l1', l2.value as 'l2', a.count FROM arc_graph a,arc_lemma l1,arc_lemma l2 where l1.id=a.id_lemma1 and l2.id=a.id_lemma2 and date1<='"+date+"' and date2>='"+date+"'";

		stmt = db.makeStatement();
		stmt.setQueryTimeout(5000);
		String output="";
		ResultSet rs = stmt.executeQuery(query);
		
		
		
		
		while(rs.next()){
			String l1= rs.getString("l1");
			String l2= rs.getString("l2");
			int count = rs.getInt("count");
		
			
			output += l1+"\t"+l2+"\t"+count+"\n";
		
		}
		
		return output;
		
	}

	/*
	 * @see postprocessing methods
	 */

	public static void updateLemmaTable() throws SQLException {
		Statement stmt = db.makeStatement();

		String query = "insert into document_lemma (lemma) select distinct t.lemma from document_token t where t.lemma not in (select lemma from document_lemma)";

		stmt = db.makeStatement();

		stmt.executeUpdate(query);
		stmt.close();
	}

	/*
	 * 
	 * @see to be called after updateLemmaTable
	 */
	public static void updateTokenTable() throws SQLException {
		Statement stmt = db.makeStatement();

		String query = "update document_token dt set dt.id_lemma = (select id from document_lemma dl where dl.lemma = dt.lemma)";

		stmt = db.makeStatement();

		stmt.executeUpdate(query);
		stmt.close();
	}

	public static void selectSentenceSimpleCooccurrence() {
		// SELECT d1.id_lemma, d2.id_lemma, count(d1.id_sentence) from
		// document_token d1, document_token d2 where d1.id_sentence =
		// d2.id_sentence group by d1.id_lemma, d2.id_lemma limit 100
	}

	/*
	 * end postprocessing
	 */

	/*
	 * 
	 * @see:annotationsToDB methods
	 */

	public static int insertDocumentMetadata(String title,
			String document_filename, String sqlDate) throws SQLException {
		Statement stmt = db.makeStatement();

		title = title.replace("'", "\\'");

		String query = "insert into document (title,filename,date) values ('"
				+ title + "','" + document_filename + "','" + sqlDate + "')";

		stmt = db.makeStatement();

		stmt.executeUpdate(query);
		stmt.close();
		int id_listitem = 0;
		query = "select max(id) as id from document";
		stmt = db.makeStatement();

		ResultSet rs = stmt.executeQuery(query);
		if (rs.next()) {
			id_listitem = rs.getInt("id");
		}
		rs.close();
		stmt.close();
		return id_listitem;
	}

	public static Hashtable<Integer, Integer> insertDocumentSentences(
			int doc_id, ArrayList<Sentence> sentences) throws SQLException {
		Statement stmt = db.makeStatement();

		// dealing with special characters

		String query = "insert into document_sentence (id_document,begin,end,text) values ";
		int count = 0;
		for (Sentence sentence : sentences) {
			String text = sentence.getCoveredText();
			text = text.replace("'", " ");
			// sent = sent.replaceAll("'", " ");
			text = text.replaceAll("\"", " ");
			text = text.replaceAll("[(]|[)]", " ");
			text = text.replaceAll("(\\s|\\n|\\t)+", " ");
			if (count == 0) {
				query += "(" + doc_id + "," + sentence.getBegin() + ","
						+ sentence.getEnd() + ",'" + text + "') ";
				// query_names += "("+l.getId()+",'"+name+"') ";
			} else {
				query += ", (" + doc_id + "," + sentence.getBegin() + ","
						+ sentence.getEnd() + ",'" + text + "') ";
				// query_names += ", ("+l.getId()+",'"+name+"') ";
			}
			count++;
		}

		stmt = db.makeStatement();

		stmt.executeUpdate(query);

		stmt.close();

		Hashtable<Integer, Integer> inserted = new Hashtable<Integer, Integer>();
		query = "select begin,id from document_sentence where id_document="
				+ doc_id;
		stmt = db.makeStatement();

		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			inserted.put(rs.getInt("begin"), rs.getInt("id"));
		}
		rs.close();
		stmt.close();
		return inserted;

	}

	public static void insertDocumentTokens(ArrayList<String> tokenstrings)
			throws SQLException {
		Statement stmt = db.makeStatement();

		// dealing with special characters

		String query = "insert into document_token (id_document,begin,end,text,lemma,pos,id_sentence) values ";
		int count = 0;
		for (String tokenstring : tokenstrings) {

			if (count == 0) {
				query += tokenstring;
				// query_names += "("+l.getId()+",'"+name+"') ";
			} else {
				query += ", " + tokenstring;
				// query_names += ", ("+l.getId()+",'"+name+"') ";
			}
			count++;
		}

		stmt = db.makeStatement();

		stmt.executeUpdate(query);

		stmt.close();

	}

	/*
	 * end section
	 */

	public static void addStatisticTerms(Hashtable<String, Integer> dict,
			int year, String dictionary_type) throws SQLException {

		Enumeration<String> terms = dict.keys();

		while (terms.hasMoreElements()) {
			String term = terms.nextElement();

			int exists = existsStatisticsTerm(term);

			if (exists != 0) {
				insertStatisticTermFrequency(exists, dict.get(term), year,
						dictionary_type);
			} else {
				int id_term = insertStatisticTerm(term);
				insertStatisticTermFrequency(id_term, dict.get(term), year,
						dictionary_type);
			}
		}

	}

	private static int insertStatisticTerm(String name) throws SQLException {
		Statement stmt = db.makeStatement();

		// dealing with special characters
		name = name.replace("'", "\\'");

		String query = "insert into statistics_term (term) values ('" + name
				+ "')";
		stmt = db.makeStatement();

		stmt.executeUpdate(query);
		stmt.close();
		int id_listitem = 0;
		query = "select max(id) as id from statistics_term";
		stmt = db.makeStatement();

		ResultSet rs = stmt.executeQuery(query);
		if (rs.next()) {
			id_listitem = rs.getInt("id");
		}
		rs.close();
		stmt.close();
		return id_listitem;
	}

	private static void insertStatisticTermFrequency(int id, int tf, int year,
			String dictionary_type) throws SQLException {
		Statement stmt = db.makeStatement();

		// dealing with special characters

		String query = "insert into statistics_term_frequency (id_term,tf,year,dictionary_type) values ("
				+ id + "," + tf + "," + year + ",'" + dictionary_type + "')";
		stmt = db.makeStatement();

		stmt.executeUpdate(query);

		stmt.close();

	}

	private static int existsStatisticsTerm(String name) throws SQLException {
		Statement stmt = db.makeStatement();
		name = name.replace("'", "\\'");
		ResultSet rs = stmt
				.executeQuery("select id from statistics_term where term = '"
						+ name + "'");
		int id = 0;
		while (rs.next()) {
			id = rs.getInt("id");
		}
		rs.close();
		stmt.close();

		return id;
	}

	public static Set<String> getArticlesForYear(int year) throws SQLException {
		Statement stmt = db.makeStatement();
		Set<String> titles = new HashSet<String>();
		ResultSet rs = stmt
				.executeQuery("select title from statistics_timesdocument where year = "
						+ year + "");
		String title = "";
		while (rs.next()) {
			title = rs.getString("title");
			titles.add(title);
		}
		rs.close();
		stmt.close();

		return titles;
	}

	public static void insertStatisticsArchive(
			List<TimesDocumentStatistics> stats) throws SQLException {
		Statement stmt = db.makeStatement();

		// dealing with special characters

		String query = "insert into statistics_timesdocument (year,title,length,wordcount) values ";
		int count = 0;
		for (TimesDocumentStatistics stat : stats) {
			if (count == 0) {
				query += "(" + stat.getYear() + ",'" + stat.getFilename()
						+ "'," + stat.getLength() + "," + stat.getNbWords()
						+ ") ";
				// query_names += "("+l.getId()+",'"+name+"') ";
			} else {
				query += ", (" + stat.getYear() + ",'" + stat.getFilename()
						+ "'," + stat.getLength() + "," + stat.getNbWords()
						+ ") ";
				// query_names += ", ("+l.getId()+",'"+name+"') ";
			}
			count++;
		}

		stmt = db.makeStatement();

		stmt.executeUpdate(query);

		stmt.close();

	}

}
