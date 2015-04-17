# RateSDSUProfessorsAndroid
Android app to rate professors at SDSU
Android app to rate professors at SDSU

This apps root was renamed from Assignment3a to RateSDSUProfessorsAndroid for clairty on github. 
Assignment was made in Android studio for school. The app may not work if the professor closes his database.

Known errors: 
1.There is an error with leaking network connection, even though I close the httpclient in onPostExecute and onPause.
The leaks do not crash my program when tested on the emulator but show up in the log files. I might redo the network connections with volley in the future to fix this.  
2.SQLLite data is off by one when offline and I have only written it for the professors view.
3.When I test on the emulator it does not become online until I press back and re-open the app through the Android emulator menus. Tested on emulator and Galaxy Note 10.1. The app looks better on a mobile device because of large buttons on tablet. I might remake the app with a check for a tablet and make certain buttons smaller.
