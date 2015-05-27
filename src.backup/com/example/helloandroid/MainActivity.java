package com.example.helloandroid;

import java.io.IOException;
import java.io.InputStream;

import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	private static final int PICK_CONTACT_REQUEST = 0;
	private Uri contactUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		renderContact(null);
	}

	private void renderContact(Uri uri) {
		// TODO Auto-generated method stub
		TextView nameView = (TextView) findViewById(R.id.contact_name);
		TextView phoneView = (TextView) findViewById(R.id.contact_phone);
		ImageView imageView = (ImageView) findViewById(R.id.contact_portrait);
		if (uri == null) {
			nameView.setText("Select a contact");
			phoneView.setText(null);
			imageView.setImageBitmap(null);
		} else {
			nameView.setText(getDisplayName(uri));
			phoneView.setText(getMobileNumber(uri));
			imageView.setImageBitmap(getPhoto(uri));
		}
	}

	private Bitmap getPhoto(Uri uri) {
		// TODO Auto-generated method stub
		Bitmap photo=null;
		Cursor contactCursor = getContentResolver().query(
				uri,
				new String[] { ContactsContract.Contacts._ID},
				null, null, null);
		String id=null;
		if(contactCursor.moveToFirst()){
			id=contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));
		}
		contactCursor.close();
		try{
			InputStream input=ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,new Long(id).longValue()));
			if(input!=null){
				photo=BitmapFactory.decodeStream(input);
			}
			input.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return photo;
	}

	private String getMobileNumber(Uri uri) {
		// TODO Auto-generated method stub
		String number = null;
		Cursor contactCursor = getContentResolver().query(
				uri,
				new String[] { ContactsContract.Contacts._ID},
				null, null, null);
		String id=null;
		if(contactCursor.moveToFirst()){
			id=contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));
		}
		contactCursor.close();
		
		Cursor cursor = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"=? and "+ContactsContract.CommonDataKinds.Phone.TYPE+"="+ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE, new String[]{id}, null);
		if(cursor.moveToFirst()){
			number=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		}
		cursor.close();
		return number;
	}

	private String getDisplayName(Uri uri) {
		String name = null;
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		if (cursor.moveToFirst()) {
			name = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		}
		cursor.close();
		Log.i("联系人姓名", name);
		return name;
	}

	public void onUpdateContact(View view) {
		Intent intent = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, MainActivity.PICK_CONTACT_REQUEST);
	}
	
	public void onImCoolClick(View view){
		SmsManager smsManager=SmsManager.getDefault();
		smsManager.sendTextMessage(getMobileNumber(this.contactUri), null,"Babe, I'm cool!",null,null);
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == MainActivity.PICK_CONTACT_REQUEST) {
			if (resultCode == RESULT_OK) {
				Log.d("Selection", intent.toString());
				contactUri=intent.getData();
				renderContact(intent.getData());
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
