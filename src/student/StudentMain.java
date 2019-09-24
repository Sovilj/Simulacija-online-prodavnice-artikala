package student;
import operations.*;
import student.*;
import sun.util.calendar.Gregorian;

import org.junit.Assert;
import org.junit.Test;

import tests.BuyerOperationsTest;
import tests.TestHandler;
import tests.TestRunner;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class StudentMain {

    public static void main(String[] args) {

    	 ss150222_ArticleOperations articleOperations = new ss150222_ArticleOperations() ; // Change this for your implementation
         ss150222_BuyerOperations buyerOperations = new ss150222_BuyerOperations() ;
         ss150222_CityOperations cityOperations = new ss150222_CityOperations();
         ss150222_GeneralOperations generalOperations = new ss150222_GeneralOperations();
         ss150222_OrderOperations orderOperations = new ss150222_OrderOperations();
         ss150222_ShopOperations shopOperations = new ss150222_ShopOperations();
         ss150222_TransactionOperations transactionOperations = new ss150222_TransactionOperations();
         
//        Calendar c = Calendar.getInstance();
//        c.clear();
//        c.set(2010, Calendar.JANUARY, 01);
//
//
//        Calendar c2 = Calendar.getInstance();
//        c2.clear();
//        c2.set(2010, Calendar.JANUARY, 01);
//
//        if(c.equals(c2)) System.out.println("jednako");
//        else System.out.println("nije jednako");

      TestHandler.createInstance(
                articleOperations,
                buyerOperations,
                cityOperations,
                generalOperations,
                orderOperations,
                shopOperations,
                transactionOperations
        );

        TestRunner.runTests();
  
         
       
       //  generalOperations.time(1);
  
    generalOperations.eraseAll();
      
     /*   int cityId = cityOperations.createCity("Kragujevac");
      Assert.assertNotEquals(-1, cityId);
   
      int buyerId = buyerOperations.createBuyer("Pera", cityId);
      Assert.assertNotEquals(-1, buyerId);
    
      System.out.println(cityId);
      System.out.println(buyerId);
      
      generalOperations.eraseAll();
      
      int cityId1 = cityOperations.createCity("Kragujevac");
      int cityId2 = cityOperations.createCity("Beograd");
       buyerId = buyerOperations.createBuyer("Lazar", cityId1);
      buyerOperations.setCity(buyerId, cityId2);

       cityId = buyerOperations.getCity(buyerId);
      Assert.assertEquals(cityId2, cityId);
      
      System.out.println(cityId2);
      System.out.println(cityId);
      
      generalOperations.eraseAll();
      
      cityId = cityOperations.createCity("Kragujevac");
      buyerId = buyerOperations.createBuyer("Pera", cityId);

      BigDecimal credit1 = new BigDecimal("1000");

      BigDecimal creditReturned = buyerOperations.increaseCredit(buyerId, credit1);
      Assert.assertEquals(credit1, creditReturned);
      
      System.out.println(credit1);
      System.out.println(creditReturned);
    
      BigDecimal credit2 = new BigDecimal("500");
      buyerOperations.increaseCredit(buyerId, credit2);

      creditReturned = buyerOperations.getCredit(buyerId);
      Assert.assertEquals(credit1.add(credit2), creditReturned);
      
      System.out.println(credit1.add(credit2));
      System.out.println(creditReturned);
      
      generalOperations.eraseAll();
      
      cityId = cityOperations.createCity("Kragujevac");
      buyerId = buyerOperations.createBuyer("Pera", cityId);

      int orderId1 = buyerOperations.createOrder(buyerId);
      int orderId2 = buyerOperations.createOrder(buyerId);
      Assert.assertNotEquals(-1, orderId1);
      Assert.assertNotEquals(-1, orderId2);
      
      System.out.println(orderId1);
      System.out.println(orderId2);
     
      List<Integer> orders = buyerOperations.getOrders(buyerId);
      Assert.assertEquals(2, orders.size());
      Assert.assertTrue(orders.contains(orderId1) && orders.contains(orderId2));
      
      System.out.println(orders.size());
      System.out.println(orders);
      
      */
      
 /*       Calendar initialTime = Calendar.getInstance();
        initialTime.clear();
        initialTime.set(2018, Calendar.JANUARY, 1);
        generalOperations.setInitialTime(initialTime);
        Calendar receivedTime = Calendar.getInstance();
        receivedTime.clear();
        receivedTime.set(2018, Calendar.JANUARY, 22);

        //make network
        int cityB = cityOperations.createCity("B");
        int cityC1 = cityOperations.createCity("C1");
        int cityA = cityOperations.createCity("A");
        int cityC2 = cityOperations.createCity("C2");
        int cityC3 = cityOperations.createCity("C3");
        int cityC4 = cityOperations.createCity("C4");
        int cityC5 = cityOperations.createCity("C5");

        cityOperations.connectCities(cityB, cityC1, 8);
        cityOperations.connectCities(cityC1, cityA, 10);
        cityOperations.connectCities(cityA, cityC2, 3);
        cityOperations.connectCities(cityC2, cityC3, 2);
        cityOperations.connectCities(cityC3, cityC4, 1);
        cityOperations.connectCities(cityC4, cityA, 3);
        cityOperations.connectCities(cityA, cityC5, 15);
        cityOperations.connectCities(cityC5, cityB, 2);

        //make shops, buyer and articles
        int shopA = shopOperations.createShop("shopA", "A");
        int shopC2 = shopOperations.createShop("shopC2", "C2");
        int shopC3 = shopOperations.createShop("shopC3", "C3");

        shopOperations.setDiscount(shopA, 20);
        shopOperations.setDiscount(shopC2, 50);

        int laptop = articleOperations.createArticle(shopA, "laptop", 1000);
        int monitor = articleOperations.createArticle(shopC2, "monitor", 200);
        int stolica = articleOperations.createArticle(shopC3, "stolica", 100);
        int sto = articleOperations.createArticle(shopC3, "sto", 200);

        shopOperations.increaseArticleCount(laptop, 10);
        shopOperations.increaseArticleCount(monitor, 10);
        shopOperations.increaseArticleCount(stolica, 10);
        shopOperations.increaseArticleCount(sto, 10);

        int buyer = buyerOperations.createBuyer("kupac", cityB);
        int order = buyerOperations.createOrder(buyer);

        orderOperations.addArticle(order, laptop, 5);
        orderOperations.addArticle(order, monitor, 4);
        orderOperations.addArticle(order, stolica, 10);
        orderOperations.addArticle(order, sto, 4);

        Assert.assertNull(orderOperations.getSentTime(order));
        System.out.println(orderOperations.getSentTime(order));
          
        Assert.assertTrue("created".equals(orderOperations.getState(order)));
        System.out.println("created".equals(orderOperations.getState(order)));
        
        orderOperations.completeOrder(order);
        
        Assert.assertTrue("sent".equals(orderOperations.getState(order)));
        
        System.out.println("sent".equals(orderOperations.getState(order)));
        
        int buyerTransactionId = transactionOperations.getTransationsForBuyer(buyer).get(0);
        Assert.assertEquals(initialTime, transactionOperations.getTimeOfExecution(buyerTransactionId));

        Assert.assertNull(transactionOperations.getTransationsForShop(shopA));     
         
        System.out.println(transactionOperations.getTransationsForShop(shopA));
        
        //calculate ammounts - begin
        BigDecimal shopAAmount = new BigDecimal("5").multiply(new BigDecimal("1000")).setScale(3);
        BigDecimal shopAAmountWithDiscount = new BigDecimal(0.8).multiply(shopAAmount).setScale(3,BigDecimal.ROUND_HALF_DOWN);
        BigDecimal shopC2Amount = new BigDecimal("4").multiply(new BigDecimal("200")).setScale(3);
        BigDecimal shopC2AmountWithDiscount = new BigDecimal(0.5).multiply(shopC2Amount).setScale(3);
        BigDecimal shopC3Amount = (new BigDecimal("10").multiply(new BigDecimal("100")))
                .add(new BigDecimal("4").multiply(new BigDecimal("200"))).setScale(3);
        BigDecimal shopC3AmountWithDiscount = shopC3Amount;

        BigDecimal amountWithoutDiscounts = shopAAmount.add(shopC2Amount).add(shopC3Amount).setScale(3);
        BigDecimal amountWithDiscounts = shopAAmountWithDiscount.add(shopC2AmountWithDiscount).add(shopC3AmountWithDiscount).setScale(3);

        BigDecimal systemProfit = amountWithDiscounts.multiply(new BigDecimal("0.05")).setScale(3);;
        BigDecimal shopAAmountReal = shopAAmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);;
        BigDecimal shopC2AmountReal = shopC2AmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);;
        BigDecimal shopC3AmountReal = shopC3AmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);;
        //calculate ammounts - end

        
        System.out.println();
        System.out.println();
        System.out.println();
        
      
        
          System.out.println(amountWithDiscounts);
        System.out.println(orderOperations.getFinalPrice(order));
        Assert.assertEquals(amountWithDiscounts, orderOperations.getFinalPrice(order));
       
        
        
        Assert.assertEquals(amountWithoutDiscounts.subtract(amountWithDiscounts),  orderOperations.getDiscountSum(order));
        System.out.println(amountWithoutDiscounts.subtract(amountWithDiscounts));
        System.out.println(orderOperations.getDiscountSum(order));
        
        
        Assert.assertEquals(amountWithDiscounts, transactionOperations.getBuyerTransactionsAmmount(buyer));
        System.out.println(amountWithDiscounts);
        System.out.println(transactionOperations.getBuyerTransactionsAmmount(buyer));
        
        Assert.assertNull(transactionOperations.getShopTransactionsAmmount(shopA));
        System.out.println(transactionOperations.getShopTransactionsAmmount(shopA));
        
        Assert.assertNull(transactionOperations.getShopTransactionsAmmount(shopC2));
        System.out.println(transactionOperations.getShopTransactionsAmmount(shopC2));
        
        Assert.assertNull(transactionOperations.getShopTransactionsAmmount(shopC3));
        System.out.println(transactionOperations.getShopTransactionsAmmount(shopC3));
        
        Assert.assertEquals(new BigDecimal("0"), transactionOperations.getSystemProfit());
        System.out.println(new BigDecimal("0"));
        System.out.println(transactionOperations.getSystemProfit());
       
        
      generalOperations.time(2);
        Assert.assertEquals(initialTime, orderOperations.getSentTime(order));
        System.out.println(initialTime);
        System.out.println(orderOperations.getSentTime(order));
        
        
        Assert.assertNull(orderOperations.getRecievedTime(order));
        System.out.println(orderOperations.getRecievedTime(order));
        
        Assert.assertEquals(orderOperations.getLocation(order), cityA);
        System.out.println(orderOperations.getLocation(order));
        System.out.println(cityA);
        

         generalOperations.time(9);
        Assert.assertEquals(orderOperations.getLocation(order), cityA);
        System.out.println(orderOperations.getLocation(order));
        System.out.println(cityA);
        
        
        generalOperations.time(7);
        System.out.println(orderOperations.getLocation(order));
        System.out.println(cityC5);
        Assert.assertEquals(orderOperations.getLocation(order), cityC5);
      

        generalOperations.time(5);
        Assert.assertEquals(orderOperations.getLocation(order), cityB);
        System.out.println(orderOperations.getLocation(order));
        System.out.println(cityB);
        
        System.out.println(receivedTime.getTimeInMillis());
        System.out.println(orderOperations.getRecievedTime(order).getTimeInMillis());
        Assert.assertEquals(receivedTime, orderOperations.getRecievedTime(order));
       
        
        Assert.assertEquals(shopAAmountReal, transactionOperations.getShopTransactionsAmmount(shopA));
        System.out.println(shopAAmountReal);
        System.out.println(transactionOperations.getShopTransactionsAmmount(shopA));
        
        Assert.assertEquals(shopC2AmountReal, transactionOperations.getShopTransactionsAmmount(shopC2));
        System.out.println(shopC2AmountReal);
        System.out.println(transactionOperations.getShopTransactionsAmmount(shopC2));
        
        Assert.assertEquals(shopC3AmountReal, transactionOperations.getShopTransactionsAmmount(shopC3));
        System.out.println(shopC3AmountReal);
        System.out.println(transactionOperations.getShopTransactionsAmmount(shopC3));
        
        Assert.assertEquals(systemProfit, transactionOperations.getSystemProfit());
        
        System.out.println(systemProfit);
        System.out.println(transactionOperations.getSystemProfit());

        int shopATransactionId = transactionOperations.getTransactionForShopAndOrder(order, shopA);
        Assert.assertNotEquals(-1, shopATransactionId);
        System.out.println(-1);
        System.out.println(shopATransactionId);
        
        Assert.assertEquals(receivedTime, transactionOperations.getTimeOfExecution(shopATransactionId));
        System.out.println(receivedTime);
        System.out.println(transactionOperations.getTimeOfExecution(shopATransactionId));
        
        
       
        */
      
         
      
    
    
    
    
    }
}
