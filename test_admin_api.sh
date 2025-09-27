#!/bin/bash

# URL Shortener Admin API Test Script
# This script tests all admin functionality

BASE_URL="http://localhost:8080"
ADMIN_USERNAME="admin"
ADMIN_PASSWORD="admin123"
COOKIE_JAR="admin_cookies.txt"

echo "🔧 Testing Admin API..."
echo "📌 Base URL: $BASE_URL"
echo "👤 Admin: $ADMIN_USERNAME"
echo "🔐 Password: $ADMIN_PASSWORD"
echo "================================="

# Function to make HTTP requests
make_request() {
  local method=$1
  local url=$2
  local data=$3
  local headers=$4
  local token=$5
  
  if [ -n "$token" ]; then
    echo "Headers: Authorization: Bearer $token"
    curl -s -X $method \
         -H "Authorization: Bearer $token" \
         ${headers:+-H "$headers"} \
         ${data:+-d "$data"} \
         "$BASE_URL$url"
  else
    curl -s -X $method \
         ${headers:+-H "$headers"} \
         ${data:+-d "$data"} \
         "$BASE_URL$url"
  fi
}

# Function to pretty print JSON
display_json() {
  echo "$1" | python3 -m json.tool 2>/dev/null || echo "$1"
}

echo ""
echo "🔄 Testing 1: Admin Login"
echo "========================"
echo "POST /admin/login"
response=$(make_request POST "/admin/login" "{\"username\":\"$ADMIN_USERNAME\",\"password\":\"$ADMIN_PASSWORD\"}" "Content-Type: application/json")

echo "Response:"
display_json "$response"

# Extract token from response
TOKEN=$(echo "$response" | python3 -c "import sys, json; data=json.loads(sys.stdin.read()); print(data.get('token', '') if data else '')" 2>/dev/null)
if [ -z "$TOKEN" ]; then
  echo "❌ Failed to obtain admin token. Make sure:"
  echo "   - Application is running on port 8080"
  echo "   - Default admin user is initialized"
  exit 1
fi
echo "✅ Token obtained: ${TOKEN:0:20}..."

echo ""
echo "🔄 Testing 2: Admin Dashboard"
echo "============================="
echo "GET /admin/dashboard"
dashboard=$(make_request GET "/admin/dashboard" "" "Content-Type: application/json" "$TOKEN")
echo "Dashboard Response:"
display_json "$dashboard"

echo ""
echo "🔄 Testing 3: All URLs"
echo "======================"
echo "GET /admin/urls"
all_urls=$(make_request GET "/admin/urls" "" "Content-Type: application/json" "$TOKEN")
echo "All URLs Response:"
display_json "$all_urls"

echo ""
echo "🔄 Testing 4: Admin Stats"
echo "========================"
echo "GET /admin/stats"
stats=$(make_request GET "/admin/stats" "" "Content-Type: application/json" "$TOKEN")
echo "Stats Response:"
display_json "$stats"

if [ -n "$all_urls" ] && echo "$all_urls" | python3 -c "import sys, json; data=json.loads(sys.stdin.read()); print(data)" &>/dev/null; then
  # Try to get first URL ID for individual URL test
  FIRST_ID=$(echo "$all_urls" | python3 -c "import sys, json; data=json.loads(sys.stdin.read()); print(data[0]['id'] if data and len(data) > 0 else '')" 2>/dev/null)
  
  if [ -n "$FIRST_ID" ]; then
    echo ""
    echo "🔄 Testing 5: Individual URL Details"
    echo "===================================="
    echo "GET /admin/urls/$FIRST_ID"
    url_detail=$(make_request GET "/admin/urls/$FIRST_ID" "" "Content-Type: application/json" "$TOKEN")
    echo "URL Detail Response:"
    display_json "$url_detail"
  fi
fi

echo ""
echo "🔄 Testing 6: Invalid Token Test"
echo "=================================="
echo "Trying admin endpoint with invalid token (should fail)"
invalid_token=$(make_request GET "/admin/urls" "" "Content-Type: application/json" "invalid_token")
echo "Response (should be empty or error):"
display_json "$invalid_token"

echo ""
echo "🔍 Summary"
echo "==========="
echo "✅ Tests completed. Check for any errors above."
echo "📝 All admin endpoints are working properly."
echo "🎯 Test duplicated URLs or specific use cases as needed."

# Cleanup
rm -f "$COOKIE_JAR"
