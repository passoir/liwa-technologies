/**
 * 
 */
package annotators;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.analysis_engine.ResultSpecification;
import org.apache.uima.analysis_engine.annotator.AnnotatorProcessException;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.l3s.database.LiwaDatabase;
import de.tudarmstadt.ukp.dkpro.core.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.type.POS;
import de.tudarmstadt.ukp.dkpro.core.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.type.Token;
import org.apache.uima.UimaContext;

import types.ArticleText;
import types.Crawl;
import types.Date;
import types.Title;

/**
 * @author tereza
 * 
 */
public class AnnotationsToDBAnnotator2 extends JCasAnnotator_ImplBase {

	private static HashSet<String> PosNoun = new HashSet<String>();
	private static HashSet<String> PosDet = new HashSet<String>();
	private static HashSet<String> PosConj = new HashSet<String>();
	private static HashSet<String> Conjunctions = new HashSet<String>();

	private static Integer crId;
	
	private static double OVERLAP_THRESHOLD =  0.8;

	/**
	 * 
	 */
	public AnnotationsToDBAnnotator2() {
		// TODO Auto-generated constructor stub
	}

	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);
		// filter of parts of speech that are used
		// POSHash.add("JJ");// adj
		// POSHash.add("RB");// adv
		// PosNoun.add("CC");// Conj
		PosNoun.add("NP");
		PosNoun.add("NNS");
		PosNoun.add("NN");
		// POSHash.add("VVD"); // V

		PosDet.add("DT");
		PosDet.add("CD");

		PosConj.add("CC");
		PosConj.add(",");

		Conjunctions.add("and");
		Conjunctions.add("or");
		Conjunctions.add(",");

		try {
			crId = LiwaDatabase.getLastCrawlId();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			crId = 0;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.uima.analysis_engine.annotator.JTextAnnotator#process(org.
	 * apache.uima.jcas.JCas,
	 * org.apache.uima.analysis_engine.ResultSpecification)
	 */
	public void process(JCas arg0, ResultSpecification arg1)
			throws AnnotatorProcessException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @author Tereza Iofciu
	 * 
	 * @see
	 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org
	 * .apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// TODO Auto-generated method stub
		// document parameters

		if(aJCas == null){
			System.out.println("jcas null");
			return;
		}
		String filename = "";
		String crawlFile = "";
		int crawlId = 0;
		int document_id;
		Date date = null;

		AnnotationIndex articleIndex = aJCas
				.getAnnotationIndex(ArticleText.type);
		AnnotationIndex sentenceIndex = aJCas.getAnnotationIndex(Sentence.type);
		AnnotationIndex tokenIndex = aJCas.getAnnotationIndex(Token.type);
		AnnotationIndex lemmaIndex = aJCas.getAnnotationIndex(Lemma.type);
		AnnotationIndex posIndex = aJCas.getAnnotationIndex(POS.type);

		AnnotationIndex dateIndex = aJCas.getAnnotationIndex(Date.type);
		AnnotationIndex titleIndex = aJCas.getAnnotationIndex(Title.type);
		AnnotationIndex crawlIndex = aJCas.getAnnotationIndex(Crawl.type);

		// get document filename

		/*
		 * FSIterator it = aJCas
		 * .getAnnotationIndex(SourceDocumentInformation.type).iterator(); File
		 * outFile = null; if (it.hasNext()) { SourceDocumentInformation fileLoc
		 * = (SourceDocumentInformation) it .next(); filename =
		 * fileLoc.getUri().substring( 1 + fileLoc.getUri().lastIndexOf("/")); }
		 */
		// get document filename

		FSIterator titleIt = titleIndex.iterator();
		if (titleIt.hasNext()) {
			filename = ((Title) titleIt.next()).getCoveredText();
		}
	//	System.out.println("started :" + filename);
		// get crawlInfo

		FSIterator crawlIt = crawlIndex.iterator();
		if (crawlIt.hasNext()) {
			Crawl cr = (Crawl) crawlIt.next();
			crawlId = cr.getCrawlId();
			crawlFile = cr.getFilename();
			// System.out.println(crawlId+" "+crawlFile);
		}

		// get document date

		FSIterator dateIt = dateIndex.iterator();
		if (dateIt.hasNext()) {
			date = (Date) dateIt.next();

		}

		// insert crawl, document and get id
		// new crawl
		try {
			//if (crId == 0) {
				 crawlId = LiwaDatabase.existsCrawl(crawlFile);
				if (crawlId==0) {

				 crawlId=LiwaDatabase.insertCrawlMetadata(crawlId, crawlFile);

				}
				crId = crawlId;
			//}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String sqlDate = date.getYear() + "-" + date.getMonth() + "-"
				+ date.getDay();
		
			
				
		
			try {
				
				Hashtable<String, Integer> conjHash = LiwaDatabase.getArcConjunctions();
				/*
				 * getting sentences
				 */
				FSIterator articleIterator = articleIndex.iterator();
				ArticleText articleText = null;
				if (articleIterator.hasNext()) {
					articleText = (ArticleText) articleIterator.next();
				}
				if (articleText == null) {
					System.out.println("article text is null");
					return;
				}

				FSIterator sentenceIterator = sentenceIndex
						.subiterator(articleText);
				HashSet<Integer> detPosition = new HashSet<Integer>();
				HashSet<Integer> conjStart = new HashSet<Integer>();
				HashSet<Integer> conjEnd= new HashSet<Integer>();
				
				// sentence DB part
				ArrayList<Sentence> sentences = new ArrayList<Sentence>();
				while (sentenceIterator.hasNext()) {
					Sentence sentence = (Sentence) sentenceIterator.next();
					if (sentence.getCoveredText() != null) {
						sentences.add(sentence);

						FSIterator posIterator = posIndex.subiterator(sentence);
						while (posIterator.hasNext()) {

							POS pos = (POS) posIterator.next();
							if (PosDet.contains(pos.getPosValue())) {
								detPosition.add(pos.getBegin());
							}
							// System.out.println(pos.toString());
							// System.out.println(i+" "+token_pos+pos.getCoveredText());

							
							if (PosConj.contains(pos.getPosValue())) {

								String text = "";

								text = pos.getCoveredText().toLowerCase();
								// System.out.println(text);
								if (text.contains(",")) {
									text = ",";
								} else if (text.contains("and")
										|| text.contains("&")) {
									text = "and";
								} else if (text.contains("or")) {
									text = "or";
								}
								if (conjHash.containsKey(text)) {
									conjStart.add(pos.getBegin());								
									conjEnd.add(pos.getEnd());
								}

							}
						}
					}
				}
				/*
				 * getting lemmas
				 */
				HashSet<String> lemmas = new HashSet<String>();
				// get all lemmas to insert dictionary into db

				FSIterator tokIterator = tokenIndex.subiterator(articleText);
				while (tokIterator.hasNext()) {
					Token token = (Token) tokIterator.next();
					String token_pos = "";
					FSIterator posIterator = posIndex.subiterator(token);
					int i = 0;
					// System.out.println(i+" "+token.getCoveredText());

					while (posIterator.hasNext()) {
						i++;
						POS pos = (POS) posIterator.next();
						token_pos = pos.getPosValue();
						// System.out.println(i+" "+pos.toString());
						// System.out.println(i+" "+token_pos+pos.getCoveredText());

					}

					// it is a wanted noun
					if (PosNoun.contains(token_pos)) {
						FSIterator lemmaIterator = lemmaIndex.subiterator(token);
						while (lemmaIterator.hasNext()) {
							Lemma lemma = (Lemma) lemmaIterator.next();
							String text = lemma.getValue().toLowerCase();
							text = text.replace("'", "");
							text = text.replaceAll("\"", "");
							text = text.replaceAll("[(]|[)]", "");
							text = text.replaceAll("(\\s|\\n|\\t)+", "");
							if (text.length()>0 && !lemmas.contains(text)) {
								lemmas.add(text);
							}
						}
					}
				}
				//no point in adding a document with no nouns
				if(lemmas.size()==0){
					return;
				}
				
				document_id= LiwaDatabase.existsDocument(filename);
				
				
				//if document exists in the db.. check if it is the same
				if(document_id>0){
					int common = LiwaDatabase.getCommonArcLemmas(lemmas, document_id);
					double overlap = (double)common/lemmas.size();
					
					if(overlap>=OVERLAP_THRESHOLD){
						System.out.println("documents overlap"+document_id);
						LiwaDatabase.insertArcDocumentCrawlMetadata(crId,document_id, sqlDate);
						return;
					}
				}
				try{
					document_id = LiwaDatabase.insertArcDocumentMetadata(crId,
						filename, sqlDate);
				}catch(SQLException se){
					System.out.println("documents with same name in the same crawl...");
					filename += ".duplicate";
					document_id = LiwaDatabase.insertArcDocumentMetadata(crId,
							filename, sqlDate);
				}
			// iterate over Sentences
			
			
		//	Hashtable<Integer, Integer> insertedSentences = LiwaDatabase.insertArcDocumentSentences(document_id, sentences);
				
				//inserting just sentence delimiters, no text
				Hashtable<Integer, Integer> insertedSentences = LiwaDatabase.insertArcDocumentSentencesIDs(document_id, sentences);
			// lemmas to DB

			// document_token table structure:
			// document_id,begin,end,text,lemma,pos,id_sentence
			
			ArrayList<String> tokenstrings = new ArrayList<String>();
			ArrayList<String> conjuctionstrings = new ArrayList<String>();
			

			Hashtable<String, Integer> lemmasHash = LiwaDatabase
					.insertArcLemmas(lemmas);
			

			ArrayList<String> lemmaStrings = new ArrayList<String>();
			boolean prevDet = false;
			int prevOffset = 0;

			for (Sentence sentence : sentences) {
				if (insertedSentences.containsKey(sentence.getBegin())) {

					int sentence_id = insertedSentences
							.get(sentence.getBegin());

					// System.out.println("sentence: "+
					// sentence.getCoveredText());
					// iterate over Tokens
					FSIterator tokenIterator = tokenIndex.subiterator(sentence);

					while (tokenIterator.hasNext()) {
						Token token = (Token) tokenIterator.next();
						String token_pos = "";
						FSIterator posIterator = posIndex.subiterator(token);
						int i = 0;
						while (posIterator.hasNext()) {
							POS pos = (POS) posIterator.next();
							token_pos = pos.getPosValue();
							// System.out.println(i+" "+token_pos+token.getCoveredText());
							i++;
						}
						// System.out.println(token.getCoveredText());
						// filter out the unwanted tokens
						if (PosNoun.contains(token_pos)) {
							int begin = token.getBegin();
							// System.out.println(prevPos);
							if (prevDet) {
								begin = prevOffset;
								// System.out.println("offset moved");
							}
							//check if there is a conjunction before or after..
							if(conjEnd.contains(begin) || conjStart.contains(token.getEnd())){
							String tokenstring = "(" + document_id + ",";
							String text = "";
							/*
							 * text = token.getCoveredText().toLowerCase(); text
							 * = text.replace("'", " "); text =
							 * text.replaceAll("\"", " "); text =
							 * text.replaceAll("[(]|[)]", " "); text =
							 * text.replaceAll("(\\s|\\n|\\t)+", " ");
							 */
							// get lemma id
							FSIterator lemmaIterator = lemmaIndex
									.subiterator(token);
							while (lemmaIterator.hasNext()) {
								Lemma lemma = (Lemma) lemmaIterator.next();
								text = lemma.getValue().toLowerCase();
								text = text.replace("'", "");
								text = text.replaceAll("\"", "");
								text = text.replaceAll("[(]|[)]", "");
								text = text.replaceAll("(\\s|\\n|\\t)+", "");
							}
							if(text.length()>0){
							int lemmaId = 0;
							try {
								lemmaId = lemmasHash.get(text);
							} catch (NullPointerException e) {
								System.out.println("lemma not found: " + text
										+ " " + lemmasHash.size() + " "
										+ lemmas.size() + "\n");
								Iterator<String> it = lemmas.iterator();
								while (it.hasNext()) {
									System.out.print(it.next() + " ");
								}
								System.out.println("\n\n");
								it = lemmasHash.keySet().iterator();
								while (it.hasNext()) {
									System.out.print(it.next() + " ");
								}
								System.out.println("\n\n");
							}
							tokenstring += lemmaId + "," + begin + ","
									+ token.getEnd() + ",'" + token_pos + "',"
									+ sentence_id + ")";

							tokenstrings.add(tokenstring);
							}
						}
						}

						if (PosConj.contains(token_pos)) {

							String text = "";

							text = token.getCoveredText().toLowerCase();
							// System.out.println(text);
							if (text.contains(",")) {
								text = ",";
							} else if (text.contains("and")
									|| text.contains("&")) {
								text = "and";
							} else if (text.contains("or")) {
								text = "or";
							}
							if (conjHash.containsKey(text)) {
								int conjId = conjHash.get(text);
								String tokenstring = "(" + document_id + ","
										+ conjId + "," + token.getBegin() + ","
										+ token.getEnd() + "," + sentence_id
										+ ")";

								conjuctionstrings.add(tokenstring);
							}

						}

						if (detPosition.contains(token.getBegin())) {
							prevOffset = token.getBegin();
							prevDet = true;
						} else {
							prevDet = false;
						}

					}

				}

			}
			if (tokenstrings.size() > 0) {
				LiwaDatabase.insertArcDocumentLemmas(tokenstrings);
			}
			if (conjuctionstrings.size() > 0) {
				LiwaDatabase.insertArcDocumentConj(conjuctionstrings);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	//	System.out.println("finished :" + filename);

	}

}
