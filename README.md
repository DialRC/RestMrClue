# RestMrClue

A REST API server based on [JAVA Spark Framework](https://github.com/perwendel/spark).

## Protocol:
### Initialize a New Session
POST http://localhost:port/init

```javascript
{
    sessionID: "USR_1234",
    timeStamp: "yyyy-MM-dd'T'HH-mm-ss.SSS"
}
```

### Get the Next Response of a Ongoing Session
POST http://locahost:port/next

```javascript
{
    sessionID: "USR_1234",
    text: "I guess the answer is APPLE", 
    asrConf: 0.9,
    timeStamp: "yyyy-MM-dd'T'HH-mm-ss.SSS"
}
```

### Expected Return Format

All the POST requests will have the same return JSON format. 
```javascript
{
    sessionID: "USR_1234",
    sys: "This word starts with A",
    terminal: False
    timeStamp: "yyyy-MM-dd'T'HH-mm-ss.SSS"
}
```
If non-verbal output is also needed. Please add the non-verbal features into the JSON.
