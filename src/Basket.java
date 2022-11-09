import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Basket implements Serializable {

    protected String[] productName;
    protected int[] productPrice;
    protected int[] basket;

    public Basket(String[] productName, int[] productPrice, int[] basket) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.basket = basket;
    }

    public void addToCart(int productNumber, int productCount) {
        this.basket[productNumber] = this.basket[productNumber] + productCount;
    }

    public void printCart() {
        System.out.println("Ваша корзина:");
        int sumProducts = 0;
        for (int i = 0; i < this.basket.length; i++) {
            if (this.basket[i] != 0) {
                System.out.println(this.productName[i] + " " +
                        this.basket[i] + " шт " + this.productPrice[i] + " руб/шт "
                        + (this.productPrice[i] * this.basket[i]) + " руб в сумме");
                sumProducts += (this.productPrice[i] * this.basket[i]);
            }
        }
        System.out.println("Итого: " + sumProducts + " руб");
    }


    public void saveTxt(File textFile) {
        try (PrintWriter out = new PrintWriter(textFile)) {
            out.print(strReplace(Arrays.toString(this.productName)) + ";"
                    + strReplace(Arrays.toString(this.productPrice)) + ";"
                    + strReplace(Arrays.toString(this.basket)));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String strReplace(String inputString) {
        return inputString.replace("[", "").replace("]", "");
    }

    static Basket loadFromTxtFile(File textFile) {
        //Заполнение коллекций из файлов
        ArrayList<String> productName = new ArrayList<>();
        ArrayList<Integer> productPrice = new ArrayList<>();
        ArrayList<Integer> basket = new ArrayList<>();
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

    public void showGoods() {
        System.out.println("Список возможных товаров для покупки");
        for (int i = 0; i < this.productName.length; i++) {
            System.out.println((i + 1) + ". " + productName[i] + " " + this.productPrice[i] + " руб/шт");
        }
        System.out.println();
    }

    @Override
    public String toString() {
        return (Arrays.toString(this.productName) + "\n" + Arrays.toString(this.productPrice));
    }

}
