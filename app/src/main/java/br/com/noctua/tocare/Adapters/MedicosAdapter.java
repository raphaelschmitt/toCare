package br.com.noctua.tocare.Adapters;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import br.com.noctua.tocare.Objetos.Medico;
import br.com.noctua.tocare.R;

/**
 * Created by Raphael on 02/03/2018.
 */

public class MedicosAdapter extends ArrayAdapter<Medico> {

    private ArrayList<Medico> medicoList;

    public MedicosAdapter(Context context, int textViewResourceId, ArrayList<Medico> ListaLogsList) {
        super(context, textViewResourceId, ListaLogsList);
        this.medicoList = new ArrayList<Medico>();
        this.medicoList.addAll(ListaLogsList);
    }

    private class ViewHolder {
        public TextView tvNome;
        public SimpleDraweeView ivFotoPerfil;
        public TextView tvPaisOrigem;
        public TextView tvCROCRM;
    }

    public Medico getItem(int position){
        return medicoList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            MedicosAdapter.ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.item_medico_list, null);

                holder = new MedicosAdapter.ViewHolder();
                holder.tvNome = (TextView)convertView.findViewById(R.id.tv_nomePaciente);
                holder.tvPaisOrigem = (TextView)convertView.findViewById(R.id.tvPaisOrigem);
                holder.tvCROCRM = (TextView)convertView.findViewById(R.id.tvCROCRM);
                holder.ivFotoPerfil = (SimpleDraweeView) convertView.findViewById(R.id.ivFotoPerfil);
                convertView.setTag(holder);
            } else {
                holder = (MedicosAdapter.ViewHolder) convertView.getTag();
            }

            Medico medico = medicoList.get(position);

            if(medico.getGenero().equals("Masculino")){
                holder.tvNome.setText("Dr. " + medico.getNome() + " " + medico.getSobrenome());
            } else {
                holder.tvNome.setText("Dra. " + medico.getNome() + " " + medico.getSobrenome());
            }
            holder.tvCROCRM.setText("CRO/CRM: " + medico.getRegistroMedico());
            holder.tvPaisOrigem.setText("País de Origem: " + medico.getPaisOrigem());

            //NEW PART - FRESCO CARREGAMENTO DE IMAGEM
            ControllerListener listener = new BaseControllerListener(){
                @Override
                public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                }

                @Override
                public void onFailure(String id, Throwable throwable) {
                    super.onFailure(id, throwable);
                }

                @Override
                public void onIntermediateImageFailed(String id, Throwable throwable) {
                    super.onIntermediateImageFailed(id, throwable);
                }

                @Override
                public void onIntermediateImageSet(String id, Object imageInfo) {
                    super.onIntermediateImageSet(id, imageInfo);
                }

                @Override
                public void onRelease(String id) {
                    super.onRelease(id);
                }

                @Override
                public void onSubmit(String id, Object callerContext) {
                    super.onSubmit(id, callerContext);
                }
            };

            String urlImagem = medico.getFoto().replace(" ", "%20");

            Uri uri = null;
            DraweeController dc = null;

            if(urlImagem.contains("https")){
                uri = Uri.parse(medico.getFoto().replace(" ", "%20"));
                dc = Fresco.newDraweeControllerBuilder()
                        .setUri(uri)
                        .setControllerListener(listener)
                        .setOldController(holder.ivFotoPerfil.getController())
                        .build();
            } else {
                uri = Uri.parse("https://tocaredev.azurewebsites.net"+medico.getFoto().replace(" ", "%20"));
                dc = Fresco.newDraweeControllerBuilder()
                        .setUri(uri)
                        .setControllerListener(listener)
                        .setOldController(holder.ivFotoPerfil.getController())
                        .build();
            }
            holder.ivFotoPerfil.setController(dc);

        } catch (Exception e) {
            String erro = e.toString();
        }
        return convertView;
    }
    
}
