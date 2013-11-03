package de.arnepoths.pixelme;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class NewPixelMeActivity extends Activity {

	private static final int SELECT_PICTURE = 1;
	private ImageView mPixelImage;
	private ImageView mOrignalImageView;
	private Bitmap mOriginalImage;
	private SeekBar mDimensionSeeker;

	private TextView mPixelSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pixel_me_new);
		mPixelImage = (ImageView) findViewById(R.id.pixel_new_image);
		mPixelSize = (TextView) findViewById(R.id.textView1);

		mOrignalImageView = (ImageView) findViewById(R.id.pixel_original);
		mDimensionSeeker = (SeekBar) findViewById(R.id.seekBar1);

		mDimensionSeeker.setMax(20);
		mDimensionSeeker.setOnSeekBarChangeListener(getSeekbarListener());

	}

	private OnSeekBarChangeListener getSeekbarListener() {
		return new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if (mOriginalImage != null) {
					filterImage(seekBar.getProgress());
					mPixelSize.setText(getString(R.string.new_pixel_size)
							+ " is " + seekBar.getProgress() + "x"
							+ seekBar.getProgress());
				} else {
					Toast.makeText(NewPixelMeActivity.this,
							"Please choose a picture", Toast.LENGTH_LONG)
							.show();
				}

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

			}
		};
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

		case R.id.action_show_original:
			int visibility = (mOrignalImageView.getVisibility() == View.VISIBLE ? View.GONE
					: View.VISIBLE);
			mOrignalImageView.setVisibility(visibility);
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && data != null) {
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

	private void filterImage(int dimension) {
		if (dimension < 2) {
			mPixelImage.setImageBitmap(mOriginalImage);
		} else {
			Config config = mOriginalImage.getConfig();
			Bitmap image = mOriginalImage.copy(config, false);
			int width = image.getWidth();
			int height = image.getHeight();

			int[] pixels = new int[width * height];
			image.getPixels(pixels, 0, image.getWidth(), 0, 0,
					image.getWidth(), image.getHeight());

			new FilterTask(dimension, mOriginalImage.getWidth(),
					mOriginalImage.getHeight()).execute(pixels);
		}
	}

	private void setImage(Bitmap image) {
		setImage(image, true);
	}

	private void setImage(Bitmap image, boolean isOriginal) {
		if (image != null) {
			if (isOriginal) {
				mOriginalImage = image;
				mOrignalImageView.setImageBitmap(mOriginalImage);
			}
			mPixelImage.setImageBitmap(image);
		}
	}

	private Bitmap getImage(Uri data) {
		String imgPath = getPath(data);
		return getBitmapThumbFromFile(imgPath);
	}

	public static Bitmap getBitmapThumbFromFile(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(path, options);
		int imageWidth = options.outWidth;
		int sampleSize = imageWidth / 480;

		options = new BitmapFactory.Options();
		options.inSampleSize = sampleSize;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		return BitmapFactory.decodeFile(path, options);
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor == null)
			return null;
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String s = cursor.getString(column_index);
		cursor.close();
		return s;
	}

	private class FilterTask extends AsyncTask<int[], Void, int[]> {

		private int mDimen;
		private int mWidth;
		private int mHeight;

		public FilterTask(int dimen, int widht, int height) {
			mDimen = dimen;
			mWidth = widht;
			mHeight = height;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected int[] doInBackground(int[]... params) {

			return PixelMeUtil.filter(params[0], mWidth, mHeight, mDimen);
		}

		@Override
		protected void onPostExecute(int[] result) {
			super.onPostExecute(result);
			Bitmap image = Bitmap.createBitmap(result, mWidth, mHeight,
					Bitmap.Config.ARGB_8888);
			setImage(image, false);

		}
	}

}
