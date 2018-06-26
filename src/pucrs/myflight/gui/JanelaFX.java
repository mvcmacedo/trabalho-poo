package pucrs.myflight.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
            consulta3();
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
            Tracado tr2 = new Tracado();
            tr2.setWidth(5);
            tr2.setCor(Color.BLUE);
            tr2.addPonto(r.getOrigem().getLocal());
            tr2.addPonto(r.getDestino().getLocal());
            gerenciador.addTracado(tr2);


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

        if (cod.isEmpty()) {
            aeros = gerAero.getCods();
        } else {
            aeros = gerAero.getCodsPorPais(cod);
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

    private void consulta3() {

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
