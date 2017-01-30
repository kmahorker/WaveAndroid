package com.thewavesocial.waveandroid.BusinessObjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Things I did during file translation:
 * 1. Changed List<Int64> to ArrayList<Long> (see line 37)
 * 2. Changed Int64 to long
 * 3. Changed DateTime to Date
 * - Wei Tung
 */
public class Party
{
    private long partyID;
    private String name;
    private double price; //decimal == double?
    private String hostName;
    private Date startingDateTime;
    private Date endingDateTime;
    private String address;
    private List<Long> attendingUsers;
    private boolean isPublic;

    public Party()
    {
        partyID = 0;
        name = "";
        price = 0;
        hostName = "";
        startingDateTime = new Date();
        endingDateTime = new Date();
        address = "";
        attendingUsers = new ArrayList<Long>();
        isPublic = false;
    }

    public Party(
            //long partyID,
            String name,
            double price,
            String hostName,
            Date startingDateTime,
            Date endingDateTime,
            String address,
            List<Long> attendingUsers,
            boolean isPublic)
    {
        //this.partyID = partyID;
        this.name = name;
        this.price = price;
        this.hostName = hostName;
        this.startingDateTime = startingDateTime;
        this.endingDateTime = endingDateTime;
        this.address = address;
        this.attendingUsers = attendingUsers;
        this.isPublic = isPublic;
    }

    //Delete
    public boolean removeAttending(long userIDToRemove)
    {
        return attendingUsers.remove(userIDToRemove);
    }

    //Add
    public void addAttending(long userIDToAdd)
    {
        attendingUsers.add(userIDToAdd);
    }

    //Setters
    public void setPartyID(long partyID)
    {
        this.partyID = partyID;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }

    public void setStartingDateTime(Date dateTimeObj)
    {
        this.startingDateTime = dateTimeObj;
    }

    public void setEndingDateTime(Date dateTimeObj)
    {
        this.endingDateTime = dateTimeObj;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public void setAttendingUsers(List<Long> attendingUsers)
    {
        this.attendingUsers = attendingUsers;
    }

    public void setIsPublic(boolean isPublic)
    {
        this.isPublic = isPublic;
    }

    //Getters
    public long getPartyID()
    {
        return partyID;
    }

    public String getName()
    {
        return name;
    }

    public double getPrice()
    {
        return price;
    }

    public String getHostName()
    {
        return hostName;
    }

    public Date getStartingDateTime()
    {
        return startingDateTime;
    }

    public Date getEndingDateTime()
    {
        return endingDateTime;
    }

    public String getAddress()
    {
        return address;
    }

    public List<Long> getAttendingUsers()
    {
        return attendingUsers;
    }

    public boolean getIsPublic()
    {
        return isPublic;
    }

    @Override
    public String toString()
    {
        return name;
    }

}
