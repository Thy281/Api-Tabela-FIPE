package com.quesada.tabela_fipe.controller;

import com.quesada.tabela_fipe.model.Dados;
import com.quesada.tabela_fipe.model.Modelos;
import com.quesada.tabela_fipe.model.Veiculo;
import com.quesada.tabela_fipe.service.ConsumoApi;
import com.quesada.tabela_fipe.service.ConverterDados;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class Principal {
    private final Scanner input = new Scanner(System.in);

    private final ConsumoApi consumoApi = new ConsumoApi();
    private final ConverterDados converterDados = new ConverterDados();

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private final String URL_MARCAS = "/marcas";
    private final String URL_MODELOS = "/modelos";
    private final String URL_ANOS = "/anos";

    public void exibeMenu() {
        var menu = """
                ----- OPÇÕES -----
                Carros
                Motos
                Caminhãos
                
                Digite uma das opções para consular:
                """;

        System.out.print(menu);
        var opcao = input.nextLine();

        String endereco;

        if (opcao.toLowerCase().contains("carr")) {
            endereco = URL_BASE + "carros" + URL_MARCAS;
        } else if (opcao.toLowerCase().contains("mot")) {
            endereco = URL_BASE + "motos" + URL_MARCAS;
        } else if (opcao.toLowerCase().contains("caminh")) {
            endereco = URL_BASE + "caminhoes" + URL_MARCAS;
        } else {
            System.out.println("Opção inválida!");
            return;
        }

        try {
            var json = consumoApi.obterDados(endereco);
            var marcas = converterDados.obeterDados(json, Dados[].class);

            Arrays.stream(marcas)
                    .sorted(Comparator.comparing(Dados::codigo))
                    .forEach(System.out::println);

            System.out.println("Informe o código da marca para consulta:");
            var codigoMarca = input.nextLine();

            endereco = endereco + "/" + codigoMarca + URL_MODELOS;
            json = consumoApi.obterDados(endereco);
            var modeloLista = converterDados.obeterDados(json, Modelos.class);

            System.out.println("\nModelos dessa marca: ");
            modeloLista.modelos().stream()
                    .sorted(Comparator.comparing(Dados::codigo))
                    .forEach(System.out::println);

            System.out.println("\nDigite um trecho do nome do carro a ser buscado");
            var nomeVeiculo = input.nextLine();

            System.out.println("\nModelos filtrados");
            modeloLista.modelos().stream()
                    .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                    .forEach(System.out::println);

            System.out.println("\nDigite o código do modelo para buscar os valores de avaliação: ");
            var codigoModelo = input.nextLine();

            endereco = endereco + "/" + codigoModelo + URL_ANOS;
            json = consumoApi.obterDados(endereco);
            var anos = converterDados.obeterDados(json, Dados[].class);

            System.out.println("\nAnos disponíveis para este modelo:");
            Arrays.stream(anos).forEach(ano -> {
                String nomeAno = ano.nome();
                if (nomeAno.startsWith("32000")) {
                    nomeAno = "Zero KM";
                }
                System.out.println("Ano: " + nomeAno + " (Código: " + ano.codigo() + ")");
            });

            System.out.println("\nDigite o código do ano para consulta detalhada:");
            var codigoAno = input.nextLine();

            endereco = endereco + "/" + codigoAno;
            json = consumoApi.obterDados(endereco);
            Veiculo veiculo = converterDados.obeterDados(json, Veiculo.class);

            System.out.println("\nDetalhes do veículo:");
            System.out.println(veiculo);
        } catch (RuntimeException e) {
            System.out.println("Ocorreu um erro: Código inválido ou falha na comunicação com a API.");
            System.out.println("Verifique se digitou os códigos corretamente.");
        }
    }

}
