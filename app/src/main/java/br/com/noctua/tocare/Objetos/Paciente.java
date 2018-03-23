package br.com.noctua.tocare.Objetos;

/**
 * Created by Raphael on 21/02/2018.
 */

public class Paciente extends Pessoa {

    private int idPaciente, idPacienteMedico;
    private Boolean planoDeSaude, deficienciaFisica;
    private String descricaoGeral, descricaoDeficiencia, tipoSanguineo, cadastro;
    private Double altura, peso;
    private Double imc;
    private Prontuario prontuario;
    
    public Paciente(){
        
    }

    public Paciente(int idPaciente, Boolean planoDeSaude, Boolean deficienciaFisica, String descricaoGeral, String tipoSanguineo,
                    String cadastro, Double altura, Double peso, Double imc, Prontuario prontuario){
        this.idPaciente = idPaciente;
        this.planoDeSaude = planoDeSaude;
        this.deficienciaFisica = deficienciaFisica;
        this.descricaoGeral = descricaoGeral;
        this.tipoSanguineo = tipoSanguineo;
        this.cadastro = cadastro;
        this.altura = altura;
        this.peso = peso;
        this.imc = imc;
        this.prontuario = prontuario;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public Boolean getPlanoDeSaude() {
        return planoDeSaude;
    }

    public void setPlanoDeSaude(Boolean planoDeSaude) {
        this.planoDeSaude = planoDeSaude;
    }

    public Boolean getDeficienciaFisica() {
        return deficienciaFisica;
    }

    public void setDeficienciaFisica(Boolean deficienciaFisica) {
        this.deficienciaFisica = deficienciaFisica;
    }

    public String getDescricaoGeral() {
        return descricaoGeral;
    }

    public void setDescricaoGeral(String descricaoGeral) {
        this.descricaoGeral = descricaoGeral;
    }

    public String getTipoSanguineo() {
        return tipoSanguineo;
    }

    public void setTipoSanguineo(String tipoSanguineo) {
        this.tipoSanguineo = tipoSanguineo;
    }

    public String getCadastro() {
        return cadastro;
    }

    public void setCadastro(String cadastro) {
        this.cadastro = cadastro;
    }

    public Double getAltura() {
        return altura;
    }

    public void setAltura(Double altura) {
        this.altura = altura;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getImc() {
        return imc;
    }

    public void setImc(Double imc) {
        this.imc = imc;
    }

    public Prontuario getProntuario() {
        return prontuario;
    }

    public void setProntuario(Prontuario prontuario) {
        this.prontuario = prontuario;
    }

    public String getDescricaoDeficiencia() {
        return descricaoDeficiencia;
    }

    public void setDescricaoDeficiencia(String descricaoDeficiencia) {
        this.descricaoDeficiencia = descricaoDeficiencia;
    }

    public int getIdPacienteMedico() {
        return idPacienteMedico;
    }

    public void setIdPacienteMedico(int idPacienteMedico) {
        this.idPacienteMedico = idPacienteMedico;
    }
}
