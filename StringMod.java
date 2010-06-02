package rom.Ring.Schedule;

public class StringMod {
	
	public StringMod(){
		
	}
	
	public String pad(int c){
		if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
	}
	
}
