package vn.edu.rmit.fruitybang;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import Game.Game;
import Model.Account;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.jme3.app.AndroidHarness;
import com.jme3.system.android.AndroidConfigChooser.ConfigType;

public class GameActivity extends AndroidHarness {
	private Game game;
	private Account account;
	private Dialog dialog;

	public GameActivity() {
		// Set the application class to run
		appClass = "Game.Game";

		// Try ConfigType.FASTEST; or ConfigType.LEGACY if you have problems
		eglConfigType = ConfigType.LEGACY;

		// Exit Dialog title & message
		exitDialogTitle = "Are you sure to quit?";
		exitDialogMessage = "Are you sure to quit?";

		// Enable verbose logging
		eglConfigVerboseLogging = false;

		// Choose screen orientation
		screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

		// Invert the MouseEvents X (default = true)
		mouseEventsInvertX = true;
		// Invert the MouseEvents Y (default = true)
		mouseEventsInvertY = true;
	}

	private void loadAccount() {
		Log.i("Fruity", "loadAccount()");
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		account = game.getAccount();

		account.setCapacity(preferences.getInt("capacity", 1));
		account.setFruit(preferences.getInt("fruit", Account.APPLE));
		account.setMoney(preferences.getInt("money", 0));
		account.setPower(preferences.getInt("power", 1));
		account.setShield(preferences.getBoolean("shield", false));
		account.setScore(preferences.getLong("score", 0));

		// Log.i("Fruity",
		// "After load: Capacity: " + account.getCapacity() + " Power: "
		// + account.getPower() + " Money " + account.getMoney()
		// + " Fruit: " + account.getFruit());
	}

	private void saveAccount() {
		Log.i("Fruity", "saveAccount()");
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor edit = preferences.edit();
		account = game.getAccount();

		edit.putInt("capacity", account.getCapacity());
		edit.putInt("fruit", account.getFruit());
		edit.putInt("power", account.getPower());
		edit.putInt("money", account.getMoney());
		edit.putBoolean("shield", account.isShield());
		edit.putLong("score", account.getScore());
		edit.commit();
	}

	@Override
	protected void onPause() {
		saveAccount();
		Log.i("Fruity", "GameActivity onPause()");
		super.onPause();
		if (dialog.isShowing()) {
			dialog.cancel();
		}
	}

	@Override
	public void onCreate(Bundle arg0) {
		Log.i("Fruity", "before GameActivity.super.onCreate()");
		super.onCreate(arg0);
		game = (Game) this.getJmeApplication();

		dialog = new Dialog(this, android.R.style.Theme_Light);
		dialog.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,
				LayoutParams.FLAG_FULLSCREEN);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.loading);
		dialog.show();

		LoadingAsyncTask loading = new LoadingAsyncTask(dialog, game);
		loading.execute();
		loadAccount();

		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					"vn.edu.rmit.fruitybang", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.e("Fruity",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {
		} catch (NoSuchAlgorithmException e) {
		}
	}

//	@Override
//	protected void onStop() {
//		// TODO Auto-generated method stub
//		Log.i("Fruity", "onStop() GameActivity");
//		Intent intent = new Intent(this, MainActivity.class);
//		intent.putExtra("score", account.getScore());
//		startActivity(intent);
//		super.onStop();
//	}
}
