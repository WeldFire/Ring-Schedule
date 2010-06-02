package rom.Ring.Schedule;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.IBinder;
import android.widget.Toast;

public class ScheduleService extends Service{
	public NotesDbAdapter database;
	private Context theContext;
	String startTime, endTime;
	int mode, reminder = -1;
	checkTime newChk;
	ArrayList<checkTime> cTAL;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate()
	{
		database = new NotesDbAdapter(this);
        database.open();
     
        cTAL = new ArrayList<checkTime>();
   
        theContext = getApplicationContext();
        
        Cursor reminderItems = database.fetchAllReminders();
        //startManagingCursor(c);
        if (reminderItems.moveToFirst()) {
            do {
                // Get the field values
            	if(reminderItems.getString(reminderItems.getColumnIndexOrThrow(NotesDbAdapter.KEY_ENABLED)).equals("1"))
            	{
            		addTimeCheck(reminderItems, theContext);
            	}
            } while (reminderItems.moveToNext());
        }
		Toast.makeText(getBaseContext(), "Service started!! :D", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onDestroy()
	{
		for(int i=0; i != cTAL.size(); i++)
		{
			unregisterReceiver(cTAL.get(i));
		}
		database.close();
		Toast.makeText(getBaseContext(), "I'm melting!!! D:", Toast.LENGTH_SHORT).show();
	}
	
	public void addTimeCheck(Cursor reminderItems, Context context)
    {	
		Setting setting = new Setting(reminderItems);
		
		newChk = new checkTime(setting.id, setting.startTime, setting.endTime, setting.ringMode, setting.reminder, theContext);
		cTAL.add(newChk);
		IntentFilter TimeTick = new IntentFilter(Intent.ACTION_TIME_TICK);
		
		registerReceiver(newChk, TimeTick);
    }

	public void updateTimeCheck(Cursor reminderItems){
		Setting setting = new Setting(reminderItems);
		int updatePos = -1;
		long id = setting.id;
		int size = cTAL.size();
		
		for(int i = 0; i != size; i++)//search
		{
			if(cTAL.get(i).id == id)
			{
				updatePos = i;
				break;
			}
		}
		
		if(updatePos == -1)//if not found
		{
			return;
		}
		
		unregisterReceiver(cTAL.get(updatePos));
		
		newChk = new checkTime(setting.id, setting.startTime, setting.endTime, setting.ringMode, setting.reminder, cTAL.get(updatePos).context);
		cTAL.add(updatePos, newChk);
		IntentFilter TimeTick = new IntentFilter(Intent.ACTION_TIME_TICK);
		if(newChk == null || TimeTick == null)
		{
			Toast.makeText(getBaseContext(), "NOOOOOOOOOOOOO@", Toast.LENGTH_LONG).show();
		}
		else
		{
		 registerReceiver(newChk, TimeTick);
		}
	}

}


class checkTime extends BroadcastReceiver{
	String startTime, endTime;
	int prevMode, mode;
	long id;
	boolean reminder;
	Context context;
	AudioControler ACM; 
	StringMod strMod = new StringMod();

	
	public checkTime(long id, String startTime, String endTime, int mode, boolean reminder, Context context)
	{
		this.context = context;
		ACM = new AudioControler(this.context);
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
		this.mode = mode;
		this.reminder = reminder;
	}

	@Override
	public void onReceive(Context arg0, Intent arg1) {
	Calendar setCalendar = Calendar.getInstance();  
	Date d = setCalendar.getTime();
	String cTime = strMod.pad(d.getHours()) + ":" + strMod.pad(d.getMinutes());
   	Toast.makeText(arg0, "Start, End, Current:\n" + startTime + "\n" + endTime + "\n" + cTime, Toast.LENGTH_LONG).show();
   	if(cTime.equals(startTime))
   	{
   		ACM.setPrevRing();
   		switch(mode)
   		{
       		case 0: ACM.toggleSilent();break;
       		case 1: ACM.toggleVib();break;
       		case 2: ACM.toggleNorm();break;
       		case 3: ACM.maxVol();break;
   		}
   		if(reminder)
   		{

   		}
    }
   	else if (cTime.equals(endTime))
   	{
   		ACM.recover();
   	}
	}
	
}
