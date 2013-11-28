package vn.edu.rmit.fruitybang;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class SelectionFragment extends Fragment {
	private Button publishButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.selection, container, false);

		publishButton = (Button) view.findViewById(R.id.publishButton);
		publishButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (getActivity() instanceof MainActivity) {
					MainActivity main = (MainActivity) getActivity();
					publishFeedDialog(main.getScore());
				} else {
					publishFeedDialog(1);
				}
			}
		});
		return view;
	}

	private void publishFeedDialog(long score) {
		Bundle params = new Bundle();
		params.putString("picture", "https://raw.github.com/damhonglinh/fruitybang/master/ic_launcher.png");
		params.putString("name", getString(R.string.title));
		params.putString("caption", getString(R.string.caption));
		params.putString("description", getString(R.string.description) + " - "
				+ score + getString(R.string.description1));
		params.putString("link", getString(R.string.websiteUrl));

		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(getActivity(),
				Session.getActiveSession(), params)).setOnCompleteListener(
				new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error == null) {
							// When the story is posted, echo the success
							// and the post Id.
							final String postId = values.getString("post_id");
							if (postId != null) {
								Toast.makeText(getActivity(),
										getString(R.string.shareSuccessfully),
										Toast.LENGTH_SHORT).show();
							} else {
								// User clicked the Cancel button
								Toast.makeText(getActivity(),
										getString(R.string.shareCancelled),
										Toast.LENGTH_SHORT).show();
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							// User clicked the "x" button
							Toast.makeText(
									getActivity().getApplicationContext(),
									getString(R.string.shareCancelled),
									Toast.LENGTH_SHORT).show();
						} else {
							// Generic, ex: network error
							Toast.makeText(
									getActivity().getApplicationContext(),
									getString(R.string.shareFail),
									Toast.LENGTH_SHORT).show();
						}
					}
				}).build();
		feedDialog.show();
	}
}
