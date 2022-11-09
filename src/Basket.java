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


    public void saveBin(File serializeFile) {
        Basket basket = new Basket(this.productName, this.productPrice, this.basket);
        try (FileOutputStream fos = new FileOutputStream(serializeFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(basket);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static Basket loadFromBinFile(File serializeFile) {
        Basket basket = null;
        try (FileInputStream fis = new FileInputStream(serializeFile);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            basket = (Basket) ois.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return basket;
    }

}
