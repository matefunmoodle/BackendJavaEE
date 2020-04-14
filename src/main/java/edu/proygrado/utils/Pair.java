package edu.proygrado.utils;

public class Pair<F,S> {
	
	private F first;
	private S second;
	
	public Pair(F first, S second) {
		super();
		this.first = first;
		this.second = second;
	}
	public F getFirst() {
		return this.first;
	}
	public void setFirst(F first) {
		this.first = first;
	}
	public S getSecond() {
		return this.second;
	}
	public void setValue(S second) {
		this.second = second;
	}
}
