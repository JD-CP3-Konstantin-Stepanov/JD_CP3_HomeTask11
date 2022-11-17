package org.example;

import com.opencsv.CSVReader;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class ClientLog {
    @CsvBindByName(column = "productName")
    protected int productNum;
    @CsvBindByName(column = "productPrice")
    protected int amount;
    protected List<ClientLog> logAsList = new ArrayList<>();

    //Конструктор класса ClientLog
    public ClientLog(int productNum, int amount) {
        this.productNum = productNum;
        this.amount = amount;
    }

    //Метод добавления объектов в класс ClientLog
    public void log(int productNum, int amount) {
        ClientLog log = new ClientLog(productNum, amount);
        logAsList.add(log);
    }

    //Выгрузка данных log в формате CSV
    public void exportAsCSV(File txtFile) {
        //Переопределение заголовка CSV файла согласно аннотации
        HeaderColumnNameMappingStrategy<ClientLog> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(ClientLog.class);
        String headerLine = Arrays.stream(ClientLog.class.getDeclaredFields())
                .map(field -> field.getAnnotation(CsvBindByName.class))
                .filter(Objects::nonNull)
                .map(CsvBindByName::column)
                .collect(Collectors.joining(","));

        try (StringReader reader = new StringReader(headerLine)) {
            CsvToBean<ClientLog> csv = new CsvToBeanBuilder<ClientLog>(reader)
                    .withType(ClientLog.class)
                    .withMappingStrategy(strategy)
                    .build();
            for (ClientLog row : csv) {
            }
        }
        //Заполнение списка предыдущими значениями log из файла
        if (txtFile.exists()) {
            try (CSVReader reader = new CSVReader(new FileReader(txtFile))) {
                String[] nextLine;
                int headerSkip = 0;
                while ((nextLine = reader.readNext()) != null) {
                    //В учёт берутся значения исключая header CSV
                    headerSkip++;
                    if (headerSkip >= 2) {
                        logAsList.add(new ClientLog(Integer.parseInt(nextLine[0]), Integer.parseInt(nextLine[1])));
                    }
                }
            } catch (IOException | CsvValidationException e) {
                e.printStackTrace();
            }
        }
        //Заполнение CSV файла
        try (var writer = Files.newBufferedWriter(txtFile.toPath())) {
            StatefulBeanToCsv<ClientLog> csvFinal = new StatefulBeanToCsvBuilder<ClientLog>(writer)
                    .withMappingStrategy(strategy)
                    .build();
            csvFinal.write(logAsList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
