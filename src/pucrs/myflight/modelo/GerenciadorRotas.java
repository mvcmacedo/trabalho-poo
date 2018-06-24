package pucrs.myflight.modelo;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;


public class GerenciadorRotas {

    private ArrayList<Rota> rotas;
    private GerenciadorAeroportos origem;
    private GerenciadorAeroportos destino;
    private GerenciadorCias cia;
    private GerenciadorAeronaves avioes;

    public GerenciadorRotas(GerenciadorCias cia, GerenciadorAeroportos origem, GerenciadorAeroportos destino, GerenciadorAeronaves avioes) {
        this.rotas = new ArrayList<>();
        this.cia = cia;
        this.origem = origem;
        this.destino = destino;
        this.avioes = avioes;
    }

    public ArrayList<Rota> listarTodas() {
        return new ArrayList<>(rotas);
    }

    public void carregaDados(String nomeArq) throws IOException {
        Path path = Paths.get(nomeArq);
        try (Scanner sc = new Scanner(Files.newBufferedReader(path, Charset.forName("utf8")))) {
            sc.useDelimiter("[;\n]"); // separadores: ; e nova linha
            String header = sc.nextLine(); // pula cabeçalho
            String airline, from, to, equipment, kjj, jkk;

            while (sc.hasNext()) {
                airline = sc.next();
                from = sc.next();
                to = sc.next();
                kjj = sc.next();
                jkk = sc.next();

                equipment = sc.next();
                String[] equipments = equipment.split("\\s+");

                for(String s : equipments){
                    Rota nova = new Rota(cia.buscarCodigo(airline), origem.buscarCodigo(from), destino.buscarCodigo(to), avioes.buscarCodigo(s));
                    adicionar(nova);
                }
                //System.out.format("%s - %s (%s)%n", nome, data, cpf);
            }
        }
    }

    public void adicionar(Rota rota) {
        rotas.add(rota);
    }

    public void ordenarCias() {
        Collections.sort(rotas);
    }

    public void ordenarNomesCias() {
        rotas.sort( (Rota r1, Rota r2) ->
          r1.getCia().getNome().compareTo(
          r2.getCia().getNome()));
    }

    public void ordenarNomesAeroportos() {
        rotas.sort( (Rota r1, Rota r2) ->
                r1.getOrigem().getNome().compareTo(
                r2.getOrigem().getNome()));
    }

    public void ordenarNomesAeroportosCias() {
        rotas.sort( (Rota r1, Rota r2) -> {
           int result = r1.getOrigem().getNome().compareTo(
                   r2.getOrigem().getNome());
           if(result != 0)
               return result;
           return r1.getCia().getNome().compareTo(
                   r2.getCia().getNome());
        });
    }

    public ArrayList<Rota> buscarOrigem(String codigo) {
        ArrayList<Rota> result = new ArrayList<>();
        for(Rota r: rotas)
            if(r.getOrigem().getCodigo().equals(codigo))
                result.add(r);
        return result;
    }
}
