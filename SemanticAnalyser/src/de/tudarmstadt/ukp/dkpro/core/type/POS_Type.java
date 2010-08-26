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
import org.apache.uima.jcas.tcas.Annotation_Type;

/**
 * The part of speech of a word or a phrase. Updated by JCasGen Tue Dec 01
 * 19:55:13 CET 2009
 * 
 * @generated
 */
public class POS_Type extends Annotation_Type {
	/** @generated */
	protected FSGenerator getFSGenerator() {
		return fsGenerator;
	}

	/** @generated */
	private final FSGenerator fsGenerator = new FSGenerator() {
		public FeatureStructure createFS(int addr, CASImpl cas) {
			if (POS_Type.this.useExistingInstance) {
				// Return eq fs instance if already created
				FeatureStructure fs = POS_Type.this.jcas.getJfsFromCaddr(addr);
				if (null == fs) {
					fs = new POS(addr, POS_Type.this);
					POS_Type.this.jcas.putJfsFromCaddr(addr, fs);
					return fs;
				}
				return fs;
			} else
				return new POS(addr, POS_Type.this);
		}
	};
	/** @generated */
	public final static int typeIndexID = POS.typeIndexID;
	/**
	 * @generated
	 * @modifiable
	 */
	public final static boolean featOkTst = JCasRegistry
			.getFeatOkTst("de.tudarmstadt.ukp.dkpro.core.type.POS");

	/** @generated */
	final Feature casFeat_PosValue;
	/** @generated */
	final int casFeatCode_PosValue;

	/** @generated */
	public String getPosValue(int addr) {
		if (featOkTst && casFeat_PosValue == null)
			jcas.throwFeatMissing("PosValue",
					"de.tudarmstadt.ukp.dkpro.core.type.POS");
		return ll_cas.ll_getStringValue(addr, casFeatCode_PosValue);
	}

	/** @generated */
	public void setPosValue(int addr, String v) {
		if (featOkTst && casFeat_PosValue == null)
			jcas.throwFeatMissing("PosValue",
					"de.tudarmstadt.ukp.dkpro.core.type.POS");
		ll_cas.ll_setStringValue(addr, casFeatCode_PosValue, v);
	}

	/**
	 * initialize variables to correspond with Cas Type and Features
	 * 
	 * @generated
	 */
	public POS_Type(JCas jcas, Type casType) {
		super(jcas, casType);
		casImpl.getFSClassRegistry().addGeneratorForType(
				(TypeImpl) this.casType, getFSGenerator());

		casFeat_PosValue = jcas.getRequiredFeatureDE(casType, "PosValue",
				"uima.cas.String", featOkTst);
		casFeatCode_PosValue = (null == casFeat_PosValue) ? JCas.INVALID_FEATURE_CODE
				: ((FeatureImpl) casFeat_PosValue).getCode();

	}
}
