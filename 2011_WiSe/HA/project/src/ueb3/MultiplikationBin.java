package ueb3;

import java.math.BigInteger;

public class MultiplikationBin {
	
	private static int counter;
	public static int getCount(){
		return counter;
	}
	
	public static BigInteger schulMultiplikation(BigInteger a, BigInteger b){
		counter = 0;
		return schulMultiplikationStat(a, b);
	}
	
	private static BigInteger schulMultiplikationStat(BigInteger a, BigInteger b){
		return a.multiply(b);
	}
	
	public static BigInteger karatsuba(BigInteger a, BigInteger b){
		counter = 0;
		return karatsubaStat(a, b);
	}
	
	private static BigInteger karatsubaStat(BigInteger a, BigInteger b){
		return a.multiply(b);
	}
	
	public static void main(String[] args){
		BigInteger a = new BigInteger("9342093480");
		BigInteger b = new BigInteger("1231203912");
		System.out.println("schul: "+schulMultiplikation(a, b)+", zeit: "+getCount());
		System.out.println("schul: "+karatsuba(a, b)+", zeit: "+getCount());
	}
}
