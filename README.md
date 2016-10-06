# The Future is asynchronous: Tools for scalable services 

## Intro

This is a set of resources for understanding the reasons for moving to a
non-blocking/asynchronous architecture.
 
I'm doing a talk at the following conferences based on this material:
* Devoxx Poland 2016
* JAX London 2016
* London Java Community

It attempts to show the benefit of asynchronous execution and then how 
to deal with the complexities of non-blocking and asynchronous execution.

## Motivation

Building services that call out to external systems such as databases, 
queues and HTTP services present some common scenarios:

* Sequential call outs: Easy! Stock to synchronous blocking calls

However with a synchronous threading model a large amount of system resources
are spent on threads that are simply waiting on IO. This has become more relevant
with the increase in the use of linux containers which promote the efficient
use of resources due to allowing multi tenancy.

* Sequential call outs where we can't afford the cost of a blocking thread (harder)

* Concurrent calls out to multiple services and aggregating the results: hint we can't do this synchronously any more

* Concurrent calls out and waiting for a subset to respond

* Concurrent calls out that need merged along with some other async calls e.g writing an audit log when complete

For any of the above scenarios the simplicity of the synchronous programming mode
breaks down as we need to include threads, executors etc.

The content aims to introduce the relevant tools for building all of the above 
scenarios. 


## Content

All the content is currently in the `standalone` module as it is easier
to show all the scenarios without involving real HTTP/DB calls.

I plan to extend this showing in a dropwizard and ratpact application.

### The scenario

The scenario is from internet television where a user wants to watch an online channel


* A an external request is required to validate a user exists
* Then a second call out that depends on the first to get the user's permissions
* Then then an unrelated call out to get the information for a TV channel the user wants to watch
* Return when all of the above is finished
* It turns out that the channel call takes a while so it needs to happen concurrently with the first two
* Then add a timeout so if any of the external call outs don't finish we return with a failure
* Finally add a fall back if the error is something specific


### Future (Jdk)

### ListenableFuture (Guava)

### CompletableFuture (Jdk)

### Observable (RxJava)
- Removed due to length of presentation

## Take aways

* If you're new to programming with Futures and callbacks expect a steep learning curve
  * Iterative test driven development is not (IMO) the best way to learn this stuff
  * Coursera courses are awesome
  * You will meet a monad eventually
* Know the execution model of any non-blocking framework you use
  * Find its thread pools
  * Which thread does any transform / flatmap etc work on. Does it block on the first future?
  * Is it async at the IO layer?
  * Do regular thread dumps of your application under load to confirm your assumptions
* Test with realistic concurrent users ASAP
  * Non-functional test that will fail if you block a thread that you are not meant to

### Demo

* Check users can see with seeing slide

* Show the synchronous tv service and show the async service we need to 
  implement by the end
    - 500 ms per call
    - Go through the complicated call in the synchronous service
    - How long will the method take?

* Show slide on scenarios and go through them
 
* Show the synchronous test for UserService, ChannelsService and PermissionsService
  - Explain the AsyncResponse and resume

* Explain that all calls are delayed by 500 milliseconds to handle delay

* Go go the unit tests for Sync, Futures, Listenable Futures, Completable Futures

* Go through the requirements

Requirement 1: Check user chbatey has the SPORTS permission
* Show existing synchronous 

Requirement 2:  Check chbatey can watch SkySportsOne
* Show existing synchronously

Requirement 3: Speed up scenario two by making any independent calls concurrent
* Implement synchronously. Show how hard it is to do when all your
  code relies on blocking calls e.g Using an executor for a small part
* Implement with a vanilla Future  
* Implement with all futures

// Move to Listenable Future


Requirement 4: Remove blocking
* Given up on synchronous implementation. Explain why.
* Introduce the ListenableFuture
* Implement with call backs (urgly)
* Implement with transforms and then a final callback

Requirement 5: Remove Guava, only use JDK tools
* Explain why bringing in Guava can be controversial
* However it is heavily used and not everyone is on Java 8

SyncTVService
* Demo concurrent request time
* Demo timeout

AsyncTVService
* Demo after

Complete Demo:
* 1000 requests 100 concurrent. Show thread count and memory
* Reduce threads to 5 and do it again



