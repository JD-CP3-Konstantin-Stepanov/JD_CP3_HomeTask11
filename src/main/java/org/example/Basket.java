package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Basket implements Serializable {
    protected String[] productName;
    protected int[] productPrice;
    protected int[] basket;

    //Конструктор класса Basket
    public Basket(String[] productName, int[] productPrice, int[] basket) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.basket = basket;
    }

    //Добавление товара в корзину
    public void addToCart(int productNumber, int productCount) {
        this.basket[productNumber] = this.basket[productNumber] + productCount;
    }

    //Вывод корзины введённой пользователем
    public void printCart() {
        System.out.println("Ваша корзина:");
        int sumProducts = 0;
        for (int i = 0; i < this.basket.length; i++) {
            if (this.basket[i] != 0) {
                System.out.println(this.productName[i] + " "
                        + this.basket[i] + " шт " + this.productPrice[i] + " руб/шт "
                        + (this.productPrice[i] * this.basket[i]) + " руб в сумме");
                sumProducts += (this.productPrice[i] * this.basket[i]);
            }
        }
        System.out.println("Итого: " + sumProducts + " руб");
    }

    //Сохранение данных объекта класса в текстовый файл
    public void saveTxt(File textFile) {
        try (PrintWriter out = new PrintWriter(textFile)) {
            out.print(strReplace(Arrays.toString(this.productName)) + ";"
                    + strReplace(Arrays.toString(this.productPrice)) + ";"
                    + strReplace(Arrays.toString(this.basket)));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //Замена символов в строке
    private String strReplace(String inputString) {
        return inputString.replace("[", "").replace("]", "");
    }

    //Загрузка значение из текстового файла и создание объекта класса Basket
    static Basket loadFromTxtFile(File textFile) {
        //Заполнение коллекций из файлов
        List<String> productName = new ArrayList<>();
        List<Integer> productPrice = new ArrayList<>();
        List<Integer> basket = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new FileReader(textFile))) {
            String read;
            while ((read = in.readLine()) != null) {

                String[] splitArray = read.split(";");
                int fill = 0;
                for (String value : splitArray) {
                    String[] splitWord = value.split(",");
                    fill++;
                    for (String s : splitWord) {
                        switch (fill) {
                            case 1 -> productName.add(s.trim());
                            case 2 -> productPrice.add(Integer.parseInt(s.trim()));
                            case 3 -> basket.add(Integer.parseInt(s.trim()));
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        //Перевод значений коллекций в массивы для конструктора
        String[] productNameParam = productName.toArray(new String[0]);
        int[] productPriceParam = productPrice.stream()
                .mapToInt(Integer::intValue)
                .toArray();
        int[] basketParam = basket.stream()
                .mapToInt(Integer::intValue)
                .toArray();

        return new Basket(productNameParam, productPriceParam, basketParam);
    }

    //Вывод товаров в консоль
    public void showGoods() {
        System.out.println("Список возможных товаров для покупки");
        for (int i = 0; i < this.productName.length; i++) {
            System.out.println((i + 1) + ". " + productName[i] + " " + this.productPrice[i] + " руб/шт");
        }
        System.out.println();
    }

    //Отображение данных класса Basket
    @Override
    public String toString() {
        return (Arrays.toString(this.productName) + "\n" + Arrays.toString(this.productPrice));
    }

    //Сериализация через Gson
    public void saveGson(File serializeFile) {
        Basket basket = new Basket(this.productName, this.productPrice, this.basket);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        gson.toJson(basket);

        try (FileWriter file = new FileWriter(serializeFile)) {
            file.write(gson.toJson(basket));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Сериализация через Json
    public void saveJson(File serializeFile) {

        JSONObject basketJson = new JSONObject();

        JSONArray productNameArr = new JSONArray();
        Collections.addAll(productNameArr, productName);
        basketJson.put("productName", productNameArr);

        JSONArray productPriceArr = new JSONArray();
        for (int price : productPrice) {
            productPriceArr.add(price);
        }
        basketJson.put("productPrice", productPriceArr);

        JSONArray basketArr = new JSONArray();
        for (int basket : this.basket) {
            basketArr.add(basket);
        }
        basketJson.put("basket", basketArr);

        try (FileWriter file = new FileWriter(serializeFile)) {
            file.write(basketJson.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Получение данных из json и создание объекта класса Basket
    public static Basket loadFromJson(File serializeFile) {

        JSONParser parser = new JSONParser();

        Basket basket = null;
        try {
            Object obj = parser.parse(new FileReader(serializeFile));
            JSONObject basketParsedJson = (JSONObject) obj;

            JSONArray productNameJson = (JSONArray) basketParsedJson.get("productName");
            JSONArray productPriceJson = (JSONArray) basketParsedJson.get("productPrice");
            JSONArray basketJson = (JSONArray) basketParsedJson.get("basket");

            String[] arrProductName = strArrReplace(productNameJson.toString(), true);

            String[] arrProductPrice = strArrReplace(productPriceJson.toString(), false);
            int[] productPriceInt = new int[arrProductPrice.length];
            for (int i = 0; i < arrProductPrice.length; i++) {
                productPriceInt[i] = Integer.parseInt(arrProductPrice[i]);
            }

            String[] arrBasket = strArrReplace(basketJson.toString(), false);
            int[] basketInt = new int[arrBasket.length];
            for (int i = 0; i < arrBasket.length; i++) {
                basketInt[i] = Integer.parseInt(arrBasket[i]);
            }

            basket = new Basket(arrProductName, productPriceInt, basketInt);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            throw new RuntimeException(e);
        }
        return basket;
    }

    //Формирование текстового массива из строки
    private static String[] strArrReplace(String inputString, Boolean regime) {
        String replace = inputString
                .replace("[", "")
                .replace("]", "")
                .replace(",", ", ");
        if (regime) {
            return replace
                    .replace("\"", "")
                    .split(", ");
        } else {
            return replace.split(", ");
        }
    }
}
