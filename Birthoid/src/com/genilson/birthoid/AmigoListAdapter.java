package com.genilson.birthoid;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;

public class AmigoListAdapter extends BaseAdapter {
	
	private Context contexto;
	private List<Amigo> arrayAmigos;

	public AmigoListAdapter(Context contexto, List<Amigo> arrayAmigo) {
		super();
		this.contexto = contexto;
		this.arrayAmigos = arrayAmigo;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrayAmigos.size();
	}

	@Override
	public Object getItem(int posicao) {
		// TODO Auto-generated method stub
		return arrayAmigos.get(posicao);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater =
					(LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.listitem, null);
		}

		Amigo amigo = arrayAmigos.get(position);
		if (amigo != null) {
			ProfilePictureView fotoPerfil = (ProfilePictureView) view.findViewById(R.id.list_user_picture);
			TextView nome = (TextView) view.findViewById(R.id.list_user_name);
			TextView aniversario = (TextView) view.findViewById(R.id.item_aniversario);
			if (fotoPerfil != null) {
				fotoPerfil.setProfileId(amigo.getId());
			}
			if (nome != null) {
				nome.setText(amigo.getNome());
			}
			if (aniversario != null) {
				aniversario.setText(amigo.getAniveriario());
			}
		}
		return view;
	}

}
