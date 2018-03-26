package br.com.noctua.tocare;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.math.RoundingMode;
import java.text.NumberFormat;

import br.com.noctua.tocare.Objetos.Paciente;
import br.com.noctua.tocare.Objetos.Util;

public class FichaMedica extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button btEditar;
    private String origem;

    private TextView tvPlanoDeSaude, tvDeficiencia, tvDescricaoDeficienciaFisica,
            tvTextoDescricaoDeficienciaFisica, tvDescricaoGeral, tvTipoSanguineo,
            tvAltura, tvPeso, tvImcTexto, tvImc, tvUsername, tvDataNascimento;
    private View viewDescricaoDeficienciaFisica;
    private SimpleDraweeView ivFotoPerfil;
    
    private Paciente pacienteFichaMedica = new Paciente();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_ficha_medica);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle(R.string.ficha_medica);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_forward_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ivFotoPerfil = (SimpleDraweeView) findViewById(R.id.ivFotoPerfil);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvDataNascimento = (TextView) findViewById(R.id.tv_DataNascimento);
        tvPlanoDeSaude = (TextView)findViewById(R.id.tv_PlanoDeSaude);
        tvDeficiencia = (TextView)findViewById(R.id.tv_deficienciaFisica);
        tvTextoDescricaoDeficienciaFisica = (TextView)findViewById(R.id.tv_textoDescricaoDeficienciaFisica);
        tvDescricaoDeficienciaFisica = (TextView)findViewById(R.id.tv_descricaoDeficienciaFisica);
        viewDescricaoDeficienciaFisica = (View)findViewById(R.id.v_descricaoDeficienciaFisica);
        tvDescricaoGeral = (TextView)findViewById(R.id.tv_descricaoGeral);
        tvTipoSanguineo = (TextView)findViewById(R.id.tv_tipoSanguineo);
        tvAltura = (TextView)findViewById(R.id.tv_altura);
        tvPeso = (TextView)findViewById(R.id.tv_peso);
        tvImcTexto = (TextView)findViewById(R.id.tv_imcTexto);
        tvImc = (TextView)findViewById(R.id.tv_imcValor);

        btEditar = (Button) findViewById(R.id.btEditar);
        btEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), EditarFichaMedica.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        Intent intent = getIntent();
        origem = intent.getStringExtra("origem");

        if(!origem.equals("homePaciente") && !origem.equals("editarFichaMedica")){
            btEditar.setVisibility(View.INVISIBLE);
            btEditar.setHeight(0);
            pacienteFichaMedica = DetalhesPaciente.paciente;
        } else {
            pacienteFichaMedica = HomePaciente.paciente;
        }

        montarTela();

    }

    public void montarTela(){
        if(pacienteFichaMedica.getPlanoDeSaude()){
            tvPlanoDeSaude.setText("Sim");
        } else {
            tvPlanoDeSaude.setText("Não");
        }
        if(pacienteFichaMedica.getDeficienciaFisica()){
            tvDeficiencia.setText("Sim");
            tvDescricaoDeficienciaFisica.setText(pacienteFichaMedica.getDescricaoDeficiencia());
        } else {
            tvDeficiencia.setText("Não");
            tvTextoDescricaoDeficienciaFisica.setVisibility(View.GONE);
            tvDescricaoDeficienciaFisica.setVisibility(View.GONE);
            viewDescricaoDeficienciaFisica.setVisibility(View.GONE);
        }
        if(pacienteFichaMedica.getDescricaoGeral().isEmpty()){
            tvDescricaoGeral.setText("Não informado");
        } else {
            tvDescricaoGeral.setText(pacienteFichaMedica.getDescricaoGeral());
        }
        if(pacienteFichaMedica.getTipoSanguineo().isEmpty()){
            tvTipoSanguineo.setText("Não informado");
        } else {
            tvTipoSanguineo.setText(pacienteFichaMedica.getTipoSanguineo());
        }
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        format.setMaximumIntegerDigits(2);
        format.setRoundingMode(RoundingMode.HALF_UP);
        tvAltura.setText(format.format(pacienteFichaMedica.getAltura()));
        tvPeso.setText(format.format(pacienteFichaMedica.getPeso()));
        Double auxImc = pacienteFichaMedica.getPeso()/(pacienteFichaMedica.getAltura() * pacienteFichaMedica.getAltura());
        if(!format.format(pacienteFichaMedica.getAltura()).equals("0,00") && !format.format(pacienteFichaMedica.getPeso()).equals("0,00")){
            tvImc.setText("(" + format.format(auxImc) + ")");
            if(auxImc >= 12 && auxImc <= 18.99){
                tvImcTexto.setText("Magro ");
                tvImcTexto.setTextColor(Color.parseColor("#2ca1e1"));
            } else if(auxImc >= 19 && auxImc <= 24.99){
                tvImcTexto.setText("Saudável ");
                tvImcTexto.setTextColor(Color.parseColor("#46de24"));
            } else if(auxImc >= 25 && auxImc <= 29.99){
                tvImcTexto.setText("Gordo ");
                tvImcTexto.setTextColor(Color.parseColor("#dae725"));
            } else if(auxImc >= 30 && auxImc <= 39.99){
                tvImcTexto.setText("Obesidade ");
                tvImcTexto.setTextColor(Color.parseColor("#e68b23"));
            } else if(auxImc >= 40){
                tvImcTexto.setText("Obesidade Morbida ");
                tvImcTexto.setTextColor(Color.parseColor("#e12727"));
            }
        } else {
            tvImc.setText("");
            tvImcTexto.setText("Não Calculado");
        }

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

        String urlImagem = pacienteFichaMedica.getFoto().replace(" ", "%20");

        Uri uri = null;
        DraweeController dc = null;

        if(urlImagem.contains("https")){
            uri = Uri.parse(pacienteFichaMedica.getFoto().replace(" ", "%20"));
            dc = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setControllerListener(listener)
                    .setOldController(ivFotoPerfil.getController())
                    .build();
        } else {
            uri = Uri.parse("https://tocaredev.azurewebsites.net"+pacienteFichaMedica.getFoto().replace(" ", "%20"));
            dc = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setControllerListener(listener)
                    .setOldController(ivFotoPerfil.getController())
                    .build();
        }
        ivFotoPerfil.setController(dc);

        tvUsername.setText(pacienteFichaMedica.getNome() + " " + pacienteFichaMedica.getSobrenome());

        int idade = Util.retornaIdade(pacienteFichaMedica.getDataNascimento());

        tvDataNascimento.setText(pacienteFichaMedica.getDataNascimento() + " (" + idade + ")");

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home){
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
