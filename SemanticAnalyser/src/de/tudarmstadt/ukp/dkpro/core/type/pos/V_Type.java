/* First created by JCasGen Thu Nov 19 18:35:10 CET 2009 */
package de.tudarmstadt.ukp.dkpro.core.type.pos;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import de.tudarmstadt.ukp.dkpro.core.type.POS_Type;

/**
 * Verb and verb phrase Updated by JCasGen Tue Dec 01 19:55:14 CET 2009
 * 
 * @generated
 */
public class V_Type extends POS_Type {
	/** @generated */
	protected FSGenerator getFSGenerator() {
		return fsGenerator;
	}

	/** @generated */
	private final FSGenerator fsGenerator = new FSGenerator() {
		public FeatureStructure createFS(int addr, CASImpl cas) {
			if (V_Type.this.useExistingInstance) {
				// Return eq fs instance if already created
				FeatureStructure fs = V_Type.this.jcas.getJfsFromCaddr(addr);
				if (null == fs) {
					fs = new V(addr, V_Type.this);
					V_Type.this.jcas.putJfsFromCaddr(addr, fs);
					return fs;
				}
				return fs;
			} else
				return new V(addr, V_Type.this);
		}
	};
	/** @generated */
	public final static int typeIndexID = V.typeIndexID;
	/**
	 * @generated
	 * @modifiable
	 */
	public final static boolean featOkTst = JCasRegistry
			.getFeatOkTst("de.tudarmstadt.ukp.dkpro.core.type.pos.V");

	/**
	 * initialize variables to correspond with Cas Type and Features
	 * 
	 * @generated
	 */
	public V_Type(JCas jcas, Type casType) {
		super(jcas, casType);
		casImpl.getFSClassRegistry().addGeneratorForType(
				(TypeImpl) this.casType, getFSGenerator());

	}
}
