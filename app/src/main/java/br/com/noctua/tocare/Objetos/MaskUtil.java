package br.com.noctua.tocare.Objetos;

/**
 * Created by Raphael on 22/02/2018.
 */

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;


public class MaskUtil {

    private static final String CPFMask = "###.###.###-##";
    private static final String CNPJMask = "##.###.###/####-##";

    public static String unmask(String s) {
        return s.replaceAll("[^0-9]*", "");
    }

    private static String getDefaultMask(String str) {
        String defaultMask = CPFMask;
        if (str.length() == 14){
            defaultMask = CNPJMask;
        }
        return CPFMask;
    }

    public static TextWatcher insert(final EditText editText, final MaskType maskType) {
        return new TextWatcher() {

            boolean isUpdating;
            String oldValue = "";

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = MaskUtil.unmask(s.toString());
                String mask;
                switch (maskType) {
                    case CPF:
                        mask = CPFMask;
                        break;
                    case CNPJ:
                        mask = CNPJMask;
                        break;
                    default:
                        mask = getDefaultMask(value);
                        break;
                }

                String maskAux = "";
                if (isUpdating) {
                    oldValue = value;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : mask.toCharArray()) {
                    if ((m != '#' && value.length() > oldValue.length()) || (m != '#' && value.length() < oldValue.length() && value.length() != i)) {
                        maskAux += m;
                        continue;
                    }

                    try {
                        maskAux += value.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                editText.setText(maskAux);
                editText.setSelection(maskAux.length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String cpf = removeMascara(editText.getText().toString());
                if(!ValidaCPF.isCPF(cpf)){
                    editText.setError("Insira um CPF válido!");
                } else {

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

            public String removeMascara(String cpf){

                cpf = cpf.replaceAll("[.-]", "");

                return cpf;
            }

        };
    }

}
