# Anti-Fraud-System
In the final stage adapted mechanism feedback to service. Feedback will be carried out</br>
manually by a SUPPORT specialist for completed transactions. Based on the feedback</br>
results, we will change the limits of fraud detection algorithms following the special rules.

<table>
    <tr>
        <th>Transaction Feedback →</br>
        Transaction Validity ↓</th>
        <th>ALLOWED</th>
        <th>MANUAL_PROCESSING</th>
        <th>PROHIBITED</th>
    </tr>
    <tr>
        <td>ALLOWED</td>
        <td>Exception</td>
        <td>↓ max ALLOWED</td>
        <td>↓ max ALLOWED
            </br>↓ max MANUAL</td>
    </tr>
    <tr>
        <th>MANUAL_PROCESSING</th>
        <td>↑ max ALLOWED</td>
        <td>Exception</td>
        <td>↓ max MANUAL</td>
    </tr>
    <tr>
        <th>PROHIBITED</th>
        <td>↑ max ALLOWED</br>
            ↑ max MANUAL</td>
        <td>↑ max MANUAL</td>
        <td>Exception</td>
    </tr>
    </tbody>
</table>

Implemented the role model for system:</br>
<table>
<tr>
    <th></th>
    <th>Anonymous</th>
    <th>MERCHANT</th>
    <th>ADMINISTRATOR</th>
    <th>SUPPORT</th>
</tr>
<tr>
    <td>POST /api/auth/user</td>
    <td>+</td>
    <td>+</td>
    <td>+</td>
    <td>+</td>
</tr>
<tr>
    <td>DELETE /api/auth/user</td>
    <td>-</td>
    <td>-</td>
    <td>+</td>
    <td>-</td>
</tr>
<tr>
    <td>GET /api/auth/list</td>
    <td>-</td>
    <td>-</td>
    <td>+</td>
    <td>+</td>
</tr>
<tr>
    <td>POST /api/antifraud/transaction</td>
    <td>-</td>
    <td>+</td>
    <td>-</td>
    <td>-</td>
</tr>
<tr>
    <td>POST /api/auth/access</td>
    <td>-</td>
    <td>-</td>
    <td>+</td>
    <td>-</td>
</tr>
<tr>
    <td>POST /api/auth/role</td>
    <td>-</td>
    <td>-</td>
    <td>+</td>
    <td>-</td>
</tr>
<tr>
    <td>POST, DELETE, GET api/antifraud/suspicious-ip</td>
    <td>-</td>
    <td>-</td>
    <td>-</td>
    <td>+</td>
</tr>
<tr>
    <td>POST, DELETE, GET api/antifraud/stolencard</td>
    <td>-</td>
    <td>-</td>
    <td>-</td>
    <td>+</td>
</tr>
<tr>
    <td>GET /api/antifraud/history</td>
    <td>-</td>
    <td>-</td>
    <td>-</td>
    <td>+</td>
</tr>
<tr>
    <td>PUT /api/antifraud/transaction</td>
    <td>-</td>
    <td>-</td>
    <td>-</td>
    <td>+</td>
</tr>
</table>

ADMINISTRATOR is the user who has registered first, all other users</br>
should receive the MERCHANT roles. All users added after ADMINISTRATOR</br>
must be locked by default and unlocked later by ADMINISTRATOR.</br>
The SUPPORT role should be assigned by ADMINISTRATOR to one of</br>
the users later.

In our service, we will check IP addresses for compliance with IPv4.</br>
Any address following this format consists of four series of numbers</br>
from 0 to 255 separated by dots.

Card numbers must be checked according to the Luhn algorithm.

Added correlation to fraud detection rules. Now, the result of the operation</br>
depends on other operations.

Enriched the transaction event with the world region and the transaction date.</br>
There is the table for world region codes:

<table>
    <tr>
        <th>Code</th>
        <th>Description</th>
    </tr>
    <tr>
        <td>EAP</td>
        <td>East Asia and Pacific</td>
    </tr>
    <tr>
        <td>ECA</td>
        <td>Europe and Central Asia</td>
    </tr>
    <tr>
        <td>HIC</td>
        <td>High-Income countries</td>
    </tr>
    <tr>
        <td>LAC</td>
        <td>Latin America and the Caribbean</td>
    </tr>
    <tr>
        <td>MENA</td>
        <td>The Middle East and North Africa</td>
    </tr>
    <tr>
        <td>SA</td>
        <td>South Asia</td>
    </tr>
    <tr>
        <td>SSA</td>
        <td>Sub-Saharan Africa</td>
    </tr>
</table>

