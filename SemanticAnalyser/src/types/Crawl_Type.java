/* First created by JCasGen Tue Feb 16 14:55:52 CET 2010 */
package types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** 
 * Updated by JCasGen Sat May 29 13:45:57 CEST 2010
 * @generated */
public class Crawl_Type extends Annotation_Type {
	/** @generated */
	protected FSGenerator getFSGenerator() {return fsGenerator;}
	/** @generated */
	private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Crawl_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Crawl_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Crawl(addr, Crawl_Type.this);
  			   Crawl_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Crawl(addr, Crawl_Type.this);
  	  }
    };
	/** @generated */
	public final static int typeIndexID = Crawl.typeIndexID;
	/**
	 * @generated
	 * @modifiable
	 */
	public final static boolean featOkTst = JCasRegistry.getFeatOkTst("types.Crawl");

	/** @generated */
	final Feature casFeat_crawlId;
	/** @generated */
	final int casFeatCode_crawlId;

	/** @generated */
	public int getCrawlId(int addr) {
        if (featOkTst && casFeat_crawlId == null)
      jcas.throwFeatMissing("crawlId", "types.Crawl");
    return ll_cas.ll_getIntValue(addr, casFeatCode_crawlId);
  }
	/** @generated */
	public void setCrawlId(int addr, int v) {
        if (featOkTst && casFeat_crawlId == null)
      jcas.throwFeatMissing("crawlId", "types.Crawl");
    ll_cas.ll_setIntValue(addr, casFeatCode_crawlId, v);}
    
  
 
	/** @generated */
	final Feature casFeat_filename;
	/** @generated */
	final int casFeatCode_filename;

	/** @generated */
	public String getFilename(int addr) {
        if (featOkTst && casFeat_filename == null)
      jcas.throwFeatMissing("filename", "types.Crawl");
    return ll_cas.ll_getStringValue(addr, casFeatCode_filename);
  }
	/** @generated */
	public void setFilename(int addr, String v) {
        if (featOkTst && casFeat_filename == null)
      jcas.throwFeatMissing("filename", "types.Crawl");
    ll_cas.ll_setStringValue(addr, casFeatCode_filename, v);}
    
  
 
  /** @generated */
  final Feature casFeat_lastDocument;
  /** @generated */
  final int     casFeatCode_lastDocument;
  /** @generated */ 
  public boolean getLastDocument(int addr) {
        if (featOkTst && casFeat_lastDocument == null)
      jcas.throwFeatMissing("lastDocument", "types.Crawl");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_lastDocument);
  }
  /** @generated */    
  public void setLastDocument(int addr, boolean v) {
        if (featOkTst && casFeat_lastDocument == null)
      jcas.throwFeatMissing("lastDocument", "types.Crawl");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_lastDocument, v);}
    
  



	/**
	 * initialize variables to correspond with Cas Type and Features
	 * 
	 * @generated
	 */
	public Crawl_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_crawlId = jcas.getRequiredFeatureDE(casType, "crawlId", "uima.cas.Integer", featOkTst);
    casFeatCode_crawlId  = (null == casFeat_crawlId) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_crawlId).getCode();

 
    casFeat_filename = jcas.getRequiredFeatureDE(casType, "filename", "uima.cas.String", featOkTst);
    casFeatCode_filename  = (null == casFeat_filename) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_filename).getCode();

 
    casFeat_lastDocument = jcas.getRequiredFeatureDE(casType, "lastDocument", "uima.cas.Boolean", featOkTst);
    casFeatCode_lastDocument  = (null == casFeat_lastDocument) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_lastDocument).getCode();

  }
}
