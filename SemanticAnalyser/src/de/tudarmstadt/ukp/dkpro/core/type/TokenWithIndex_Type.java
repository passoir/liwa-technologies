/* First created by JCasGen Thu Nov 19 18:35:09 CET 2009 */
package de.tudarmstadt.ukp.dkpro.core.type;

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
 * Updated by JCasGen Tue Dec 01 19:55:13 CET 2009
 * 
 * @generated
 */
public class TokenWithIndex_Type extends Token_Type {
	/** @generated */
	protected FSGenerator getFSGenerator() {
		return fsGenerator;
	}

	/** @generated */
	private final FSGenerator fsGenerator = new FSGenerator() {
		public FeatureStructure createFS(int addr, CASImpl cas) {
			if (TokenWithIndex_Type.this.useExistingInstance) {
				// Return eq fs instance if already created
				FeatureStructure fs = TokenWithIndex_Type.this.jcas
						.getJfsFromCaddr(addr);
				if (null == fs) {
					fs = new TokenWithIndex(addr, TokenWithIndex_Type.this);
					TokenWithIndex_Type.this.jcas.putJfsFromCaddr(addr, fs);
					return fs;
				}
				return fs;
			} else
				return new TokenWithIndex(addr, TokenWithIndex_Type.this);
		}
	};
	/** @generated */
	public final static int typeIndexID = TokenWithIndex.typeIndexID;
	/**
	 * @generated
	 * @modifiable
	 */
	public final static boolean featOkTst = JCasRegistry
			.getFeatOkTst("de.tudarmstadt.ukp.dkpro.core.type.TokenWithIndex");

	/** @generated */
	final Feature casFeat_TokenIndex;
	/** @generated */
	final int casFeatCode_TokenIndex;

	/** @generated */
	public int getTokenIndex(int addr) {
		if (featOkTst && casFeat_TokenIndex == null)
			jcas.throwFeatMissing("TokenIndex",
					"de.tudarmstadt.ukp.dkpro.core.type.TokenWithIndex");
		return ll_cas.ll_getIntValue(addr, casFeatCode_TokenIndex);
	}

	/** @generated */
	public void setTokenIndex(int addr, int v) {
		if (featOkTst && casFeat_TokenIndex == null)
			jcas.throwFeatMissing("TokenIndex",
					"de.tudarmstadt.ukp.dkpro.core.type.TokenWithIndex");
		ll_cas.ll_setIntValue(addr, casFeatCode_TokenIndex, v);
	}

	/**
	 * initialize variables to correspond with Cas Type and Features
	 * 
	 * @generated
	 */
	public TokenWithIndex_Type(JCas jcas, Type casType) {
		super(jcas, casType);
		casImpl.getFSClassRegistry().addGeneratorForType(
				(TypeImpl) this.casType, getFSGenerator());

		casFeat_TokenIndex = jcas.getRequiredFeatureDE(casType, "TokenIndex",
				"uima.cas.Integer", featOkTst);
		casFeatCode_TokenIndex = (null == casFeat_TokenIndex) ? JCas.INVALID_FEATURE_CODE
				: ((FeatureImpl) casFeat_TokenIndex).getCode();

	}
}
