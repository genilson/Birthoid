package com.genilson.birthoid;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class Birthoid extends FragmentActivity {

	//Ids dos Fragments
	private static final int LOGIN = 0;
	private static final int AUTH = 1;
	private static final int SETTINGS = 2;
	private static final int FRAGMENT_COUNT = 3;
	private boolean isResumed = false;
	BirthoidApplication app;
	
	//Ajuda a criar, abrir automaticamente, salvar e restaurar uma sessao ativa
	private UiLifecycleHelper ciclo;
	
	//Define um listener para executar onSessionStateChange quando houver
	//mudanca de estado sessao
	private Session.StatusCallback callback = new Session.StatusCallback() {
		    @Override
		    public void call(Session session, SessionState state,
		    		Exception exception) {
		        onSessionStateChange(session, state, exception);
		    }
		};
		
	//Armazena todos os Fragments utilizados na aplicacao
	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	private MenuItem settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_birthoid);
		ciclo = new UiLifecycleHelper(this, callback);
		ciclo.onCreate(savedInstanceState);
		
		app = (BirthoidApplication) getApplication();

		FragmentManager fm = getSupportFragmentManager();
		//Obtem referência dos Fragments
		fragments[LOGIN] = fm.findFragmentById(R.id.loginFragment);
		fragments[AUTH] = fm.findFragmentById(R.id.authFragment);
		fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);

		FragmentTransaction transaction = fm.beginTransaction();
		
		//Escondendo todos os Fragments
		for (Fragment fragment: fragments){
			transaction.hide(fragment);
		}
		transaction.commit();
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		//Somente fara mudancas se a activity estiver visivel
		if (isResumed) {
			FragmentManager manager = getSupportFragmentManager();
			//Limpa o stack de Fragments
			int backStackSize = manager.getBackStackEntryCount();
			for (int i = 0; i < backStackSize; i++) {
				manager.popBackStack();
			}
			
			//Mostra o Fragment de Login caso nao exista uma sessao aberta ou a
			//tela inicial caso contrario
			if (state.isOpened()) {
				showFragment(AUTH, false);
			} else if (state.isClosed()) {
				showFragment(LOGIN, false);
			}
		}
	}//onSessionStateChange
	
	//Garantindo que sera aberto o Fragment apropriado quando os Fragments
	//obtiverem foco
	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		
		Session session = Session.getActiveSession();
		
		if (session != null && session.isOpened()){
			showFragment(AUTH, false);
		}else{
			showFragment(LOGIN, false);
		}
	}
	
	//Exibe um Fragment na tela
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();

		for (int i = 0; i < fragments.length; i++) {
			if (i == fragmentIndex) {
				transaction.show(fragments[i]);
			} else {
				transaction.hide(fragments[i]);
			}
		}
		//Se for adicionado ao BackStack, o Fragment podera recebera o foco
		//quando o botao voltar for pressionado.
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	//Exibindo uma opcao no menu que executa o UserSettingsFragment da API,
	//que permite ao usuario fazer logout da aplicacao
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//Apenas exibe o menu se o Fragment de autenticacao for o visivel
		if (fragments[AUTH].isVisible()){
			if (menu.size() == 0){
				settings = menu.add(R.string.logout);
			}
			return true;
		}
		else{
			menu.clear();
			settings = null;
		}
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.equals(settings)){
			showFragment(SETTINGS, true);
			return true;
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ciclo.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ciclo.onResume();
		isResumed = true;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		ciclo.onPause();
		isResumed = false;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ciclo.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		ciclo.onSaveInstanceState(outState);
		//outState.clear();
		//outState.putSerializable("birthoid_friends", (ArrayList<Amigo>) app.getAmigos());
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		//app.setAmigos((ArrayList<Amigo>) (savedInstanceState.getSerializable("birthoid_friends")));
		//ListView list = (ListView) findViewById(R.id.friends_list);
		//list.setAdapter(new AmigoListAdapter(getApplicationContext(), app.getAmigos()));
	}
}
