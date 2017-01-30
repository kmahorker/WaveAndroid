package com.thewavesocial.waveandroid.BusinessObjects;

//Nothing changed
public final class CurrentUser
{
    private static User theUser = new User(); //replace with getCurrentUser from databaseAccess class

    //string n = CurrentUser.theUser.getFirstName();

    public static User getTheUser()
    {
        return theUser;
    }
    public static void setTheUser(User theUser)
    {
        CurrentUser.theUser = theUser;
    }
}
