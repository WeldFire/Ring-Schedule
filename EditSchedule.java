package rom.Ring.Schedule;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditSchedule extends Activity{
	static final int START_TIME_ID = 0;
	static final int END_TIME_ID = 1;
	private EditText scheduleName;
	private EditText scheduleDescription;
	private Button startTime;
	private TextView startTimeTxt;
	private Button endTime;
	private TextView endTimeTxt;
	private CheckBox enabledBox;
	private CheckBox reminderBox;
	private Spinner ringerModes;
	private Button submitBtn;
	private Long RowId;
	private int tpHour, tpMinute;
	private String uTime;
	private ScheduleService sService;
	
    NotesDbAdapter database;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_schedule);
		
        database = new NotesDbAdapter(this);
        
        scheduleName = (EditText) findViewById(R.id.title);
        scheduleDescription = (EditText) findViewById(R.id.body);
        startTime = (Button) findViewById(R.id.startBtn);
        startTimeTxt = (TextView) findViewById(R.id.startTxt);
        endTime = (Button) findViewById(R.id.endBtn);
        endTimeTxt = (TextView) findViewById(R.id.endTxt);
        enabledBox = (CheckBox) findViewById(R.id.enabled);
        reminderBox = (CheckBox) findViewById(R.id.reminder);
        ringerModes = (Spinner) findViewById(R.id.selectMode);
        submitBtn = (Button) findViewById(R.id.confirm);
        
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.RingerMode_array, android.R.layout.simple_spinner_item);    
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    
    	ringerModes.setAdapter(adapter);
    	
    	 RowId = savedInstanceState != null ? savedInstanceState.getLong(NotesDbAdapter.KEY_ROWID) : null;
    	 
    	 if(RowId == null) 
    	 {
 			Bundle extras = getIntent().getExtras();            
 			RowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID) : null;
 		 }
        
    	populateFields();
    	
    	//Calls dialog to set the starting time
    	startTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {uTime = "start";showDialog(START_TIME_ID);}});
    	//Calls dialog to set the ending time
    	endTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {uTime = "end";showDialog(END_TIME_ID);}});
    	//Returns 'OK' to calling program and ends calling the save state method if the name is not null
    	submitBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {if(!scheduleName.getText().toString().equals("")){setResult(RESULT_OK);finish();}else{Toast.makeText(getBaseContext(), "You must give your schedule a name!", Toast.LENGTH_SHORT).show();}}});
    	
	}
	
	/**
	 * enabledBox and reminderBox need to be stored as strings as 'true' or 'false' in the database
	 * ringerModes need to be stored as an integer starting at 0
	 */
	private void populateFields() {
        if (RowId != null) {
        	database.open();
            Cursor note = database.fetchReminder(RowId);
            startManagingCursor(note);
            scheduleName.setText(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            scheduleDescription.setText(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
            startTimeTxt.setText(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_startTime)));
            endTimeTxt.setText(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_endTime)));
            ringerModes.setSelection(note.getInt(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_RINGMODE)));
            
            
            String enabledTxt = note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_ENABLED));
            String reminderTxt = note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_REMINDER));
            
            database.close();
            
            if(enabledTxt.equals("1"))
            {
            	enabledBox.setChecked(true);
            }
            else
            {
            	enabledBox.setChecked(false);
            }
            
            if(reminderTxt.equals("1"))
            {
            	reminderBox.setChecked(true);
            }
            else
            {
            	reminderBox.setChecked(false);
            }
        }
    }

	/**
	 * Time dialog boxes
	 */
	@Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case START_TIME_ID:
            return new TimePickerDialog(this, mTimeSetListener, tpHour, tpMinute, true);
        case END_TIME_ID:
        	return new TimePickerDialog(this, mTimeSetListener, tpHour, tpMinute, true);
        }
        return null;
    }
    
	// the call back received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                tpHour = hourOfDay;
                tpMinute = minute;
                updateDisplay(uTime);
            }
        };
	
        
    public void updateDisplay(String time)
    {
    	if(time.equals("start"))
    	{
    		startTimeTxt.setText(pad(tpHour) + ":" + pad(tpMinute));
    	}
    	else
    	{
    		endTimeTxt.setText(pad(tpHour) + ":" + pad(tpMinute));
    	}
    }
	
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
    
    //Save instance state stuff
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(RowId != null)
        {
        outState.putLong(NotesDbAdapter.KEY_ROWID, RowId);
		}
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
    
    private void saveState() {
    	String name = scheduleName.getText().toString();
    	String description = scheduleDescription.getText().toString();
    	String startText = startTimeTxt.getText().toString();
    	String endText = endTimeTxt.getText().toString();
    	boolean isEnabled = enabledBox.isChecked();
    	boolean isReminder = reminderBox.isChecked();
    	int ringerMode = ringerModes.getLastVisiblePosition();
    	database.open();
    	sService = new ScheduleService();
    	
		try{
			if (RowId == null) {
	            long id = database.createReminder(name, description, startText, endText, ringerMode, isEnabled, isReminder);
	            if (id > 0) {
	                RowId = id;
	                if(isEnabled)
	                {
		               // Intent servIntent = new Intent(this, ScheduleService.class);
		               // ServiceConnection connection = null;
		               
		               // bindContext.bindService(servIntent, connection, 0);
		                sService.addTimeCheck(database.fetchReminder(id), getApplicationContext());
		                //TODO implement a way to add time check
	                }
	            }
	        } else {
	            database.updateReminder(RowId, name, description, startText, endText, ringerMode, isEnabled, isReminder);
	            sService.updateTimeCheck(database.fetchReminder(RowId));
	        }
		}
		catch(Exception e)
		{
			Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_SHORT).show();
		}


		database.close();
    	
    	
    }
}
