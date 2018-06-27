package pucrs.myflight.modelo;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import pucrs.myflight.modelo.GerenciadorPaises;

public class GerenciadorAeroportos {

    private ArrayList<Aeroporto> aeroportos;
    private GerenciadorPaises paises;

    public GerenciadorAeroportos(GerenciadorPaises paises) {
        this.aeroportos = new ArrayList<>();
        this.paises = paises;
    }

    public void ordenarNomes() {
        Collections.sort(aeroportos);
    }

    public void carregaDados(String nomeArq) throws IOException {
        Path path = Paths.get(nomeArq);
        try (Scanner sc = new Scanner(Files.newBufferedReader(path, Charset.forName("utf8")))) {
            sc.useDelimiter("[;\n]"); // separadores: ; e nova linha
            String header = sc.nextLine(); // pula cabeçalho
            String cod, nome, lat, lon, abrevi, codPais;
            Double latitude, longitude;
            Pais pais;
            while (sc.hasNext()) {
                cod = sc.next();
                lat = sc.next();
                lon = sc.next();
                nome = sc.next();
                abrevi = sc.next();
                latitude = Double.parseDouble(lat);
                longitude = Double.parseDouble(lon);

                // verifica se o país está na lista de países
                pais = paises.buscarCodigo(abrevi);
                codPais = pais.getCodigo();

                Geo loc = new Geo(latitude, longitude);
                Aeroporto nova = new Aeroporto(cod, nome, loc, codPais);
                adicionar(nova);
                //System.out.format("%s - %s (%s)%n", nome, data, cpf);
            }
        }
    }

    public void adicionar(Aeroporto aeroporto) {
        aeroportos.add(
                aeroporto);
    }

    public ArrayList<String> getCods() {
        ArrayList<String> codigos = new ArrayList<>();
        for (Aeroporto a : aeroportos) {
            codigos.add(a.getCodigo());
        }
        return codigos;
    }

    public ArrayList<String> getCodsPorPais(String cod) {
        ArrayList<String> codigos = new ArrayList<>();
        for (Aeroporto a : aeroportos) {
            if (a.getPais().equalsIgnoreCase(cod)) codigos.add(a.getCodigo());
        }
        return codigos;
    }

    public int tempoEntreAeroportos(Aeroporto origem, Aeroporto destino){
        double distancia = Geo.distancia(origem.getLocal(), destino.getLocal());
        double tempo = Math.ceil(distancia/890); // 890 = media de vel aviao comercial
        int tempoInt = (int) tempo;
        return tempoInt;
    }

    public ArrayList<Aeroporto> listarTodos() {
        return new ArrayList<>(aeroportos);
    }

    public Aeroporto buscarCodigo(String codigo) {
        for (Aeroporto a : aeroportos)
            if (a.getCodigo().equals(codigo))
                return a;
        return null;
    }
}
