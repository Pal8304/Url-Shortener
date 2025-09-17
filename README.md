# Url Shortener Backend

A rudimentary URL shortener backend service built primarily for learning and demonstration purposes.

## Running the Application
- Ensure you have [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or higher  installed.

- Clone the repository ``` git clone https://github.com/Pal8304/Url-Shortener.git```



## API Endpoints 

- **Generate Shortened Url** : 
```
POST http://localhost:8080/api/
    
Body:
{
   "originalUrl" : "https://github.com/Pal8304/Url-Shortener"
}
```
                  
- **Redirect to Original Url** : ```GET http://localhost:8080/api/{short_url} ```
