package vn.edu.rmit.fruitybang;

import Game.Game;
import android.app.Dialog;
import android.os.AsyncTask;
import android.util.Log;

public class LoadingAsyncTask extends AsyncTask<Void, Void, Void> {
	private Dialog dialog;
	private Game game;

	public LoadingAsyncTask(Dialog dialog, Game game) {
		this.dialog = dialog;
		this.game = game;
	}

	@Override
	public Void doInBackground(Void... params) {
		try {
			while (!game.isFinished()) {
				Thread.sleep(3000);
				Log.i("Fruity", "Thread sleep for 3 second");
			}
			Thread.sleep(5000);
		} catch (Exception e) {
			Log.i("Fruity", "Catched Exception " + e.toString());
		} finally {
			dialog.dismiss();
		}
		return null;
	}
}
