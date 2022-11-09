import java.io.File;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        File file = new File("..//JD_CP3_HomeTask11/basket.txt");
        Basket basket;
        if (file.exists()) {
            basket = Basket.loadFromTxtFile(file);
            basket.showGoods();
            basket.printCart();
        } else {
            String[] products = {"Молоко", "Хлеб", "Гречневая крупа"};
            int[] prices = {50, 14, 80};
            basket = new Basket(products, prices, new int[products.length]);
            basket.showGoods();
        }
        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println("Выберите товар и количество или введите 'end'");
            String inputString = scanner.nextLine();

            if (inputString.equals("end")) {
                basket.printCart();
                basket.saveTxt(file);
                scanner.close();
                break;
            } else {
                String[] parts = inputString.split(" ");
                basket.addToCart(Integer.parseInt(parts[0]) - 1, Integer.parseInt(parts[1]));
            }
        }
    }
}