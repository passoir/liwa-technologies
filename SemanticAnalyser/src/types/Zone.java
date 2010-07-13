/**
 * 
 */
package types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

/**
 * A Zone is a distinct division of text. It is an abstract Type and provides a
 * parent type for sub-types which represent various kinds of text zones.
 * 
 * @author tereza XML source:
 *         src/main/resources/julie-document-structure-types.xml
 */
public class Zone extends Annotation {
	/**
	 * @generated
	 * @ordered
	 */
	public final static int typeIndexID = JCasRegistry.register(Zone.class);
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
	protected Zone() {}
    
	/**
	 * Internal - constructor used by generator
	 * 
	 * @generated
	 */
	public Zone(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
	/** @generated */
	public Zone(JCas jcas) {
    super(jcas);
    readObject();   
  } 

	/** @generated */
	public Zone(JCas jcas, int begin, int end) {
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
}
