/**
 * 
 */
package types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

/**
 * @author tereza
 * 
 */
public class Annotation extends org.apache.uima.jcas.tcas.Annotation {
	/**
	 * @generated
	 * @ordered
	 */
	public final static int typeIndexID = JCasRegistry.register(Annotation.class);
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
	protected Annotation() {}
    
	/**
	 * Internal - constructor used by generator
	 * 
	 * @generated
	 */
	public Annotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
	/** @generated */
	public Annotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

	/** @generated */
	public Annotation(JCas jcas, int begin, int end) {
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
	// * Feature: confidence

	/**
	 * getter for confidence - gets The component that made the annotation may
	 * put its confidence/score calculated internally here, O
	 * 
	 * @generated
	 */
	public String getConfidence() {
    if (Annotation_Type.featOkTst && ((Annotation_Type)jcasType).casFeat_confidence == null)
      jcasType.jcas.throwFeatMissing("confidence", "types.Annotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Annotation_Type)jcasType).casFeatCode_confidence);}
    
	/**
	 * setter for confidence - sets The component that made the annotation may
	 * put its confidence/score calculated internally here, O
	 * 
	 * @generated
	 */
	public void setConfidence(String v) {
    if (Annotation_Type.featOkTst && ((Annotation_Type)jcasType).casFeat_confidence == null)
      jcasType.jcas.throwFeatMissing("confidence", "types.Annotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((Annotation_Type)jcasType).casFeatCode_confidence, v);}    
   
    
	// *--------------*
	// * Feature: componentId

	/**
	 * getter for componentId - gets Indicates which NLP component has been used
	 * to derive the annotation, C
	 * 
	 * @generated
	 */
	public String getComponentId() {
    if (Annotation_Type.featOkTst && ((Annotation_Type)jcasType).casFeat_componentId == null)
      jcasType.jcas.throwFeatMissing("componentId", "types.Annotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Annotation_Type)jcasType).casFeatCode_componentId);}
    
	/**
	 * setter for componentId - sets Indicates which NLP component has been used
	 * to derive the annotation, C
	 * 
	 * @generated
	 */
	public void setComponentId(String v) {
    if (Annotation_Type.featOkTst && ((Annotation_Type)jcasType).casFeat_componentId == null)
      jcasType.jcas.throwFeatMissing("componentId", "types.Annotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((Annotation_Type)jcasType).casFeatCode_componentId, v);}    
   
    
	// *--------------*
	// * Feature: id

	/**
	 * getter for id - gets
	 * 
	 * @generated
	 */
	public String getId() {
    if (Annotation_Type.featOkTst && ((Annotation_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "types.Annotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Annotation_Type)jcasType).casFeatCode_id);}
    
	/**
	 * setter for id - sets
	 * 
	 * @generated
	 */
	public void setId(String v) {
    if (Annotation_Type.featOkTst && ((Annotation_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "types.Annotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((Annotation_Type)jcasType).casFeatCode_id, v);}    
  }
