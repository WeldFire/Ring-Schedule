package rom.Ring.Schedule;

import android.database.Cursor;


public class Setting {
	
	long id;
	String name;
	String description;
	String startTime;
	String endTime;
	boolean enabled;
	int ringMode;
	boolean reminder;
	
	public Setting(){
		this.id = -1;
	}
	
	public Setting(Cursor entry){
		this.id = entry.getInt(entry.getColumnIndexOrThrow(NotesDbAdapter.KEY_ROWID));
		this.name = entry.getString(entry.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE));
		this.description = entry.getString(entry.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));		
		this.startTime = entry.getString(entry.getColumnIndexOrThrow(NotesDbAdapter.KEY_startTime));
		this.endTime = entry.getString(entry.getColumnIndexOrThrow(NotesDbAdapter.KEY_endTime));
		this.enabled = Boolean.parseBoolean(entry.getString(entry.getColumnIndexOrThrow(NotesDbAdapter.KEY_ENABLED)));
		this.ringMode = entry.getInt(entry.getColumnIndexOrThrow(NotesDbAdapter.KEY_RINGMODE));
		this.reminder = Boolean.parseBoolean(entry.getString(entry.getColumnIndexOrThrow(NotesDbAdapter.KEY_REMINDER)));
	}
	
	public void save(NotesDbAdapter database){
		//if creating new
		if(id == -1)
		{
			this.id = database.createReminder(this.name, this.description, this.startTime, this.endTime, this.ringMode, this.enabled, this.reminder);
		}
		//if updating record
		else
		{
			database.updateReminder(this.id, this.name, this.description, this.startTime, this.endTime, this.ringMode, this.enabled, this.reminder);
		}
	}
	
}
