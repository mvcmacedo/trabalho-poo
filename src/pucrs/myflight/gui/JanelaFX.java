package pucrs.myflight.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pucrs.myflight.modelo.*;

public class JanelaFX extends Application {

    final SwingNode mapkit = new SwingNode();

    private GerenciadorCias gerCias;
    private GerenciadorAeroportos gerAero;
    private GerenciadorRotas gerRotas;
    private GerenciadorAeronaves gerAvioes;
    private GerenciadorPaises gerPaises;

    private ArrayList<Aeroporto> aeroportos;
    private ArrayList<CiaAerea> cias;
    private ArrayList<Rota> rotas;
    private ArrayList<Aeronave> avioes;
    private ArrayList<Pais> paises;


    private GerenciadorMapa gerenciador;

    private EventosMouse mouse;

    private ObservableList<CiaAerea> comboCiasData;
    private ComboBox<CiaAerea> comboCia;

    @Override
    public void start(Stage primaryStage) throws Exception {

        setup();


        GeoPosition poa = new GeoPosition(-30.05, -51.18);
        gerenciador = new GerenciadorMapa(poa, GerenciadorMapa.FonteImagens.VirtualEarth);
        mouse = new EventosMouse();
        gerenciador.getMapKit().getMainMap().addMouseListener(mouse);
        gerenciador.getMapKit().getMainMap().addMouseMotionListener(mouse);

        createSwingContent(mapkit);

        BorderPane pane = new BorderPane();
        GridPane leftPane = new GridPane();
        GridPane form = new GridPane();
        GridPane horas = new GridPane();

        horas.setAlignment(Pos.BASELINE_RIGHT);
        horas.setHgap(2);
        horas.setVgap(-40);
        horas.setPadding(new Insets(10, 10, 10, 10));

        leftPane.setAlignment(Pos.CENTER);
        leftPane.setHgap(10);
        leftPane.setVgap(10);
        leftPane.setPadding(new Insets(10, 10, 10, 10));

        form.setAlignment(Pos.BASELINE_LEFT);
        form.setHgap(20);
        form.setVgap(20);
        form.setPadding(new Insets(10, 10, 10, 10));

        Button btnConsulta1 = new Button("Consulta 1");
        Button btnConsulta2 = new Button("Consulta 2");
        Button btnConsulta3 = new Button("Consulta 3");
        Button btnConsulta4 = new Button("Consulta 4");

        leftPane.add(btnConsulta1, 0, 0);
        leftPane.add(btnConsulta2, 1, 0);
        leftPane.add(btnConsulta3, 2, 0);
        leftPane.add(btnConsulta4, 3, 0);

        // botão para retornar ao menu de consultas
        Button voltar = new Button("Voltar");

        btnConsulta1.setOnAction(e -> {
            // monta o form da consulta
            form.add(voltar, 0, 0);
            form.add(new Label("Cia Aerea:"), 0, 1);
            TextField ciaAerea = new TextField();
            form.add(ciaAerea, 1, 1);
            Button btnBuscaCia = new Button("Buscar");
            form.add(btnBuscaCia, 2, 1);
            pane.setTop(form);
            btnBuscaCia.setOnAction(c -> {
                String cia = String.valueOf(ciaAerea.getText());
                consulta1(cia);
            });
        });

        btnConsulta2.setOnAction(e -> {
            form.add(voltar, 0, 0);
            form.add(new Label("Digite um país:"), 0, 1);
            TextField pais = new TextField();
            form.add(pais, 1, 1);
            Button btnBuscaPais = new Button("Buscar");
            form.add(btnBuscaPais, 2, 1);
            pane.setTop(form);
            btnBuscaPais.setOnAction(c -> {
                String paisCod = String.valueOf(pais.getText());
                consulta2(paisCod);
            });
        });

        btnConsulta3.setOnAction(e -> {
            form.getChildren().clear(); // limpa o form
            form.add(voltar, 0, 0);
            form.add(new Label("Aeroporto Origem:"), 0, 1);
            TextField origem = new TextField();
            form.add(origem, 1, 1);
            form.add(new Label("Aeroporto Destino:"), 2, 1);
            TextField destino = new TextField();
            form.add(destino, 3, 1);
            Button btnBuscarRotas = new Button("Buscar");
            form.add(btnBuscarRotas, 4, 1);
            pane.setTop(form);
            btnBuscarRotas.setOnAction(c -> {
                String origemCod = String.valueOf(origem.getText());
                String destinoCod = String.valueOf(destino.getText());
                ArrayList<Rota> rotasConsulta = consulta3(origemCod, destinoCod);
                form.add(new Label("Selecione uma rota: "), 6, 1);
                ComboBox routes = new ComboBox();
                form.add(routes, 7,1);
                ObservableList listaRotas = FXCollections.observableArrayList(
                        rotasConsulta);
                routes.setItems(listaRotas);

                routes.setCellFactory(lv -> {
                    ListCell<Rota> cell = new ListCell<Rota>() {
                        @Override
                        protected void updateItem(Rota item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(empty ? null : String.valueOf(item));
                        }
                    };
                    cell.setOnMousePressed(r -> {
                        horas.getChildren().clear();
                        if (! cell.isEmpty()) {
                            gerenciador.clear();
                            for(Rota route : rotas){
                                if(route.equals(cell.getItem())){
                                    Geo origemAero = route.getOrigem().getLocal();
                                    Geo destinoAero = route.getDestino().getLocal();
                                    double distancia = Geo.distancia(origemAero, destinoAero);
                                    double tempo = Math.ceil(distancia/890); // media de velocidade de um avião comercial
                                    int tempoVoo = (int) tempo;
                                    Tracado tracado = new Tracado();
                                    tracado.setWidth(5);
                                    tracado.setCor(Color.GREEN);
                                    tracado.addPonto(route.getOrigem().getLocal());
                                    tracado.addPonto(route.getDestino().getLocal());
                                    gerenciador.addTracado(tracado);
                                    horas.add(new Label(tempoVoo + " Horas") , 8,1);
                                    pane.setRight(horas);
                                }
                            }
                        }
                    });
                    return cell ;
                });


            });
        });

        btnConsulta4.setOnAction(e -> {
            consulta4();
        });

        voltar.setOnAction(e -> {
            form.getChildren().clear(); // limpa o form
            pane.setTop(leftPane); // retorna o menu de consultas
            gerenciador.clear();
            List<MyWaypoint> lstPoints = new ArrayList<>();
            gerenciador.setPontos(lstPoints);

        });

        pane.setCenter(mapkit);
        pane.setTop(leftPane);

        Scene scene = new Scene(pane, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Mapas com JavaFX");
        primaryStage.show();

    }

    // Inicializando os dados aqui...
    private void setup() {

        gerCias = new GerenciadorCias();

        try {
            gerCias.carregaDados("airlines.dat");
            this.cias = gerCias.listarTodas();
        } catch (IOException e) {
            System.out.println("Não foi possível ler airlines.dat!");
        }

        gerPaises = new GerenciadorPaises();

        try {
            gerPaises.carregaDados("countries.dat");
            this.paises = gerPaises.listarTodos();
        } catch (IOException e) {
            System.out.println("Não foi possível ler countries.dat!");
        }

        gerAvioes = new GerenciadorAeronaves();

        try {
            gerAvioes.carregaDados("equipment.dat");
            this.avioes = gerAvioes.listarTodas();
        } catch (IOException e) {
            System.out.println("Não foi possível ler equipment.dat!");
        }


        gerAero = new GerenciadorAeroportos(gerPaises);

        try {
            gerAero.carregaDados("airports.dat");
            this.aeroportos = gerAero.listarTodos();
        } catch (IOException e) {
            System.out.println("Não foi possível ler airports.dat!");
        }

        gerRotas = new GerenciadorRotas(gerCias, gerAero, gerAero, gerAvioes);

        try {
            gerRotas.carregaDados("routes.dat");
            this.rotas = gerRotas.listarTodas();
        } catch (IOException e) {
            System.out.println("Não foi possível ler routes.dat!");
        }

    }

    private void consulta1(String cia) {

        // Lista para armazenar o resultado da consulta
        List<MyWaypoint> lstPoints = new ArrayList<>();
        ArrayList<Aeroporto> aeros = gerRotas.getAeroportosPorCia(cia);
        ArrayList<Rota> rotas = gerRotas.getRotasPorCia(cia);

        gerenciador.clear();
        for (Rota r : rotas) {
            Tracado tr = new Tracado();
            tr.setWidth(5);
            tr.setCor(Color.BLUE);
            tr.addPonto(r.getOrigem().getLocal());
            tr.addPonto(r.getDestino().getLocal());
            gerenciador.addTracado(tr);


        }

        // Adiciona os locais de cada aeroporto (sem repetir) na lista de waypoints
        for (Aeroporto a : aeros) {
            lstPoints.add(new MyWaypoint(Color.RED, a.getCodigo(), a.getLocal(), 5));
        }

        // Para obter um ponto clicado no mapa, usar como segue:
        // GeoPosition pos = gerenciador.getPosicao();

        // Informa o resultado para o gerenciador
        gerenciador.setPontos(lstPoints);

        // Quando for o caso de limpar os traçados...
        // gerenciador.clear();

        gerenciador.getMapKit().repaint();
    }

    private void consulta2(String cod) {

        List<MyWaypoint> lstPoints = new ArrayList<>();
        ArrayList<String> aeros;

        if (cod.isEmpty()) { // usuário não digitou nada
            aeros = gerAero.getCods(); // pega todos aeroportos
        } else { // usuário digitou um país
            aeros = gerAero.getCodsPorPais(cod); // pega aeroportos do país digitado
        }
        Map<String, Integer> trafego = gerRotas.buscarRotasPorAero(aeros);
        for (Map.Entry<String, Integer> entry : trafego.entrySet()) {
            // System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
            Aeroporto a = gerAero.buscarCodigo(entry.getKey());
            double size;
            Color color;
            if (entry.getValue() < 50) {
                size = 5;
                color = Color.gray;
            } else if (entry.getValue() >= 50 && entry.getValue() < 100) {
                size = 10;
                color = Color.CYAN;
            } else if (entry.getValue() >= 100 && entry.getValue() < 250) {
                size = 20;
                color = Color.blue;
            } else {
                size = 40;
                color = Color.red;
            }
            lstPoints.add(new MyWaypoint(color, a.getCodigo(), a.getLocal(), size));
        }

        gerenciador.setPontos(lstPoints);

        gerenciador.getMapKit().repaint();

    }

    private ArrayList<Rota> consulta3(String origem, String destino) {
//        System.out.println(origem + " " + destino);
        List<MyWaypoint> lstPoints = new ArrayList<>();
        ArrayList<Rota> rotasConsulta = gerRotas.buscarRotasOrigemDestino(origem, destino);
        Aeroporto aeroOrigem = gerAero.buscarCodigo(origem);
        Aeroporto aeroDestino = gerAero.buscarCodigo(destino);

        gerenciador.clear();
        for (Rota r : rotasConsulta) {
            Tracado tr = new Tracado();
            tr.setWidth(5);
            tr.setCor(Color.BLUE);
            tr.addPonto(r.getOrigem().getLocal());
            if(!(r.getOrigem().getCodigo().equalsIgnoreCase(origem))) lstPoints.add(new MyWaypoint(Color.gray, r.getOrigem().getCodigo(), r.getOrigem().getLocal(), 5));
            tr.addPonto(r.getDestino().getLocal());
            if(!(r.getDestino().getCodigo().equalsIgnoreCase(destino))) lstPoints.add(new MyWaypoint(Color.gray, r.getDestino().getCodigo(), r.getDestino().getLocal(), 5));
            gerenciador.addTracado(tr);
        }

        lstPoints.add(new MyWaypoint(Color.RED, aeroOrigem.getCodigo(), aeroOrigem.getLocal(), 10));
        lstPoints.add(new MyWaypoint(Color.GREEN, aeroDestino.getCodigo(), aeroDestino.getLocal(), 10));

        gerenciador.setPontos(lstPoints);
        gerenciador.getMapKit().repaint();

        return rotasConsulta;

    }

    private void consulta4() {

    }

    private class EventosMouse extends MouseAdapter {
        private int lastButton = -1;

        @Override
        public void mousePressed(MouseEvent e) {
            JXMapViewer mapa = gerenciador.getMapKit().getMainMap();
            GeoPosition loc = mapa.convertPointToGeoPosition(e.getPoint());
            // System.out.println(loc.getLatitude()+", "+loc.getLongitude());
            lastButton = e.getButton();
            // Botão 3: seleciona localização
            if (lastButton == MouseEvent.BUTTON3) {
                gerenciador.setPosicao(loc);
                gerenciador.getMapKit().repaint();
            }
        }
    }

    private void createSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                swingNode.setContent(gerenciador.getMapKit());
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
