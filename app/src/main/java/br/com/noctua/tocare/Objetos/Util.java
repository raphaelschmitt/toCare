package br.com.noctua.tocare.Objetos;

import java.util.Calendar;

/**
 * Created by Raphael on 14/03/2018.
 */

public class Util {
    public static boolean parseIntBoolean(int i){
        if(i == 1){
            return true;
        } else {
            return false;
        }
    }
    
    public static int retornaIdade(String dataNascimento){

        int auxAno = Integer.parseInt(""+ dataNascimento.charAt(6)+dataNascimento.charAt(7)+dataNascimento.charAt(8)+dataNascimento.charAt(9));
        int auxMes = Integer.parseInt(""+dataNascimento.charAt(3) + dataNascimento.charAt(4));
        int auxDia = Integer.parseInt(""+dataNascimento.charAt(0) + dataNascimento.charAt(1));

        Calendar calendar = Calendar.getInstance();

        int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        int idade = ano - auxAno;

        idade--;

        if(auxMes < mes){
            idade++;
        } else if (auxMes == mes){
            if(auxDia <= dia){
                idade++;
            }
        }
        
        return idade;
    }
}
