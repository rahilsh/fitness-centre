# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.4/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.4/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.0.4/reference/htmlsingle/#web)

### Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

```
curl -X PUT http://localhost:8080/fitnessCentres \
        -H "Content-Type: application/json" \
        -d '{"name": "Kor", "timings":[1,2,3,4], "supportedActivities": ["WEIGHTS","YOGA"]}'
```


Publisher
1. CancelBookingPublisher

Topic:
 CancelBooking

SubscribersList
Map<slotId, TreeSet<userId,registered_at>>

Subscribers
1. NotifyBookingCancellationUsers

publisher->(async) publish(ClassTopic) (async)-> subscribe(NotifyBookingCancellationUsers)