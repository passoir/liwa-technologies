package de.l3s.liwa.assessment;

public class Score{

	private double connectivitySonar;

	private double lda;
	private double science;
	private double technology;
	private double finance;
	public Score(double connectivitySonar, double lda, double science, double technology){
		this.connectivitySonar = connectivitySonar;
		this.lda = lda;
		this.science = science;
		this.technology = technology;
	}
	public Score(){
		this.connectivitySonar = 0;
		this.lda = 0;
		this.science = 0;
		this.technology = 0;
	}
	public double getConnectivitySonar() {
		return connectivitySonar;
	}
	public void setConnectivitySonar(double connectivitySonar) {
		this.connectivitySonar = connectivitySonar;
	}
	public double getLda() {
		return lda;
	}
	public void setLda(double lda) {
		this.lda = lda;
	}
	public double getScience() {
		return science;
	}
	public void setScience(double science) {
		this.science = science;
	}
	public double getTechnology() {
		return technology;
	}
	public void setTechnology(double technology) {
		this.technology = technology;
	}
	public double getFinance() {
return this.finance;
	}
	public void setFinance(double finance) {

		this.finance = finance;
	}
}