A transaction containing a card number is PROHIBITED if:

1.There are transactions from more than 2 regions of the world other than the region</br>
of the transaction that is being verified in the last hour in the transaction history;

2.There are transactions from more than 2 unique IP addresses other than the IP of the</br>
transaction that is being verified in the last hour in the transaction history.

A transaction containing a card number is sent for MANUAL_PROCESSING if:

1.There are transactions from 2 regions of the world other than the region of the transaction</br>
that is being verified in the last hour in the transaction history;

2.There are transactions from 2 unique IP addresses other than the IP of the transaction</br>
that is being verified in the last hour in the transaction history.


### Examples

The following examples are using the JSON format.

#### Signup

```
POST /api/auth/user
{
   "name": "<String value, not empty>",
   "username": "<String value, not empty>",
   "password": "<String value, not empty>"
}
```

Response:

```
{
    "id": 1,
    "name": "John Doe",
    "username": "JohnDoe",
    "role": "ADMINISTRATOR"
}
```

#### Delete user

```
DELETE /api/auth/user/{username}
```

Response:

```
{
   "username": "JohnDoe",
   "status": "Deleted successfully!"
}
```

#### Get user list

```
GET /api/auth/list
```

Response:

```
[
    {
        "id": <user1 id>,
        "name": "<user1 name>",
        "username": "<user1 username>",
        "role": "<user1 role>"
    },
     ...
    {
        "id": <userN id>,
        "name": "<userN name>",
        "username": "<userN username>",
        "role": "<userN role>"
    }
]
```

#### Update user role

```
PUT /api/auth/role
{
   "username": "<String value, not empty>",
   "role": "<String value, not empty>"
}
```

Response:

```
{
   "id": <Long value, not empty>,
   "name": "<String value, not empty>",
   "username": "<String value, not empty>",
   "role": "<String value, not empty>"
}
```

#### Update user access

```
PUT /api/auth/access
{
   "username": "<String value, not empty>",
   "operation": "<[LOCK, UNLOCK]>"  // determines whether the user will be activated or deactivated
}
```

Response:

```
{
    "status": "User <username> <[locked, unlocked]>!"
}
```

#### Post transaction

```
POST /api/antifraud/transaction
{
  "amount": <Long>,
  "ip": "<String value, not empty>",
  "number": "<String value, not empty>",
  "region": "<String value, not empty>",
  "date": "yyyy-MM-ddTHH:mm:ss"
}
```

Response:

```
{
   "result": "ALLOWED",
   "info": "none"
}
```

#### Save suspicious IP

```
POST /api/antifraud/suspicious-ip
{
  "ip": "<String value, not empty>"
}
```

Response:

```
{
   "id": "<Long value, not empty>",
   "ip": "<String value, not empty>"
}
```

#### Delete suspicious IP

```
DELETE /api/antifraud/suspicious-ip/{ip}
```

Response:

```
{
   "status": "IP <ip address> successfully removed!"
}
```

#### Save stolen card number

```
POST /api/antifraud/stolencard
{
  "number": "<String value, not empty>"
}
```

Response:

```
{
   "id": "<Long value, not empty>",
   "number": "<String value, not empty>"
}
```

#### Delete stolen card number

```
DELETE /api/antifraud/stolencard/{number}
```

Response:

```
{
   "status": "Card <number> successfully removed!"
}
```

#### Add transaction feedback

```
PUT /api/antifraud/transaction
{
   "transactionId": <Long>,
   "feedback": "<String>"
}
```

Response:

```
{
  "transactionId": <Long>,
  "amount": <Long>,
  "ip": "<String value, not empty>",
  "number": "<String value, not empty>",
  "region": "<String value, not empty>",
  "date": "yyyy-MM-ddTHH:mm:ss",
  "result": "<String>",
  "feedback": "<String>"
}
```

#### Get transaction history

```
GET /api/antifraud/history/{number}
```

Response:

```
[
    {
      "transactionId": <Long>,
      "amount": <Long>,
      "ip": "<String value, not empty>",
      "number": number,
      "region": "<String value, not empty>",
      "date": "yyyy-MM-ddTHH:mm:ss",
      "result": "<String>",
      "feedback": "<String>"
    },
     ...
    {
      "transactionId": <Long>,
      "amount": <Long>,
      "ip": "<String value, not empty>",
      "number": number,
      "region": "<String value, not empty>",
      "date": "yyyy-MM-ddTHH:mm:ss",
      "result": "<String>",
      "feedback": "<String>"
    }
]
```

_Note that these are just basic examples of the most common endpoints. The full list of endpoints is available in the
table above._
