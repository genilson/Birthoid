package com.genilson.birthoid;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

public class AmigosFragment extends Fragment {

	static ListView listView;
	private List<Amigo> amigos;
	private ProfilePictureView profilePictureView;
	private TextView userNameView;
	private Button carregarAmigos;
	
	//Ajuda a criar, abrir automaticamente, salvar e restaurar uma sessao ativa
	private UiLifecycleHelper ciclo;
	
	//Define um listener para executar onSessionStateChange quando houver
	//mudanca de estado sessao
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, final SessionState state,
				final Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	//Os metodos do ciclo de vida do fragmento devem ser sobrescritos para
	//executarem métodos correspondentes do UiLifecycleHelper
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ciclo = new UiLifecycleHelper(getActivity(), callback);
		ciclo.onCreate(savedInstanceState);
	}

	//Fragments instanciam seus layouts no onCreateView, enquanto activities
	//utilizam o metodo setContentView()
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.auth_fragment, container, false);
		//Obtendo referências para as views do Fragment
		profilePictureView = (ProfilePictureView) view.findViewById(R.id.user_picture);
		profilePictureView.setCropped(true);
		userNameView = (TextView) view.findViewById(R.id.user_name);
		carregarAmigos = (Button) view.findViewById(R.id.btn_carregar);
		
		//Evento que aciona o FriendsPicker da API
		carregarAmigos.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startPickerActivity(PickerActivity.FRIEND_PICKER, 0);
			}
		});

		listView = (ListView) view.findViewById(R.id.friends_list);

		//Se houver uma sessao ativa, obtem dados do usuario (foto e nome)
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()){
			makeMeRequest(session);
		}
		return view;
	}

	//Define o adapter para o listview
	protected void atualizarLista(List<Amigo> amigos){
		listView.setAdapter(new AmigoListAdapter(getActivity(), amigos));
	}
	
	private void makeMeRequest(final Session session) {
		//Utiliza uma chamada da API para obter os dados do usuario e define um
		//funcao de callback para tratar a resposta
		Request request = Request.newMeRequest(session, 
				new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (session == Session.getActiveSession()) {
					if (user != null) {
						//Define o id do usuario para que o ProfilePictureView
						//exiba a foto de perfil 
						profilePictureView.setProfileId(user.getId());
						userNameView.setText(user.getName());
					}
				}
				if (response.getError() != null) {
					//Se houver algum erro, informa ao usuario
					Toast.makeText(getActivity(), getActivity().getResources().
							getString(R.string.erro_facebook), Toast.LENGTH_SHORT).show();
				}
			}
		});
		request.executeAsync();
	}
	
	//Se o estado da sessao mudar mas a mesma estiver aberta, busca dados do usuario
	private void onSessionStateChange(final Session session, SessionState state,
			Exception exception) {
		if (session != null && session.isOpened()) {
			// Get the user's data.
			makeMeRequest(session);
		}
	}//onSessionStateChanged
	
	//Dispara um intent para execucao do FriendPickerFragment, disponivel na API
	private void startPickerActivity(Uri data, int requestCode) {
		Intent intent = new Intent();
		intent.setData(data);
		intent.setClass(getActivity(), PickerActivity.class);
		startActivityForResult(intent, requestCode);
	}

	//Atualiza a listView quando o usuario clica em "done" no FriendPicker Fragment
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			//Busca os os amigos selecionados no FriendPickerFragment e atualiza a listView
			BirthoidApplication app = (BirthoidApplication) getActivity().getApplication();
			amigos = app.getAmigos();
			atualizarLista(amigos);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		ciclo.onPause();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		ciclo.onSaveInstanceState(outState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		ciclo.onResume();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ciclo.onDestroy();
	}
}
