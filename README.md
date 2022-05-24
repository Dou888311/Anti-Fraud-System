# Anti-Fraud-System
Anti-Fraud system made with SpringBoot Data, Web, Security.

There is 4 roles in application: Anonymous(non-authorized), ADMINISTRATOR, SUPPORT, MERCHANT.

 ![Roles](https://user-images.githubusercontent.com/91428346/170064671-7df74350-d301-4b15-9c7b-96238afb1a63.png)

App have repository for Stolen Cards, SuspiciousIP, User, Transaction.

Transaction Post in Json via: 
{ 

  "amount": 210,
  
  "ip": "192.168.1.1",
  
  "number": "4000008449433403",
  
  "region": "EAP",
  
  "date": "2022-01-22T16:04:00"
  
}

Custom HttpResponses reailization. Transactions can be one of 3 types: ALLOWED, MANUAL_PROCESSING, PROHIBITED. It depends on your amount, ip which from you do transaction, your cardnumber.

First user you register is always Administrator. Port for testing: 28852

