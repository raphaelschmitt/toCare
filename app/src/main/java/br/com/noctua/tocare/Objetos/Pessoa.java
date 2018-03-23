package br.com.noctua.tocare.Objetos;

/**
 * Created by Raphael on 28/02/2018.
 */

public class Pessoa {

    private int idPessoa;
    private String foto;
    private String nome, sobrenome, cpf, identidade, genero, dataNascimento, email;
    private String telefone;

    public Pessoa(){

    }

    public Pessoa(int idPessoa, String nome, String sobrenome, String genero, String dataNascimento,
                  String identidade, String cpf, String email, String telefone, String foto){
        this.idPessoa = idPessoa;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.genero = genero;
        this.dataNascimento = dataNascimento;
        this.identidade = identidade;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.foto = foto;
    }

    public int getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(int idPessoa) {
        this.idPessoa = idPessoa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getcpf() {
        return cpf;
    }

    public void setcpf(String cpf) {
        this.cpf = cpf;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTelefoneList() {
        return telefone;
    }

    public void setTelefoneList(String telefone) {
        this.telefone = telefone;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getIdentidade() {
        return identidade;
    }

    public void setIdentidade(String identidade) {
        this.identidade = identidade;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
