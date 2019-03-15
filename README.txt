This is a caching simulator which tests different caching policies on different distributions and cache sizes.

To run this code, simply compile using javac and run from the command line using java CacheSim, no input parameters 
necessary.

Follow the prompts to select a distribution, cache size, and cache replacement policy, and observe the output.

The code contains only void methods with no input parameters.

unifSim(), zipfSim(), and depSim() are all setup methods, which are called depending on user input, and fill the RAM 
array with values from the specified distribution.

Rand(), FIFO(), LRU(), and LFU() each run 10^6 data requests, checking the cache each time and recording the number 
of cache hits and misses. Each seperate method uses a different type of replacement policy in the case of a miss, which
is specified by user input.