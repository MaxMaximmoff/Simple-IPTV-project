package com.meMaxMaximmoff.executabe;

public class RandomString {
	
	// method for generating a random string of length n
    static String getRandomString(int n)

    {
    	// select a random character from this string
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    + "0123456789"
                                    + "abcdefghijklmnopqrstuvxyz";

     // creating a StringBuffer with the size AlphaNumericString

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
        	// generate a random number between
        	// 0 variable length AlphaNumericString
            int index = (int)(AlphaNumericString.length()

                        * Math.random());

         // add the character one by one to the end of sb

            sb.append(AlphaNumericString.charAt(index));

        }

        return sb.toString();

    }
 // method for generating a random string of length n containing only digits
    static String getRandomIntString(int n)

    {
    	// select a random character from this string
        String AlphaNumericString = "0123456789";

     // creating a StringBuffer with the size AlphaNumericString

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
        	// generate a random number between
        	// 0 variable length AlphaNumericString
            int index = (int)(AlphaNumericString.length()

                        * Math.random());

         // add the character one by one to the end of sb

            sb.append(AlphaNumericString.charAt(index));

        }

        return sb.toString();

    }

}
