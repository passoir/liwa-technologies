/* First created by JCasGen Thu Nov 19 18:35:10 CET 2009 */
package de.tudarmstadt.ukp.dkpro.core.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

/**
 * Updated by JCasGen Tue Dec 01 19:55:14 CET 2009 XML source:
 * /home/tereza/eclipse/TerminologyExtraction/desc/timesAggregate.xml
 * 
 * @generated
 */
public class Lemma extends ModifiedToken {
	/**
	 * @generated
	 * @ordered
	 */
	public final static int typeIndexID = JCasRegistry.register(Lemma.class);
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
	protected Lemma() {
	}

	/**
	 * Internal - constructor used by generator
	 * 
	 * @generated
	 */
	public Lemma(int addr, TOP_Type type) {
		super(addr, type);
		readObject();
	}

	/** @generated */
	public Lemma(JCas jcas) {
		super(jcas);
		readObject();
	}

	/** @generated */
	public Lemma(JCas jcas, int begin, int end) {
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
	// * Feature: value

	/**
	 * getter for value - gets
	 * 
	 * @generated
	 */
	public String getValue() {
		if (Lemma_Type.featOkTst
				&& ((Lemma_Type) jcasType).casFeat_value == null)
			jcasType.jcas.throwFeatMissing("value",
					"de.tudarmstadt.ukp.dkpro.core.type.Lemma");
		return jcasType.ll_cas.ll_getStringValue(addr,
				((Lemma_Type) jcasType).casFeatCode_value);
	}

	/**
	 * setter for value - sets
	 * 
	 * @generated
	 */
	public void setValue(String v) {
		if (Lemma_Type.featOkTst
				&& ((Lemma_Type) jcasType).casFeat_value == null)
			jcasType.jcas.throwFeatMissing("value",
					"de.tudarmstadt.ukp.dkpro.core.type.Lemma");
		jcasType.ll_cas.ll_setStringValue(addr,
				((Lemma_Type) jcasType).casFeatCode_value, v);
	}
}
