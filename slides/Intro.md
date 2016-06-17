Two reasons for async:
* Concurrency
* Scalability

Java tools:
* Future
* ListenableFuture
* CompletableFuture

Scenarios:
* Async call
* A second async call that depends on the result of the first
* Making concurrent async calls
* Combining all the results