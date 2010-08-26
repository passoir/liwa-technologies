package org.liwa.coherence.schedule;

import java.sql.Timestamp;

public class TimestampMapper {
	private double middleSeconds;
	
	private int size;
	private double delta;
	
	
	public TimestampMapper(double startSeconds, int size, double delta){
		this.delta = delta;
		this.size = size-1;
		this.middleSeconds = startSeconds + this.size*delta;
	}
	
	public TimestampMapper(double startSeconds, double endSeconds, int size){
		this.size = size-1;
		this.delta = (endSeconds-startSeconds)/(2*this.size);
		this.middleSeconds = startSeconds + this.size*delta;
		if(delta == 0){
			delta = 1;
		}
	}
	
	public TimestampMapper(double startSeconds, double endSeconds, double delta){
		this.delta = delta;
		this.size = (int)((endSeconds-startSeconds)/(2*delta));
		this.middleSeconds = startSeconds + delta*size;
	}
	
	public double getDelta(){
		return delta;
	}
	
	public int getSize(){
		return size;
	}
	
	public double getSeconds(int i){
		return middleSeconds + i*delta;
	}
	
	public int getIndex(double seconds){
		return (int)((this.middleSeconds-seconds)/delta+1);
	}
	
	public Timestamp getVisitTimestamp(int i){
		return new Timestamp((long)(getSeconds(i)*1000));
	}
	
	public int getInterval(double intervalSeconds){
		return (int)(intervalSeconds/delta);
	}

}
