import java.io.*;
import java.math.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;

import javax.swing.plaf.synth.SynthStyle;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

class Product {
    protected String name;
    protected int number;
    protected double precio;
    protected boolean importado;

    public Product(String name, double precio){
        this.name = name;
        this.precio = precio;
        importado = false;
    }

    public Product(String name, double precio, boolean importado){
        this.name = name;
        this.precio = precio;
        this.importado = importado;
    }

    public double getTaxes(){
        double impuesto = precio * 0.1f;
        if (importado){
            impuesto += precio * 0.05f;
        }

        impuesto = Math.round(impuesto * 100.0) / 100.0;
        return impuesto;
    }

    public double getTotalPrice(){
        return precio + getTaxes();
    }
}

class SpecialProduct extends Product{
    public SpecialProduct(String name, double precio) {
        super(name, precio);
    }

    public SpecialProduct(String name, double precio, boolean importado) {
        super(name, precio, importado);
    }

    public double getTaxes(){
        if (!importado)
            return 0.0;

        double impuesto = precio * 0.05f;
        impuesto = Math.round(impuesto * 100.0) / 100.0;
        return impuesto;
    }

    public double getTotalPrice(){
        return precio + getTaxes();
    }
}

class Ticket{
    private Map<Product, Integer> productList;

    public Ticket(){
        productList = new LinkedHashMap<Product, Integer>();
    }

    public void addProduct(Product product){
        productList.put(product, productList.getOrDefault(product, 0) + 1);
    }

    public void addProduct(Product product, Integer n){
        productList.put(product, productList.getOrDefault(product, 0) + n);
    }

    public String processTicket(){
        String ticketProcessed = "";
        float totalTaxes = 0;
        float total = 0;
        for (Product product: productList.keySet()){
            int count = productList.get(product);
            totalTaxes += product.getTaxes()  * count;
            total += product.getTotalPrice()  * count;
            ticketProcessed += count + product.name + ": " + String.format("%.2f", product.getTotalPrice() * count) + " €\n";
        }

        ticketProcessed += "Impuestos sobre las ventas: " + String.format("%.2f", totalTaxes) + " €\n";
        ticketProcessed += "Total: " + String.format("%.2f", total) + " €\n";

        return ticketProcessed;
    }
}

class LineSolver {
    private static boolean isProductABook(String productName){
        String[] bookIdentificators = new String[]{"libro"};

        for (String identificator: bookIdentificators){
            if (productName.contains(identificator)){
                return true;
            }
        }
        return false;
    }

    private static boolean isProductFood(String productName){
        String foodIdentificators[] = new String[]{"bombones", "chocolate"};

        for (String identificator: foodIdentificators){
            if (productName.contains(identificator)){
                return true;
            }
        }
        return false;
    }

    private static boolean isProductMedical(String productName){
        String medicalProductIdentificator[] = new String[]{"pastillas"};

        for (String identificator: medicalProductIdentificator){
            if (productName.contains(identificator)){
                return true;
            }
        }
        return false;
    }

    public static void addProductToTicket(String line, Ticket ticket) {
        int amountOfProducts = Character.getNumericValue(line.charAt(0));

        String stringParts[] = line.split(" a ");
        float price = Float.parseFloat(stringParts[1].split(" ")[0].replace(',', '.'));
        String productName = stringParts[0].substring(1);

        boolean isImported = productName.contains("importado") || productName.contains("importada") || productName.contains("importados") || productName.contains("importadas"); 

        if (isProductABook(productName) || isProductFood(productName) || isProductMedical(productName)){
            ticket.addProduct(new SpecialProduct(productName, price, isImported), amountOfProducts);
        }
        else{
            ticket.addProduct(new Product(productName, price, isImported), amountOfProducts);
        }
    }

    public static String fixAccentMarks(String string){
        string = string.replace("Ã¡", "á");
        string = string.replace("Ã©", "é");
        string = string.replace("Ã­", "í");
        string = string.replace("Ã³", "ó");
        string = string.replace("Ãº", "ú");

        string = string.replace("Ã", "Á");
        string = string.replace("Ã‰", "É");
        string = string.replace("Ã", "Í");
        string = string.replace("Ó", "Ó");
        string = string.replace("Ãš", "Ú");
        return string;
    }
}

public class Solution {
    public static void main(String[] args) throws IOException {
        Ticket ticket = new Ticket();

        BufferedReader bufferedReader = new BufferedReader(new FileReader("input.txt"));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("output.txt", StandardCharsets.UTF_8));
        
        try {
            String line = bufferedReader.readLine();
            while (line != null){
                System.out.println(line);
                LineSolver.addProductToTicket(line, ticket);
                line = bufferedReader.readLine();
            }

            String solution = ticket.processTicket();
            solution = LineSolver.fixAccentMarks(solution);
            bufferedWriter.write(solution);
            
        } catch (IOException ex) {
                throw new RuntimeException(ex);
        }

        bufferedReader.close();
        bufferedWriter.close();
    }
}
