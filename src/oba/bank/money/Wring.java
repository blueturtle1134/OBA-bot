package oba.bank.money;

public class Wring {

	public static final double C = 10.0;
	public static final double R = 5.0;
	
	public static int wringAmount(int minutes) {
		return (int) (Math.sqrt(C*C+Math.pow(R*minutes/60.0, 2))-C);
	}
	public static String minutesToString(int minutes) {
		int h = minutes/60;
		int m = minutes-h*60;
		String result = "";
		if(h>0) {
			result += h+" ";
			if(h==1) {
				result+="hour";
			}
			else {
				result+="hours";
			}
		}
		if(h>0&&m>0) {
			result+=" and ";
		}
		if(m>0) {
			result += m+" ";
			if(m==1) {
				result+="minute";
			}
			else {
				result+="minutes";
			}
		}
		if(result.length()==0) {
			result = "less than a minute";
		}
		return result;
	}

}
