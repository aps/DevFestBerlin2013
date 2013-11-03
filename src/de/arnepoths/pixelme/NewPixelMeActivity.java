package de.arnepoths.pixelme;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class NewPixelMeActivity extends Activity {

	private static final int SELECT_PICTURE = 1;
	private ImageView mPixelImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pixel_me_new);
		mPixelImage = (ImageView) findViewById(R.id.pixel_new_image);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.pixel_me_new, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.action_select_picture:
			intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(intent, "Select Picture"),
					SELECT_PICTURE);

			break;
		case R.id.action_take_picture:

			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case SELECT_PICTURE:
				setImage(getImage(data.getData()));
				break;

			default:
				break;
			}
			super.onActivityResult(requestCode, resultCode, data);
		}

	}

	private void setImage(Bitmap image) {
		if (image != null) {
			mPixelImage.setImageBitmap(image);
		}
	}

	private Bitmap getImage(Uri data) {
		String imgPath = getPath(data);
		Log.d("###### ", "" + imgPath);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		return BitmapFactory.decodeFile(imgPath, options);

	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor == null)
			return null;
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String s = cursor.getString(column_index);
		cursor.close();
		return s;
	}

}
