package com.genilson.birthoid;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.PickerFragment;

//Activity da aplicacao, host de todos os Fragments
public class PickerActivity extends FragmentActivity {

	//Uri utilizada para disparar FriendPickerFragment
	public static final Uri FRIEND_PICKER = Uri.parse("picker://friend");
	private FriendPickerFragment friendPickerFragment;
	private List<Amigo> amigos = new ArrayList<Amigo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends_list);

		//Pega os dados do Intent e se for uma solicitacao, executa o
		//FrindPickerFragment
		Bundle args = getIntent().getExtras();
		FragmentManager manager = getSupportFragmentManager();
		Fragment fragmentToShow = null;
		Uri intentUri = getIntent().getData();

		if (FRIEND_PICKER.equals(intentUri)) {
			if (savedInstanceState == null) {
				friendPickerFragment = new FriendPickerFragment(args);
			} else {
				friendPickerFragment = 
						(FriendPickerFragment) manager.findFragmentById(R.id.picker_fragment);
			}
			//Executa o listener para tratar erros 
			friendPickerFragment.setOnErrorListener(new PickerFragment.OnErrorListener() {
				@Override
				public void onError(PickerFragment<?> fragment,
						FacebookException error) {
					PickerActivity.this.onError(error);
				}
			});
			//Define listener para os botoes
			friendPickerFragment.setOnDoneButtonClickedListener(
					new PickerFragment.OnDoneButtonClickedListener() {
						@Override
						public void onDoneButtonClicked(PickerFragment<?> fragment) {
							finishActivity();
						}
					});
			fragmentToShow = friendPickerFragment;

		} else {
			setResult(RESULT_CANCELED);
			finish();
			return;
		}

		manager.beginTransaction()
		.replace(R.id.picker_fragment, fragmentToShow)
		.commit();
	}

	private void onError(Exception error) {
		onError(error.getLocalizedMessage(), false);
	}

	private void onError(String error, final boolean finishActivity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.error_dialog_title).
		setMessage(error).
		setPositiveButton(R.string.error_dialog_button_text, 
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				if (finishActivity) {
					finishActivity();
				}
			}
		});
		builder.show();
	}

	//metodo a ser chamado quando o usuario clicar em "done"
	private void finishActivity() {
		BirthoidApplication app = (BirthoidApplication) getApplication();
		if (getIntent().getData().equals(FRIEND_PICKER)){
			if (friendPickerFragment != null){
				app.setSelectedUsers(friendPickerFragment.getSelection());
				for (GraphUser user: app.getSelectedUsers()){
					makeRequest(Session.getActiveSession(), user.getId());
				}
				app.setAmigos(amigos);
			}
		}
		setResult(RESULT_OK, null);
		finish();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (FRIEND_PICKER.equals(getIntent().getData())) {
			try {
				friendPickerFragment.loadData(false);
			} catch (Exception ex) {
				onError(ex);
			}
		}
	}

	//Faz uma requisicao para a API de um nó do OpenGraph
	private void makeRequest(final Session session, String graphPath) {
		Request request = Request.newGraphPathRequest(session, graphPath, new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				// If the response is successful
				if (session == Session.getActiveSession()) {
					GraphObject user = response.getGraphObject();
					JSONObject job = user.getInnerJSONObject();
					try {
						Amigo amg = new Amigo(job.getString("id"), job.getString("name"), job.getString("birthday"));
						
						amigos.add(amg);

					} catch (JSONException e) {
						//TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (response.getError() != null) {
					//Se houver algum erro, informa ao usuario
					Toast.makeText(getApplicationContext(), getApplicationContext().getResources().
							getString(R.string.erro_facebook), Toast.LENGTH_SHORT).show();
				}
			}
		});
		request.executeAndWait();
	}//make request*/
}
