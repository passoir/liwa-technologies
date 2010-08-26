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
public class Date extends Annotation {
	/**
	 * @generated
	 * @ordered
	 */
	public final static int typeIndexID = JCasRegistry.register(Date.class);
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
	protected Date() {}
    
	/**
	 * Internal - constructor used by generator
	 * 
	 * @generated
	 */
	public Date(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
	/** @generated */
	public Date(JCas jcas) {
    super(jcas);
    readObject();   
  } 

	/** @generated */
	public Date(JCas jcas, int begin, int end) {
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
	// * Feature: day

	/**
	 * getter for day - gets day of the month, C
	 * 
	 * @generated
	 */
	public int getDay() {
    if (Date_Type.featOkTst && ((Date_Type)jcasType).casFeat_day == null)
      jcasType.jcas.throwFeatMissing("day", "types.Date");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Date_Type)jcasType).casFeatCode_day);}
    
	/**
	 * setter for day - sets day of the month, C
	 * 
	 * @generated
	 */
	public void setDay(int v) {
    if (Date_Type.featOkTst && ((Date_Type)jcasType).casFeat_day == null)
      jcasType.jcas.throwFeatMissing("day", "types.Date");
    jcasType.ll_cas.ll_setIntValue(addr, ((Date_Type)jcasType).casFeatCode_day, v);}    
   
    
	// *--------------*
	// * Feature: month

	/**
	 * getter for month - gets month of the year, C
	 * 
	 * @generated
	 */
	public int getMonth() {
    if (Date_Type.featOkTst && ((Date_Type)jcasType).casFeat_month == null)
      jcasType.jcas.throwFeatMissing("month", "types.Date");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Date_Type)jcasType).casFeatCode_month);}
    
	/**
	 * setter for month - sets month of the year, C
	 * 
	 * @generated
	 */
	public void setMonth(int v) {
    if (Date_Type.featOkTst && ((Date_Type)jcasType).casFeat_month == null)
      jcasType.jcas.throwFeatMissing("month", "types.Date");
    jcasType.ll_cas.ll_setIntValue(addr, ((Date_Type)jcasType).casFeatCode_month, v);}    
   
    
	// *--------------*
	// * Feature: year

	/**
	 * getter for year - gets full year (e.g. 2006 and NOT 06), C
	 * 
	 * @generated
	 */
	public int getYear() {
    if (Date_Type.featOkTst && ((Date_Type)jcasType).casFeat_year == null)
      jcasType.jcas.throwFeatMissing("year", "types.Date");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Date_Type)jcasType).casFeatCode_year);}
    
	/**
	 * setter for year - sets full year (e.g. 2006 and NOT 06), C
	 * 
	 * @generated
	 */
	public void setYear(int v) {
    if (Date_Type.featOkTst && ((Date_Type)jcasType).casFeat_year == null)
      jcasType.jcas.throwFeatMissing("year", "types.Date");
    jcasType.ll_cas.ll_setIntValue(addr, ((Date_Type)jcasType).casFeatCode_year, v);}    
  }
