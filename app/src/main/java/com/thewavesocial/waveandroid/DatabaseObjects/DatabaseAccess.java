package com.thewavesocial.waveandroid.DatabaseObjects;

import android.content.pm.PackageManager;
import android.database.DatabaseUtils;
import android.provider.ContactsContract;

/*
 * Things I did during file translation:
 * 1. NotImplementedException => NullPointerException
 * 2. Commented out DatabaseInterface implementation
 * 3. Changed Int64 to long
 * - Wei Tung
 */
import com.thewavesocial.waveandroid.BusinessObjects.*;

public class DatabaseAccess //implements DatabaseInterface
{
    public DatabaseAccess()
    {
    }

    public long createParty(Party party)
    {
        throw new NullPointerException();
    }

    public long createUser(User newUser)
    {
        throw new NullPointerException();
    }

    public boolean deleteParty(long partyID)
    {
        throw new NullPointerException();
    }

    public boolean deleteUser(long userID)
    {
        throw new NullPointerException();
    }

    public Party getParty(long partyID)
    {
        throw new NullPointerException();
    }

    public User getUser(long userID)
    {
        //Temporary for testing
        return CurrentUser.getTheUser();
    }

    public boolean updateParty(long partyID, String param)
    {
        throw new NullPointerException();
    }

    public boolean updateUser(long userID, String param)
    {
        throw new NullPointerException();
    }
}
