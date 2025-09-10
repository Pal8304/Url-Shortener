#!/bin/bash

GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'


# Start PostgresQL if not running
if ! brew services list | grep postgresql@15 | grep started > /dev/null; then
  echo -e "${BLUE}Starting PostgresQL...${NC}"
  brew services start postgresql@15
else
  echo -e "${GREEN}PostgresQL already running${NC}"
fi

#Clear redis
redis-cli FLUSHALL > /dev/null;
echo -e "${BLUE}Cleared redis cache"

# Start Redis if not running
if ! brew services list | grep redis | grep started > /dev/null; then
  echo -e "${BLUE}Starting Redis...${NC}"
  brew services start redis
else
  echo -e "${GREEN}Redis already running${NC}"
fi

# Start Spring Boot
./mvnw spring-boot:run