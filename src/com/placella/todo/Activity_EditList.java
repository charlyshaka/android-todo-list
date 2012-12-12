package com.placella.todo;

import java.util.List;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.view.*;
import android.view.View.*;
import android.view.ViewGroup.*;
import android.widget.*;

public class Activity_EditList extends Activity implements Savable {
	private final Activity_EditList self = this;
	private Item item;
	private int mode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editlist);
		
		item = (Item) getIntent().getSerializableExtra("item");
		mode = getIntent().getIntExtra("action", 0);
		
		update(RESPONSE.CANCELLED);
		
		EditText et;
		et = (EditText) findViewById(R.id.title);
		if (mode == REQUEST.EDIT) {
			et.setText(item.getName());
		}
	    et.addTextChangedListener(new NoteTextWatcher(self));
		et = (EditText) findViewById(R.id.content);
		
	    if (mode == REQUEST.EDIT) {
	    	TextView t = (TextView) findViewById(R.id.heading);
	    	t.setText(getResources().getString(R.string.editlist));
	    }
	    
	    refresh();
	    
		Button b;
		b = (Button) findViewById(R.id.ok);
		b.setOnClickListener(new OnClickListener () {
			@Override
			public void onClick(View arg0) {
				if (mode == REQUEST.ADD) {
					update(RESPONSE.ADDED);
				} else {
					update(RESPONSE.MODIFIED);
				}
				finish();
			}
		});
		b = (Button) findViewById(R.id.cancel);
		b.setOnClickListener(new OnClickListener () {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		b = (Button) findViewById(R.id.add);
		b.setOnClickListener(new OnClickListener () {
			@Override
			public void onClick(View arg0) {
				final EditText input = new EditText(self);
				new Dialog_Confirm(self, R.string.additem, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String value = input.getText().toString();
					    List<Item> list = item.getListcontent();
				    	list.add(
				    		new Item(value, Item.NOTE)
				    	);
				    	refresh();
					}
				}).addView(input).show();
			}
		});
	}
	
	private void refresh() {
	    List<Item> list = item.getListcontent();
    	LinearLayout l = (LinearLayout) findViewById(R.id.listitems);
    	l.removeAllViews();
	    TextView t;
	    if (list.size() > 0) {
	    	findViewById(R.id.hint).setVisibility(View.GONE);
	    	l.setPadding(0, 10, 0, 10);
			Util.hr(l, this);
			int count = 0;
	    	for (Item i : list) { 
	    		LinearLayout il = new LinearLayout(self);
	            il.setOrientation(LinearLayout.HORIZONTAL);
	            il.setLayoutParams(
                	new LayoutParams(
                		LayoutParams.MATCH_PARENT,
                		LayoutParams.WRAP_CONTENT
                	)
                );

	    		t = new TextView(self);
	            t.setLayoutParams(
                    new TableLayout.LayoutParams(
                		LayoutParams.WRAP_CONTENT,
                		LayoutParams.WRAP_CONTENT,
                		1
                	)
                );
	    		t.setText(i.getName());
		    	t.setPadding(10, 10, 10, 10);
		    	il.addView(t);
		    	
		    	ImageButton ib = new ImageButton(self);
		    	ib.setImageResource(R.drawable.ic_trash_unselected);
	            ib.setLayoutParams(
                    new LayoutParams( // FIXME: top part of the button is getting cut off
                		50, 50 // Values obtained by trial and error.
                	)
                );
	            ib.setTag(count);
	            ib.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						List<Item> l = item.getListcontent();
						l.remove(Integer.parseInt(arg0.getTag().toString()));
						refresh();
					}
	            });
		    	il.addView(ib);
		    	
	    		l.addView(il);
				Util.hr(l, this);
				
				count++;
	    	}
	    } else {
	    	findViewById(R.id.hint).setVisibility(View.VISIBLE);
	    }
	}

	private void update(int i) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("item", item);
        setResult(i, returnIntent);
	}

	@Override
	public void save() {
		TextView tv;
		tv = (TextView) findViewById(R.id.title);
		item.setName(tv.getText().toString());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (mode == REQUEST.EDIT) {
			getMenuInflater().inflate(R.menu.activity_edit, menu);
		} else {
			getMenuInflater().inflate(R.menu.activity_default, menu);
		}
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_delete) {
			new Dialog_Confirm(self, R.string.confirm, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					update(RESPONSE.DELETED);
					finish();
				}
			}).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
