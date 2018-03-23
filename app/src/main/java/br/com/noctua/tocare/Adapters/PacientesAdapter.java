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

import br.com.noctua.tocare.Objetos.Paciente;
import br.com.noctua.tocare.Objetos.ValidaCPF;
import br.com.noctua.tocare.R;

/**
 * Created by Raphael on 27/02/2018.
 */

public class PacientesAdapter extends ArrayAdapter<Paciente> {

    private ArrayList<Paciente> pacienteList;

    public PacientesAdapter(Context context, int textViewResourceId, ArrayList<Paciente> ListaLogsList) {
            super(context, textViewResourceId, ListaLogsList);
            this.pacienteList = new ArrayList<Paciente>();
            this.pacienteList.addAll(ListaLogsList);
    }

    private class ViewHolder {
        public TextView tvNome, tvCpf, tvDataNascimento, tvTipoSanguineo, tvGenero;
        public SimpleDraweeView ivFotoPerfil;
    }

    public Paciente getItem(int position){
        return pacienteList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            PacientesAdapter.ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.item_paciente_list, null);

                holder = new PacientesAdapter.ViewHolder();
                holder.tvNome = (TextView)convertView.findViewById(R.id.tv_nomePaciente);
                holder.ivFotoPerfil = (SimpleDraweeView) convertView.findViewById(R.id.ivFotoPerfil);
                holder.tvCpf = (TextView)convertView.findViewById(R.id.tv_cpf);
                holder.tvDataNascimento = (TextView)convertView.findViewById(R.id.tv_DataNascimento);
                holder.tvTipoSanguineo = (TextView)convertView.findViewById(R.id.tv_tipoSanguineo);
                holder.tvGenero = (TextView)convertView.findViewById(R.id.tv_genero);
                convertView.setTag(holder);
            } else {
                holder = (PacientesAdapter.ViewHolder) convertView.getTag();
            }

            Paciente paciente = pacienteList.get(position);

            holder.tvNome.setText(paciente.getNome() + " " + paciente.getSobrenome());
            try {
                holder.tvCpf.setText("C.P.F: " + ValidaCPF.imprimeCPF(paciente.getcpf()));
            } catch (Exception e){
                e.printStackTrace();
                holder.tvCpf.setText("C.P.F: " + paciente.getcpf());
            }
            holder.tvDataNascimento.setText("Data Nasc.: " + paciente.getDataNascimento());
            holder.tvTipoSanguineo.setText("Tipo Sanguíneo: " + paciente.getTipoSanguineo());
            holder.tvGenero.setText("Gênero: " + paciente.getGenero());

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

            String urlImagem = paciente.getFoto().replace(" ", "%20");

            Uri uri = null;
            DraweeController dc = null;

            if(urlImagem.contains("https")){
                uri = Uri.parse(paciente.getFoto().replace(" ", "%20"));
                dc = Fresco.newDraweeControllerBuilder()
                        .setUri(uri)
                        .setControllerListener(listener)
                        .setOldController(holder.ivFotoPerfil.getController())
                        .build();
            } else {
                uri = Uri.parse("https://tocaredev.azurewebsites.net"+paciente.getFoto().replace(" ", "%20"));
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
