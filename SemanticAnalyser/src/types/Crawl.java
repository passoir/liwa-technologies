/* First created by JCasGen Tue Feb 16 14:55:52 CET 2010 */
package types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

/** 
 * Updated by JCasGen Sat May 29 13:45:57 CEST 2010
 * XML source: /home/tereza/eclipse/SemanticAnalyser/desc/ARCReaderDescriptor.xml
 * @generated */
public class Crawl extends Annotation {
	/**
	 * @generated
	 * @ordered
	 */
	public final static int typeIndexID = JCasRegistry.register(Crawl.class);
	/**
	 * @generated
	 * @ordered
	 */
	public final static int type = typeIndexID;

	/** @generated */
	public int getTypeIndexID() {return typeIndexID;}
 
	/**
	 * Never called. Disable default constructor
	 * 
	 * @generated
	 */
	protected Crawl() {}
    
	/**
	 * Internal - constructor used by generator
	 * 
	 * @generated
	 */
	public Crawl(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
	/** @generated */
	public Crawl(JCas jcas) {
    super(jcas);
    readObject();   
  } 

	/** @generated */
	public Crawl(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

	/**
	 * <!-- begin-user-doc --> Write your own initialization here <!--
	 * end-user-doc -->
	 * 
	 * @generated modifiable
	 */
	private void readObject() {
	}

	// *--------------*
	// * Feature: crawlId

	/**
	 * getter for crawlId - gets
	 * 
	 * @generated
	 */
	public int getCrawlId() {
    if (Crawl_Type.featOkTst && ((Crawl_Type)jcasType).casFeat_crawlId == null)
      jcasType.jcas.throwFeatMissing("crawlId", "types.Crawl");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Crawl_Type)jcasType).casFeatCode_crawlId);}
    
	/**
	 * setter for crawlId - sets
	 * 
	 * @generated
	 */
	public void setCrawlId(int v) {
    if (Crawl_Type.featOkTst && ((Crawl_Type)jcasType).casFeat_crawlId == null)
      jcasType.jcas.throwFeatMissing("crawlId", "types.Crawl");
    jcasType.ll_cas.ll_setIntValue(addr, ((Crawl_Type)jcasType).casFeatCode_crawlId, v);}    
   
    
	// *--------------*
	// * Feature: filename

	/**
	 * getter for filename - gets
	 * 
	 * @generated
	 */
	public String getFilename() {
    if (Crawl_Type.featOkTst && ((Crawl_Type)jcasType).casFeat_filename == null)
      jcasType.jcas.throwFeatMissing("filename", "types.Crawl");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Crawl_Type)jcasType).casFeatCode_filename);}
    
	/**
	 * setter for filename - sets
	 * 
	 * @generated
	 */
	public void setFilename(String v) {
    if (Crawl_Type.featOkTst && ((Crawl_Type)jcasType).casFeat_filename == null)
      jcasType.jcas.throwFeatMissing("filename", "types.Crawl");
    jcasType.ll_cas.ll_setStringValue(addr, ((Crawl_Type)jcasType).casFeatCode_filename, v);}    
   
    
  //*--------------*
  //* Feature: lastDocument

  /** getter for lastDocument - gets is the document the last in the archive
   * @generated */
  public boolean getLastDocument() {
    if (Crawl_Type.featOkTst && ((Crawl_Type)jcasType).casFeat_lastDocument == null)
      jcasType.jcas.throwFeatMissing("lastDocument", "types.Crawl");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((Crawl_Type)jcasType).casFeatCode_lastDocument);}
    
  /** setter for lastDocument - sets is the document the last in the archive 
   * @generated */
  public void setLastDocument(boolean v) {
    if (Crawl_Type.featOkTst && ((Crawl_Type)jcasType).casFeat_lastDocument == null)
      jcasType.jcas.throwFeatMissing("lastDocument", "types.Crawl");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((Crawl_Type)jcasType).casFeatCode_lastDocument, v);}    
  }
