package rom.Ring.Schedule;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;


public class RingSchedule extends ListActivity {
    /** Called when the activity is first created. */
	private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    
	public static final int ADD_ID = Menu.FIRST;
	public static final int DELETE_ID = Menu.FIRST + 1;
	int cHour, hour, cMinutes, minutes;
	int previousRingerMode= 2;
	public NotesDbAdapter database;
	Intent ScheduleService; //TODO move back down...
	Context starter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        database = new NotesDbAdapter(this);
        database.open(); //TODO badddd coding, leaving database open
        
        starter = getBaseContext();
        ScheduleService = new Intent(this, ScheduleService.class);
        starter.startService(ScheduleService);
        
        populateList();
        registerForContextMenu(getListView());
    }    
    public void populateList()
    {
    	//database.open();
    	Cursor c = database.fetchAllReminders();
    	startManagingCursor(c);
    	
    	String[] from = new String[] { NotesDbAdapter.KEY_TITLE };        
    	int[] to = new int[] { R.id.text1 };                
    	// Now create an array adapter and set it to display using our row        
    	SimpleCursorAdapter tasks = new SimpleCursorAdapter(this, R.layout.task_row, c, from, to);
    	setListAdapter(tasks);
    	//database.close();
    }
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, "Delete Schedule");
	}
    @Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
    	case DELETE_ID:
    		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    		//database.open();
	        database.deleteReminder(info.id);
	        //database.close();
	        populateList();
	        return true;
		}
		return super.onContextItemSelected(item);
	}   
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        super.onCreateOptionsMenu(menu);
        menu.add(0, ADD_ID, 0, "Add Schedule");
        menu.add(0, 54, 0, "Stp Service");//TODO remove
        return true;
    }
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case ADD_ID:
            createNote();
            return true;
        case 54:
        	starter.stopService(ScheduleService);
        	return true;
        }
       
        return super.onMenuItemSelected(featureId, item);
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, EditSchedule.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }
    private void createNote() {
        Intent i = new Intent(this, EditSchedule.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
}