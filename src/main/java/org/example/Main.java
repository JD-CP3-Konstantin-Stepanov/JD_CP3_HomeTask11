package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static boolean enabledLoad;
    private static String fileNameLoad;
    private static String formatLoad;
    private static boolean enabledSave;
    private static String fileNameSave;
    private static String formatSave;
    private static boolean enabledLog;
    private static String fileNameLog;

    public static void main(String[] args) {
        //Заполнение параметров работы программы
        XMLParse(new File("shop.xml"));
        File fileLoad = null;
        File fileSave = null;
        //Задание имени файла для загрузки
        switch (formatLoad) {
            case "json", "text" -> fileLoad = new File(fileNameLoad);
        }
        //Задание имени файла для сохранения
        switch (formatSave) {
            case "json", "text" -> fileSave = new File(fileNameSave);
        }
        //Задание имени файла для сохранения log
        File logFile = new File(fileNameLog);

        //Загрузка из файла на основании параметров
        Basket basket = null;
        if (enabledLoad && fileLoad != null && fileLoad.exists()) {
            switch (formatLoad) {
                case "text" -> basket = Basket.loadFromTxtFile(fileLoad);
                case "json" -> basket = Basket.loadFromJson(fileLoad);
            }
            if (basket != null) {
                basket.showGoods();
                basket.printCart();
            }
        } else {
            //Создание стандартного товаров и цен
            String[] products = {"Молоко", "Хлеб", "Гречневая крупа"};
            int[] prices = {50, 14, 80};
            basket = new Basket(products, prices, new int[products.length]);
            basket.showGoods();
        }
        Scanner scanner = new Scanner(System.in);

        ClientLog logObj = null;
        while (true) {
            //получение данных введённых пользователем
            System.out.println("Выберите товар и количество или введите 'end'");
            String inputString = scanner.nextLine();

            if (inputString.equals("end")) {
                //Вывод товаров корзины в консоль
                if (basket != null) basket.printCart();
                //Сохранение файла в зависимости от параметров
                if (enabledSave) {
                    switch (formatSave) {
                        case "text" -> {
                            assert basket != null;
                            basket.saveTxt(fileSave);
                        }
                        case "json" -> {
                            assert basket != null;
                            basket.saveJson(fileSave);
                        }
                    }
                }
                //Выгрузка log в файл в зависимости от параметров
                if (logObj != null && enabledLog) {
                    logObj.exportAsCSV(logFile);
                }
                scanner.close();
                break;
            } else {
                //Добавление товаров в корзину
                String[] parts = inputString.split(" ");
                assert basket != null;
                basket.addToCart(Integer.parseInt(parts[0]) - 1, Integer.parseInt(parts[1]));
                //Сохранение log в зависимости от параметров
                if (enabledLog) {
                    if (logObj == null) {
                        logObj = new ClientLog(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                    }
                    logObj.log(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                }
            }
        }
    }

    //Формирование параметров работы программы из XML файла
    private static void XMLParse(File XmlFile) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(XmlFile);
            System.out.println("XML parameters");
            //получение значений элемента load
            NodeList listLoad = doc.getElementsByTagName("load");
            for (int i = 0; i < listLoad.getLength(); i++) {
                Node node = listLoad.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    enabledLoad = Boolean.parseBoolean(element.getElementsByTagName("enabled").item(0).getTextContent());
                    fileNameLoad = element.getElementsByTagName("fileName").item(0).getTextContent();
                    formatLoad = element.getElementsByTagName("format").item(0).getTextContent();
                    System.out.println(node.getNodeName());
                    System.out.println("enabled : " + enabledLoad);
                    System.out.println("fileName : " + fileNameLoad);
                    System.out.println("format : " + formatLoad);
                }
            }
            //получение значений элемента save
            NodeList listSave = doc.getElementsByTagName("save");
            for (int i = 0; i < listSave.getLength(); i++) {
                Node node = listSave.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;
                    enabledSave = Boolean.parseBoolean(element.getElementsByTagName("enabled").item(0).getTextContent());
                    fileNameSave = element.getElementsByTagName("fileName").item(0).getTextContent();
                    formatSave = element.getElementsByTagName("format").item(0).getTextContent();
                    System.out.println(node.getNodeName());
                    System.out.println("enabled : " + enabledSave);
                    System.out.println("fileName : " + fileNameSave);
                    System.out.println("format : " + formatSave);
                }
            }
            //получение значений элемента log
            NodeList listLog = doc.getElementsByTagName("log");
            for (int i = 0; i < listLog.getLength(); i++) {
                Node node = listLog.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;
                    enabledLog = Boolean.parseBoolean(element.getElementsByTagName("enabled").item(0).getTextContent());
                    fileNameLog = element.getElementsByTagName("fileName").item(0).getTextContent();
                    System.out.println(node.getNodeName());
                    System.out.println("enabled : " + enabledLog);
                    System.out.println("fileName : " + fileNameLog);
                }
            }
            System.out.println("---------------------------------------");
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

    }
}