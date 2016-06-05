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

## Take aways

* If you're new to programming with Futures and callbacks expect a steep learning curve
  * Iterative test driven development is not (IMO) the best way to learn this stuff
  * Coursera courses are awesome
  * You will meet a monad eventually
* Know the execution model of any non-blocking framework you use
  * Find its thread pools
  * Which thread does any transform / flatmap etc work on. Does it block on the first future?
  * Is it async at the IO layer?
  * Do regular thread dumps of your application under load to aconfirm your assumptions
* Test with realistic concurrent users ASAP
  * Non-functional test that will fail if you block a thread that you are not meant to

  


#### TODO

Converting between the various async tools
Async HTTP Clients
Async Databsae drivers
Async Queueing clients
Show async servlets
Work on ratpack example
Add akka example

