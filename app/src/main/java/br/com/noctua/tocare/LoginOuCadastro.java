package br.com.noctua.tocare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LoginOuCadastro extends AppCompatActivity {

    Button btPaciente, btMedico, btCadastrar;
    int countBackPressed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_ou_cadastro);

        btPaciente = (Button) findViewById(R.id.btPaciente);
        btMedico = (Button) findViewById(R.id.btMedico);
        btCadastrar = (Button) findViewById(R.id.btCadastro);

        btPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginPaciente.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        btMedico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginMedico.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        btCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the Signup activity
                AlertDialog.Builder mesg = new AlertDialog.Builder(LoginOuCadastro.this);
                mesg.setMessage("Você é um médico ou um paciente?");
                mesg.setTitle("");
                mesg.setCancelable(true);
                mesg.setPositiveButton("Paciente", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent i = new Intent(getApplicationContext(), CadastroPaciente.class);
                        startActivity(i);
                        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        finish();
                    }
                });
                mesg.setNegativeButton("Médico", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent i = new Intent(getApplicationContext(), CadastroMedico.class);
                        startActivity(i);
                        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        finish();
                    }
                });
                mesg.show();
            }
        });

    }



    @Override
    public void onBackPressed(){
        if(countBackPressed >= 1){
            AlertDialog.Builder mesg = new AlertDialog.Builder(LoginOuCadastro.this);
            mesg.setMessage("Deseja fechar o aplicativo?");
            mesg.setTitle("");
            mesg.setCancelable(true);
            mesg.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            mesg.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    countBackPressed = 0;
                    return;
                }
            });
            mesg.show();
        }

        countBackPressed++;
    }

}
