/* First created by JCasGen Thu Nov 19 18:35:09 CET 2009 */
package de.tudarmstadt.ukp.dkpro.core.type.pos;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import de.tudarmstadt.ukp.dkpro.core.type.POS;

/**
 * Exclamation Updated by JCasGen Tue Dec 01 19:55:14 CET 2009 XML source:
 * /home/tereza/eclipse/TerminologyExtraction/desc/timesAggregate.xml
 * 
 * @generated
 */
public class O extends POS {
	/**
	 * @generated
	 * @ordered
	 */
	public final static int typeIndexID = JCasRegistry.register(O.class);
	/**
	 * @generated
	 * @ordered
	 */
	public final static int type = typeIndexID;

	/** @generated */
	public int getTypeIndexID() {
		return typeIndexID;
	}

	/**
	 * Never called. Disable default constructor
	 * 
	 * @generated
	 */
	protected O() {
	}

	/**
	 * Internal - constructor used by generator
	 * 
	 * @generated
	 */
	public O(int addr, TOP_Type type) {
		super(addr, type);
		readObject();
	}

	/** @generated */
	public O(JCas jcas) {
		super(jcas);
		readObject();
	}

	/** @generated */
	public O(JCas jcas, int begin, int end) {
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
