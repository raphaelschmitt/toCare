package br.com.noctua.tocare.Objetos;

/**
 * Created by Raphael on 21/02/2018.
 */

public class Medico extends Pessoa{

    private int idMedico, idPessoaPaciente, idPacienteMedico;
    private String paisOrigem, registroMedico, documentoMedico;

    public Medico(){

    }

    public Medico(int idMedico, String paisOrigem, String registroMedico, String documentoMedico){
        this.idMedico = idMedico;
        this.paisOrigem = paisOrigem;
        this.registroMedico = registroMedico;
        this.documentoMedico = documentoMedico;
    }

    public int getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(int idMedico) {
        this.idMedico = idMedico;
    }

    public String getPaisOrigem() {
        return paisOrigem;
    }

    public void setPaisOrigem(String paisOrigem) {
        this.paisOrigem = paisOrigem;
    }

    public String getRegistroMedico() {
        return registroMedico;
    }

    public void setRegistroMedico(String registroMedico) {
        this.registroMedico = registroMedico;
    }

    public String getDocumentoMedico() {
        return documentoMedico;
    }

    public void setDocumentoMedico(String documentoMedico) {
        this.documentoMedico = documentoMedico;
    }

    public int getIdPessoaPaciente() {
        return idPessoaPaciente;
    }

    public void setIdPessoaPaciente(int idPessoaPaciente) {
        this.idPessoaPaciente = idPessoaPaciente;
    }

    public int getIdPacienteMedico() {
        return idPacienteMedico;
    }

    public void setIdPacienteMedico(int idPacienteMedico) {
        this.idPacienteMedico = idPacienteMedico;
    }
}
