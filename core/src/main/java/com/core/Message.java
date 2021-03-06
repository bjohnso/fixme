package com.core;

import java.net.Socket;

public class Message {

    protected String message;
    protected String type;
    protected String id;
    protected int senderID = -1;
    protected int recipientID = -1;
    protected String status;
    protected long timeStamp;
    protected int fragments = 1;
    protected String checksum;

    public Message(String fixMessage){
        this.timeStamp = System.currentTimeMillis();
        this.id = timeStamp +""+ senderID;
        parseFix(fixMessage);
        this.message = fixMessage;
    }

    public Message(int senderID, int recipientID, String type){
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.type = type;
        this.timeStamp = System.currentTimeMillis();
        this.id = timeStamp +""+ senderID;
        this.setMessage(this.toFix());
    }

    public Message(){ 

    }

    public void setFragments(int fragments) {
        this.fragments = fragments;
    }

    public int getFragments() {
        return fragments;
    }

    public void resetID(){
        this.timeStamp = System.currentTimeMillis();
        this.id = timeStamp +""+ senderID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void returnToSender(String responseType){
        int temp = this.senderID;
        this.senderID = this.recipientID;
        this.recipientID = temp;
        this.type = responseType;
        resetID();
    }

    public void parseFix(String fixMessage){
        String soh = "" + (char)1;
        String split[] = fixMessage.split(soh);
        for (int i = 0 ; i < split.length; i++){
            String tag[] = split[i].split("=");
            if (tag.length > 1) {
                switch (tag[0]) {
                    //MESSAGE TYPE
                    case "35":
                        if (tag[1].equalsIgnoreCase("A")){
                            type = "A";
                        } else if (tag[1].equalsIgnoreCase("0")){
                            type = "0";
                        }
                        else if (tag[1].equalsIgnoreCase("D")){
                            type = "D";
                        }
                        else if (tag[1].equalsIgnoreCase("W")){
                            type = "W";
                        }
                        else if (tag[1].equalsIgnoreCase("V")){
                            type = "V";
                        }
                        else if (tag[1].equalsIgnoreCase("8")){
                            type = "8";
                        }
                        else if (tag[1].equalsIgnoreCase("j")){
                            type = "j";
                        }
                        else if (tag[1].equalsIgnoreCase("3")){
                            type = "3";
                        }
                        else{
                            type = "9";
                        }
                        break;
                    //RECIPIENT
                    case "56":
                        try {
                            recipientID = Integer.parseInt(tag[1]);
                        } catch (NumberFormatException e){
                            System.out.println("FIX ERROR RECIPIENT");
                        }
                        break;
                    //SENDER
                    case "115":
                        try {
                            senderID = Integer.parseInt(tag[1]);
                        } catch (NumberFormatException e){
                            System.out.println("FIX ERROR SENDER");
                        }
                        break;
                    //CHECKSUM
                    case "10":
                        checksum = tag[1];
                        break;
                        //STATUS
                    /*case "39":
                        if (tag[1].equalsIgnoreCase("1")){
                            done = false;
                        } else {
                            done = true;
                        }
                        break ;*/
                        //ID
                    case "66":
                        id = tag[1];
                        break;
                        //FRAGMENTS
                    case "68":
                        fragments = Integer.parseInt(tag[1]);
                        break;
                }
            }
            else {
                this.message = tag[0];
            }
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRecipientID() {
        return recipientID;
    }

    public int getSenderID() {
        return senderID;
    }

    public String getStatus() {
        return this.status;
    }

    public void setRecipientID(int recipient) {
        this.recipientID = recipient;
    }

    public void setSenderID(int sender) {
        this.senderID = sender;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }

    public String toFix(){
        String soh = "" + (char)1;
        String toReturn = "";
        int i = 0;

        if (type != null){
            toReturn += "35="+type;
            i++;
        }

        if (senderID != -1){
            if (i == 1)
                toReturn += soh;
            toReturn += "115="+senderID;
        }

        if (recipientID != -1){
            if (i > 0)
                toReturn += soh;
            toReturn += "56="+recipientID;
        }

        if (checksum != null){
            if (i > 0)
                toReturn += soh;
            toReturn += "10="+checksum;
        }

        if (id != null){
            if (i > 0)
                toReturn += soh;
            toReturn += "66="+id;
        }

        if (fragments != -1){
            if (i > 0)
                toReturn += soh;
            toReturn += "68="+fragments;
        }
        return toReturn;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getChecksum() {
        return checksum;
    }

    public String generateChecksum(){
        String soh = "" + (char)1;
        String arr[] = toFix().split(soh);
        long checksum = 0;
        for(int i = 0; i < arr.length; i++){
            String tag[] = arr[i].split("=");
            for (int j = 0; j < arr[i].length(); j++){
                if (!tag[0].equalsIgnoreCase("10"))
                    checksum += (long) arr[i].charAt(j);
                else
                    break;
            }
        }
        checksum %= 256;

        if (checksum >= 100) {
            this.checksum =  "" + checksum;
        }
        else if (checksum >= 10) {
            this.checksum = "0" + checksum;
        }
        else {
            this.checksum = "00" + checksum;
        }
        return this.checksum;
    }

    public boolean validateChecksum(){
        if (checksum != null){
            String soh = "" + (char)1;
            String arr[] = toFix().split(soh);
            long checksum = 0;
            for(int i = 0; i < arr.length; i++){
                String tag[] = arr[i].split("=");
                for (int j = 0; j < arr[i].length(); j++){
                    if (!tag[0].equalsIgnoreCase("10"))
                        checksum += (long) arr[i].charAt(j);
                    else
                        break;
                }
            }
            checksum %= 256;

            String compare = "";
            if (checksum >= 100) {
                compare =  "" + checksum;
            }
            else if (checksum >= 10) {
                compare = "0" + checksum;
            }
            else {
                compare = "00" + checksum;
            }
            if (compare.equalsIgnoreCase(this.checksum))
                return true;
        }
        return false;
    }
}
