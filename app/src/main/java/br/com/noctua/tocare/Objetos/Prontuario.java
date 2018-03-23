package br.com.noctua.tocare.Objetos;

import java.util.ArrayList;

/**
 * Created by Raphael on 21/02/2018.
 */

public class Prontuario {

    private int id;
    private String anamnese, exameFisico, hipoteseDiagnostica, planoTerapeutico;
    private ArrayList<Medico> medicosLiberadosList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnamnese() {
        return anamnese;
    }

    public void setAnamnese(String anamnese) {
        this.anamnese = anamnese;
    }

    public String getExameFisico() {
        return exameFisico;
    }

    public void setExameFisico(String exameFisico) {
        this.exameFisico = exameFisico;
    }

    public String getHipoteseDiagnostica() {
        return hipoteseDiagnostica;
    }

    public void setHipoteseDiagnostica(String hipoteseDiagnostica) {
        this.hipoteseDiagnostica = hipoteseDiagnostica;
    }

    public String getPlanoTerapeutico() {
        return planoTerapeutico;
    }

    public void setPlanoTerapeutico(String planoTerapeutico) {
        this.planoTerapeutico = planoTerapeutico;
    }

    public ArrayList<Medico> getMedicosLiberadosList() {
        return medicosLiberadosList;
    }

    public void setMedicosLiberadosList(ArrayList<Medico> medicosLiberadosList) {
        this.medicosLiberadosList = medicosLiberadosList;
    }
}
