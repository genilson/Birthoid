package com.genilson.birthoid;


public class Amigo {
	
	private String id;
	private String nome;
	private String aniveriario;
	private int requestCode;
	
	public int getRequestCode() {
		return requestCode;
	}

	public String getId() {
		return id;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getAniveriario() {
		return aniveriario;
	}
	
	public void setAniveriario(String aniveriario) {
		this.aniveriario = aniveriario;
	}

	public Amigo(String id, String nome,
			String aniveriario) {
		super();
		this.id = id;
		this.nome = nome;
		this.aniveriario = aniveriario;
	}
	
	//protected abstract View.OnClickListener getOnClickListener();
}
