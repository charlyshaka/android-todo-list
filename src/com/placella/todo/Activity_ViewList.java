package com.placella.todo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Activity_ViewList extends Activity {
	private final Activity_ViewList self = this;
	private Item item;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewlist);

		item = (Item) getIntent().getSerializableExtra("item");
		TextView t = (TextView) findViewById(R.id.title);
		t.setText(item.getName());
		
		TableLayout table = (TableLayout) findViewById(R.id.content);
		for (Item i : item.getListcontent()) {
			TableRow tr = new TableRow(self);
			CheckBox x = new CheckBox(self);
			tr.addView(x);
			t = new TextView(self);
			t.setPadding(10, 5, 10, 5);
			t.setText(i.getName());
			t.setTextSize(18);
			tr.addView(t);
			table.addView(tr);
		}
		
		update(RESPONSE.CANCELLED);
		
		Button b;
		b = (Button) findViewById(R.id.back);
		b.setOnClickListener(new OnClickListener () {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	
    /**
	* Saves the current input data in order to later
	* pass it on to the calling activity
	*/
    private void update(int i) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("item", item);
        setResult(i, returnIntent);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_view, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem i) {
		if (i.getItemId() == R.id.menu_delete) {
			new Dialog_Confirm(self, R.string.confirm, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					update(RESPONSE.DELETED);
					finish();
				}
			}).show();
        } else if (i.getItemId() == R.id.menu_edit) {
	        Bundle b = new Bundle();
	        b.putInt("action", REQUEST.EDIT);
	        b.putSerializable("item", item);
	        Intent intent = new Intent(self, Activity_EditList.class);
	        intent.putExtras(b);
	        startActivityForResult(intent, REQUEST.EDIT);
        }
        return super.onOptionsItemSelected(i);
    }
	
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	if (requestCode == REQUEST.EDIT && resultCode == RESPONSE.MODIFIED) {
	        item = (Item) intent.getSerializableExtra("item");
    		update(resultCode);
    		finish();
    	} else if (requestCode == REQUEST.EDIT && resultCode == RESPONSE.DELETED) {
	        item = (Item) intent.getSerializableExtra("item");
    		update(resultCode);
    		finish();
    	}
    }
}
