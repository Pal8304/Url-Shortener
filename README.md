# Url Shortener Backend


Questions I have right now: 
1. Do we need to send url in request body or request params is fine ( so basically do I need to create a separate POJO )
2. Figure out we'll redirect when a session is active
3. How are we generating shortened URL, current idea: id in base 65 + salting
4. What if multiple users provide the same url to shorten, would be ok to use a previously generated shortened url for that specific url 
