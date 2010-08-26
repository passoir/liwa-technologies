/* First created by JCasGen Thu Nov 19 18:35:09 CET 2009 */
package de.tudarmstadt.ukp.dkpro.core.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

/**
 * Updated by JCasGen Tue Dec 01 19:55:13 CET 2009 XML source:
 * /home/tereza/eclipse/TerminologyExtraction/desc/timesAggregate.xml
 * 
 * @generated
 */
public class TokenWithIndex extends Token {
	/**
	 * @generated
	 * @ordered
	 */
	public final static int typeIndexID = JCasRegistry
			.register(TokenWithIndex.class);
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
	protected TokenWithIndex() {
	}

	/**
	 * Internal - constructor used by generator
	 * 
	 * @generated
	 */
	public TokenWithIndex(int addr, TOP_Type type) {
		super(addr, type);
		readObject();
	}

	/** @generated */
	public TokenWithIndex(JCas jcas) {
		super(jcas);
		readObject();
	}

	/** @generated */
	public TokenWithIndex(JCas jcas, int begin, int end) {
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
	// * Feature: TokenIndex

	/**
	 * getter for TokenIndex - gets
	 * 
	 * @generated
	 */
	public int getTokenIndex() {
		if (TokenWithIndex_Type.featOkTst
				&& ((TokenWithIndex_Type) jcasType).casFeat_TokenIndex == null)
			jcasType.jcas.throwFeatMissing("TokenIndex",
					"de.tudarmstadt.ukp.dkpro.core.type.TokenWithIndex");
		return jcasType.ll_cas.ll_getIntValue(addr,
				((TokenWithIndex_Type) jcasType).casFeatCode_TokenIndex);
	}

	/**
	 * setter for TokenIndex - sets
	 * 
	 * @generated
	 */
	public void setTokenIndex(int v) {
		if (TokenWithIndex_Type.featOkTst
				&& ((TokenWithIndex_Type) jcasType).casFeat_TokenIndex == null)
			jcasType.jcas.throwFeatMissing("TokenIndex",
					"de.tudarmstadt.ukp.dkpro.core.type.TokenWithIndex");
		jcasType.ll_cas.ll_setIntValue(addr,
				((TokenWithIndex_Type) jcasType).casFeatCode_TokenIndex, v);
	}
}
