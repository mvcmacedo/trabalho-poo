package pucrs.myflight.modelo;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class GerenciadorAeronaves {

    private ArrayList<Aeronave> avioes;

    public GerenciadorAeronaves() {
        this.avioes = new ArrayList<>();
    }

//    public void adicionar(Aeronave aviao) {
//        avioes.add(aviao);
//    }

    public ArrayList<Aeronave> listarTodas() {
        return new ArrayList<>(avioes);
    }

    public Aeronave buscarCodigo(String codigo) {
        for(Aeronave a: avioes)
            if(a.getCodigo().equals(codigo))
                return a;
        return null;
    }

    public void carregaDados(String nomeArq) throws IOException {
        Path path = Paths.get(nomeArq);
        try (Scanner sc = new Scanner(Files.newBufferedReader(path, Charset.forName("utf8")))) {
            sc.useDelimiter("[;\n]"); // separadores: ; e nova linha
            String header = sc.nextLine(); // pula cabeçalho
            String cod, nome, capacidade;
            Integer cap;
            while (sc.hasNext()) {
                cod = sc.next();
                nome = sc.next();
                capacidade = sc.next();
                cap = Integer.parseInt(capacidade);
                Aeronave nova = new Aeronave(cod, nome, cap);
                adicionar(nova);
                //System.out.format("%s - %s (%s)%n", nome, data, cpf);
            }
        }
    }

    public void adicionar(Aeronave aero) {
        avioes.add(aero);
    }


    public void ordenarDescricao() {
        // Usando Comparable<Aeronave> em Aeronave
        //Collections.sort(avioes);

        // Usando expressão lambda
        //avioes.sort( (Aeronave a1, Aeronave a2) ->
        //    a1.getDescricao().compareTo(a2.getDescricao()));

        // Mesma coisa, usando método static da interface Comparator:
        //avioes.sort(Comparator.comparing(a -> a.getDescricao()));

        // Invertendo o critério de comparação com reversed():
        avioes.sort(Comparator.comparing(Aeronave::getDescricao).reversed());
    }

    public void ordenarCodigoDescricao() {
       // Ordenando pelo código e desempatando pela descrição
       avioes.sort(Comparator.comparing(Aeronave::getCodigo).
               thenComparing(Aeronave::getDescricao));
    }

    public void ordenarCodigo() {
        avioes.sort( (Aeronave a1, Aeronave a2) ->
            a1.getCodigo().compareTo(a2.getCodigo()));
    }
}
