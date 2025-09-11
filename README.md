# Url Shortener Backend


Questions I have right now: 
1. Do we need to send url in request body or request params is fine ( so basically do I need to create a separate POJO )
2. Figure out we'll redirect when a session is active
3. How are we generating shortened URL, current idea: id in base 65 + salting
4. What if multiple users provide the same url to shorten, would be ok to use a previously generated shortened url for that specific url 
5. Handle redirect ( like we are needing https or http before url to redirect )
6. Find better hash function, we have three options now
   1. (Ax + B) % (62^k)
   2. Fiestal 
   3. Format Preserving Encryption ( FPE ), should I create a service or a config for FPE ( Also decide between https://github.com/mysto/java-fpe and https://github.com/bcgit/bc-java)
7. Implement Redis 
8. Figure out if need to caches, one for top and other for mapping short to CachedUrl
