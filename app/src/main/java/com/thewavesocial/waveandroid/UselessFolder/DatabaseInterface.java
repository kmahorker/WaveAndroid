//package com.thewavesocial.waveandroid.DatabaseObjects;
//
//import com.thewavesocial.waveandroid.BusinessObjects.*;
//
///*
// * Things I did during file translation:
// * 1. Changed Int64 to long
// * - Wei Tung
// */
//public interface DatabaseInterface
//{
//    //Add User with info to the database, as well as their password
//    //returns UserID (allocated by database)
//    long createUser(User newUser); //need to pass in password somehow protected
//
//    //return User object based on a userID
//    User getUser(long userID);
//
//    //Update User with given ID and specified parameter
//    //i.e. updateUser(123, "email");
//    //--> Means the email has changed in the CurrentUser object and should be reflected in database
//    //return true if succesfully updated
//    boolean updateUser(long userID, String param);
//
//    //Delete the User with given ID from database
//    //return true if deletion succesful
//    boolean deleteUser(long userID);
//
//    //Add Party with info Party Table of database
//    //returns partyID (allocated by database)
//    long createParty(Party party);
//
//    //return Party object based on partyID
//    Party getParty(long partyID);
//
//    //return true if update Successful
//    //Similar use to updateUser
//    boolean updateParty(long partyID, String param);
//
//    //return true if deletion successful
//    //Deletes Party with given partyID
//    boolean deleteParty(long partyID);
//}