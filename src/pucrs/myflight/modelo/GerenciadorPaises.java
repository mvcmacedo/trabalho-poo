package pucrs.myflight.modelo;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class GerenciadorPaises {
    private Map<String, Pais> paises;

    public GerenciadorPaises() {
//        this.empresas = new HashMap<>();
//        this.empresas = new TreeMap<>();
        this.paises = new LinkedHashMap<>();
    }

    public Map<String, Pais> getAll(){
        return paises;
    }

    public ArrayList<Pais> listarTodos() {
        return new ArrayList<>(paises.values());
    }

    public void carregaDados(String nomeArq) throws IOException {
        Path path = Paths.get(nomeArq);
        try (Scanner sc = new Scanner(Files.newBufferedReader(path, Charset.forName("utf8")))) {
            sc.useDelimiter("[;\n]"); // separadores: ; e nova linha
            String header = sc.nextLine(); // pula cabeçalho
            String cod, nome;
            while (sc.hasNext()) {
                cod = sc.next();
                nome = sc.next();
                Pais nova = new Pais(cod, nome);
                adicionar(nova);
                //System.out.format("%s - %s (%s)%n", nome, data, cpf);
            }
        }
    }

    public void adicionar(Pais pais) {
        paises.put(pais.getCodigo(),
                pais);
    }

    public Pais buscarCodigo(String cod) {
        return paises.get(cod);
//        for (CiaAerea cia : empresas)
//            if (cia.getCodigo().equals(cod))
//                return cia;
//        return null;
    }

    public Pais buscarNome(String nome) {
        for(Pais pais: paises.values())
            if(pais.getNome().equals(nome))
                return pais;
        return null;
    }
}
