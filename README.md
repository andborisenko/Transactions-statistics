# Transactions statistics API
The main use case for our API is
to calculate real time statistic from the last 60 seconds. There will be two APIs, one of
them is called every time a transaction is made. It is also the sole input of this rest
API. The other one returns the statistic based of the transactions of the last 60 seconds.

## Specs

### Transactions

Every Time a new transaction happened, this endpoint will be called.

#### Request sample
```http
POST /transactions
{
    "amount": 12.3,
    "timestamp": 1478192204000
}
```
Where:
* `amount` is a double specifying the amount
* `time` is a long specifying unix time format in milliseconds


Returns: Empty body with either 201 or 204.
* 201 - in case of success
* 204 - if transaction is older than 60 seconds


### Statistics
This is the main endpoint of this task, this endpoint have to execute in constant time and
memory (O(1)). It returns the statistic based on the transactions which happened in the last 60
seconds.

#### Request sample
```http
GET /statistics
```

#### Response sample
```http
{
    "sum": 1000,
    "avg": 100,
    "max": 200,
    "min": 50,
    "count": 10
}
```

Where:
* `sum` is a double specifying the total sum of transaction value in the last 60
seconds
* `avg` is a double specifying the average amount of transaction value in the last
60 seconds
* `max` is a double specifying single highest transaction value in the last 60
seconds
* `min` is a double specifying single lowest transaction value in the last 60
seconds
* `count` is a long specifying the total number of transactions happened in the last
60 seconds

## Requirements
For the rest api, the biggest and maybe hardest requirement is to make the GET
/statistics execute in constant time and space. 
