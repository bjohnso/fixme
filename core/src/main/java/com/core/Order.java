package com.core;

import java.io.IOException;
import java.net.Socket;

public class Order extends Message{

    private String stock;
    private int quantity = -1;
    private boolean buy; 
    private double bid = -1;

    public Order(String fixMessage){
        super(fixMessage);
        parseFix(fixMessage);

    }

    public Order(String type, int senderID, int recipientID, String stock, double bid, int quantity, boolean buy){
        this.type = type;
        this.senderID = senderID; 
        this.recipientID = recipientID;
        this.stock = stock; 
        this.bid = bid;
        this.quantity = quantity;
        this.buy = buy;
        this.timeStamp = System.currentTimeMillis();
        this.id = timeStamp +""+ senderID;
        this.message = toFix();
    }

    public Order(Portfolio portfolio){
        super();
    }

    @Override
    public void parseFix(String fixMessage) {
        super.parseFix(fixMessage);
        String soh = "" + (char)1;
        String split[] = fixMessage.split(soh);
        for (int i = 0 ; i < split.length; i++){
            String tag[] = split[i].split("=");
            if (tag[1] != null) {
                switch (tag[0]) {
                    //BUY OR SELL
                    case "54":
                        if (tag[1].equalsIgnoreCase("1"))
                            buy = true;
                        else
                            buy = false;
                        break;

                    //STOCK
                    case "55":
                            stock = tag[1];
                        break;

                    //QUANTITY
                    case "38":
                        try {
                            quantity = Integer.parseInt(tag[1]);
                        } catch (NumberFormatException e){
                            System.out.println("FIX ERROR HOLD");
                        }
                        break;

                    //PRICE
                    case "44":
                        try {
                            bid = Double.parseDouble(tag[1]);
                        } catch (NumberFormatException e){
                            System.out.println("FIX ERROR BID");
                        }
                        break;

                    //ORDER COMPLETION
                    case "39":
                        status = tag[1];
                        break;
                }
            }
        }
    }

   

    public String getStock() {
        return stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getBid() {
        return bid;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public boolean isBuy() {
        return buy;
    }

    public void setBuy(boolean buy) {
        this.buy = buy;
    }



    public boolean isReady(){
        return this.type != null && this.recipientID != -1 && this.senderID != -1 && stock != null && this.quantity != -1 && this.bid != -1;
    }

    @Override
    public String toFix() {
        String soh = "" + (char)1;
        String toReturn = "";
        int i = 0;

        if (stock != null){
            toReturn += "55="+stock;
            i++;
        }

        if (quantity != -1){
            if (i == 1)
                toReturn += soh;
            toReturn += "38="+quantity;
        }

        if (bid != -1){
            if (i > 0)
                toReturn += soh;
            toReturn += "44="+bid;
        }

        if (buy)
            return super.toFix() + soh + toReturn + soh + "54=1";
        else
            return super.toFix() + soh + toReturn + soh + "54=2";
    }
}
