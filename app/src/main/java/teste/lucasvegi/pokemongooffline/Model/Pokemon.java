package teste.lucasvegi.pokemongooffline.Model;

import android.database.Cursor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import teste.lucasvegi.pokemongooffline.Util.BancoDadosSingleton;

/**
 * Created by Lucas on 02/12/2016.
 */
public class Pokemon implements Serializable{
    private int numero;
    private String nome;
    private String categoria;
    private int foto;
    private int icone;
    private List<Tipo> tipos;
    private int idDoce;
    private int idPokemonBase;
    Pokemon evolucao;

    public Pokemon(){

    }

    protected Pokemon(int numero, String nome, String categoria, int foto, int icone, int idDoce, int idPokemonBase, ControladoraFachadaSingleton cg){
        this.numero = numero;
        this.nome = nome;
        this.categoria = categoria;
        this.foto = foto;
        this.icone = icone;
        this.tipos = new ArrayList<Tipo>();
        this.idDoce = idDoce;
        this.idPokemonBase = idPokemonBase;

        preencherTipos(cg);
    }

    private void preencherTipos(ControladoraFachadaSingleton cg){
        //Select t.idTipo idTipo from pokemon p, tipo t, pokemontipo pt where p.idPokemon = pt.idPokemon and t.idTipo = pt.idTipo and p.idPokemon = numero
        Cursor cTipo = BancoDadosSingleton.getInstance().buscar("pokemon p, tipo t, pokemontipo pt",
                new String[]{"t.idTipo idTipo"},
                "p.idPokemon = pt.idPokemon AND t.idTipo = pt.idTipo AND p.idPokemon = " + this.numero,
                "");

        while (cTipo.moveToNext()){
            int idT = cTipo.getColumnIndex("idTipo");

            //procura o tipo retornado do banco na lista de tipos da controladora geral
            for(Tipo t : cg.getTipos()){
                if(t.getIdTipo() == cTipo.getInt(idT)){
                    this.tipos.add(t);
                }
            }
        }
        cTipo.close();
    }

    public List<Tipo> getTipos() {
        return tipos;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getFoto() {
        return foto;
    }

    public void setFoto(int foto) {
        this.foto = foto;
    }

    public int getIcone() {
        return icone;
    }

    public void setIcone(int icone) {
        this.icone = icone;
    }

    public int getIdDoce(){return idDoce;}

    public int getIdPokemonBase(){return idPokemonBase;}

    public Pokemon getEvolucao() {
        /*
        Cursor cPkmn = BancoDadosSingleton.getInstance().buscar("pokemon p, pokemon pe",
                new String[]{"pe.numero idPokemon", "pe.nome nome", "pe.categoria categoria", "pe.foto foto", "pe.icone icone",
                        "pe.idDoce idDoce", "pe.idPokemonBase idPokemonBase"},
                "pe.idPokemonBase = '" + this.numero + "'", null);

        cPkmn.moveToNext();

        int numero = cPkmn.getColumnIndex("idPokemon");
        int nome = cPkmn.getColumnIndex("nome");
        int categoria = cPkmn.getColumnIndex("categoria");
        int foto = cPkmn.getColumnIndex("foto");
        int icone = cPkmn.getColumnIndex("icone");
        int idDoce = cPkmn.getColumnIndex("idDoce");
        int idPokemonBase = cPkmn.getColumnIndex("idPokemonBase");
        evolucao = new Pokemon(cPkmn.getInt(numero),cPkmn.getString(nome),cPkmn.getString(categoria),
                cPkmn.getInt(foto),cPkmn.getInt(icone),cPkmn.getInt(idDoce), cPkmn.getInt(idPokemonBase),
                ControladoraFachadaSingleton.getInstance());

         */

        return evolucao;
    }

    public void setEvolucao(Pokemon evolucao) {
        this.evolucao = evolucao;
    }

    public int getQuantidadeDoces() {
        switch (this.categoria) {
            case "C":
                return 25;
            case "I":
                return 50;
            case "R":
                return 75;
            default:
                return 100;
        }
    }

    public void evoluir() {
        // Caso o pokemon não evolua
        if (evolucao == null) {
            return;
        }
        Cursor c = BancoDadosSingleton.getInstance().buscar("doce",new String[]{"idDoce","nome","quant"},"idDoce = " + String.valueOf(this.idDoce),"");
        Doce doce = new Doce();
        doce.setIdDoce(c.getInt(c.getColumnIndex("idDoce")));
        doce.setNomePkm(c.getString(c.getColumnIndex("nome")));
        doce.setQuantidade(c.getInt(c.getColumnIndex("quant")));
        if (doce.getQuantidade() < getQuantidadeDoces()) {
            return;
        }
        setNome(evolucao.getNome());
        setNumero(evolucao.getNumero());
        setCategoria(evolucao.getCategoria());
        setFoto(evolucao.getFoto());
        setIcone(evolucao.getIcone());
        // TODO VER COMO ATUALIZAR OS TIPOS DOS POKEMONS
        setEvolucao(evolucao.getEvolucao());
    }

    @Override
    public boolean equals(Object obj) {
        try {
            //Verificando se o segundo participante está nulo
            if (obj == null)
                return false;

            //verifica se são da mesma classe
            if (this.getClass() != obj.getClass())
                return false;

            //verifica se ocupam o mesmo lugar na memória
            if (super.equals(obj))
                return true;

            Pokemon pkmn = (Pokemon) obj;

            //Compara os dois objetos pelo estado interno
            if(this.getNumero() == pkmn.getNumero())
                return true;
            else
                return false;

        }catch (Exception e){
            return false;
        }
    }

    @Override
    public int hashCode() {
        //geração própria da hashCode para evitar colisão - objetos de classes diferentes com o mesmo hashCode
        //evita também que NÃO se retorne o mesmo hashCode para o mesmo objeto
        try {
            int numPrimo = 17;
            int hash = 1;

            //TÉCNICA: somar os hashCodes de todos os atributos da classe e multiplicar por um número primo
            hash = numPrimo * hash + ((this.nome == null) ? 0 : this.nome.hashCode());
            hash = numPrimo * hash + ((this.categoria == null) ? 0 : this.categoria.hashCode());
            hash = numPrimo * hash + (this.numero);
            hash = numPrimo * hash + (this.foto);
            hash = numPrimo * hash + (this.icone);
            hash = numPrimo * hash + (this.tipos.get(0).getIdTipo());
            hash = numPrimo * hash + ((this.tipos.get(0).getNome() == null) ? 0 : this.tipos.get(0).getNome().hashCode());
            if (this.tipos.size() > 1){
                hash = numPrimo * hash + (this.tipos.get(1).getIdTipo());
                hash = numPrimo * hash + ((this.tipos.get(1).getNome() == null) ? 0 : this.tipos.get(1).getNome().hashCode());
            }

            return hash;
        }catch (Exception e){
            return super.hashCode();
        }
    }
}
