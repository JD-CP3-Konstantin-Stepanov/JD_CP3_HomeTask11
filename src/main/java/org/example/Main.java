package org.example;

import java.io.File;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        //File file = new File("..//Files/basket.txt");
        File serializeFile = new File("basket.json");
        File logFile = new File("log.csv");

        Basket basket;
        //if (file.exists()) {
        if (serializeFile.exists()) {
            //basket = Basket.loadFromTxtFile(file);
            basket = Basket.loadFromJson(serializeFile);
            basket.showGoods();
            basket.printCart();
        } else {
            String[] products = {"Молоко", "Хлеб", "Гречневая крупа"};
            int[] prices = {50, 14, 80};
            basket = new Basket(products, prices, new int[products.length]);
            basket.showGoods();
        }
        Scanner scanner = new Scanner(System.in);

        ClientLog logObj = null;
        while (true) {

            System.out.println("Выберите товар и количество или введите 'end'");
            String inputString = scanner.nextLine();

            if (inputString.equals("end")) {
                basket.printCart();
                //basket.saveTxt(file);
                basket.saveJson(serializeFile);
                if (logObj != null) {
                    logObj.exportAsCSV(logFile);
                }
                scanner.close();
                break;
            } else {
                String[] parts = inputString.split(" ");
                basket.addToCart(Integer.parseInt(parts[0]) - 1, Integer.parseInt(parts[1]));
                if (logObj == null) {
                    //logObj = new ClientLog(Integer.parseInt(parts[0]) - 1, Integer.parseInt(parts[1]));
                    logObj = new ClientLog(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                }
                logObj.log(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            }
        }
    }

}