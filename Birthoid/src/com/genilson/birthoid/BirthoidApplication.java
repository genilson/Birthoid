package com.genilson.birthoid;

import java.util.List;

import android.app.Application;

import com.facebook.model.GraphUser;

//Utilizado para acessar estado, como uma variavel global
public class BirthoidApplication extends Application{
	private List<GraphUser> selectedUsers;
	private List<Amigo> amigos;
	
	public List<Amigo> getAmigos() {
		return amigos;
	}

	public void setAmigos(List<Amigo> amigos) {
		this.amigos = amigos;
	}

	public List<GraphUser> getSelectedUsers() {
		return selectedUsers;
	}

	public void setSelectedUsers(List<GraphUser> selectedUsers) {
		this.selectedUsers = selectedUsers;
	}
}
