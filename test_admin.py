#!/usr/bin/env python3
"""
URL Shortener Admin API Test Script
This script tests all admin functionality programmatically
"""

import requests
import json
import sys

# Configuration
BASE_URL = "http://localhost:8080"
ADMIN_USERNAME = "admin"
ADMIN_PASSWORD = "admin123"

# Global token for subsequent requests
admin_token = None

def make_request(method, endpoint, data=None, headers=None):
    """Make HTTP request and return response."""
    url = f"{BASE_URL}{endpoint}"
    
    if headers is None:
        headers = {
            "Content-Type": "application/json"
        }
    if admin_token and "admin" in endpoint:
        headers["Authorization"] = f"Bearer {admin_token}"
    
    try:
        response = requests.request(method, url, json=data, headers=headers)
        return response
    except requests.exceptions.RequestException as e:
        print(f"❌ Request failed: {e}")
        return None

def test_admin_login():
    """Test admin login and extract token."""
    global admin_token
    
    print("🔄 Testing 1: Admin Login")
    print("=" * 50)
    print("POST /admin/login")
    
    data = {
        "username": ADMIN_USERNAME,
        "password": ADMIN_PASSWORD
    }
    
    response = make_request("POST", "/admin/login", data)
    if not response:
        print("❌ Failed to connect to application")
        return False
    
    if response.status_code == 200:
        result = response.json()
        admin_token = result.get('token')
        print(f"✅ Login successful: {result.get('message')}")
        print(f"🔑 Token: {admin_token[:30]}..." if admin_token else "❌ No token provided")
        return admin_token is not None
    else:
        print(f"❌ Login failed: Status {response.status_code}")
        print(f"Response: {response.text}")
        return False

def test_admin_dashboard():
    """Test admin dashboard endpoint."""
    print("\n🔄 Testing 2: Admin Dashboard")
    print("=" * 50)
    print("GET /admin/dashboard")
    
    response = make_request("GET", "/admin/dashboard")
    if not response:
        print("❌ Failed to connect")
        return False
    
    if response.status_code == 200:
        dashboard_data = response.json()
        print("✅ Dashboard data retrieved:")
        print(f"   📊 Total URLs: {dashboard_data.get('totalUrls', 'N/A')}")
        print(f"   👆 Total Clicks: {dashboard_data.get('totalClicks', 'N/A')}")
        print(f"   📈 Average Clicks: {dashboard_data.get('averageClicksPerUrl', 'N/A')}")
        print(f"   🔥 Top URLs: {len(dashboard_data.get('topUrls', []))}")
        print(f"   🆕 Recent URLs: {len(dashboard_data.get('recentUrls', []))}")
        return True
    else:
        print(f"❌ Dashboard request failed: Status {response.status_code}")
        return False

def test_admin_urls():
    """Test admin URLs listing."""
    print("\n🔄 Testing 3: All URLs")
    print("=" * 50)
    print("GET /admin/urls")
    
    response = make_request("GET", "/admin/urls")
    if not response:
        print("❌ Failed to connect")
        return False
    
    if response.status_code == 200:
        urls_data = response.json()
        print(f"✅ Retrieved {len(urls_data)} URLs")
        
        if urls_data:
            print("📝 Sample URLs:")
            for i, url in enumerate(urls_data[:3]):  # Show first 3 URLs
                print(f"   {i+1}. ID: {url.get('id')} - Short: {url.get('shortUrl')} - Original: {url.get('originalUrl')[:50]}...")
                return url.get('id')  # Return first ID for detail test
        return True
    else:
        print(f"❌ URLs request failed: Status {response.status_code}")
        return None

def test_admin_url_detail(url_id):
    """Test admin URL detail endpoint."""
    if not url_id:
        print("\n⏭️  Skipping URL detail test (no URL available)")
        return True
    
    print(f"\n🔄 Testing 4: URL Details")
    print("=" * 50)
    print(f"GET /admin/urls/{url_id}")
    
    response = make_request("GET", f"/admin/urls/{url_id}")
    if not response:
        print("❌ Failed to connect")
        return False
    
    if response.status_code == 200:
        url_detail = response.json()
        print("✅ URL detail retrieved:")
        print(f"   🎯 ID: {url_detail.get('id')}")
        print(f"   📏 Short URL: {url_detail.get('shortUrl')}")
        print(f"   🌐 Original URL: {url_detail.get('originalUrl')}")
        print(f"   👆 Clicks: {url_detail.get('clickCount')}")
        print(f"   📅 Created: {url_detail.get('createdAt')}")
        return True
    elif response.status_code == 404:
        print("❌ URL not found")
        return False
    else:
        print(f"❌ URL detail failed: Status {response.status_code}")
        return False

def test_admin_stats():
    """Test admin stats endpoint."""
    print("\n🔄 Testing 5: Admin Statistics")
    print("=" * 50)
    print("GET /admin/stats")
    
    response = make_request("GET", "/admin/stats")
    if not response:
        print("❌ Failed to connect")
        return False
    
    if response.status_code == 200:
        stats_data = response.json()
        print("✅ Statistics retrieved:")
        print(f"   📊 Total URLs: {stats_data.get('totalUrls')}")
        print(f"   👆 Total Clicks: {stats_data.get('totalClicks')}")
        print(f"   📈 Average Clicks: {stats_data.get('averageClicksPerUrl')}")
        return True
    else:
        print(f"❌ Stats request failed: Status {response.status_code}")
        return False

def test_invalid_token():
    """Test invalid token response."""
    print("\n🔄 Testing 6: Invalid Token (Security Test)")
    print("=" * 50)
    
    temp_token = admin_token
    import requests
    
    response = requests.get(f"{BASE_URL}/admin/urls", headers={"Authorization": "Bearer invalid_token"})
    
    if response.status_code == 401:
        print("✅ Security working correctly (401 Unauthorized)")
        return True
    else:
        print(f"⚠️  Unexpected response code: {response.status_code}")
        return False

def main():
    """Run all admin API tests."""
    print("🧪 Admin API Testing Starting...")
    print("=" * 50)
    print(f"🔗 Base URL: {BASE_URL}")
    print(f"👤 Testing Admin: {ADMIN_USERNAME} / {ADMIN_PASSWORD}")
    
    # Test sequence
    tests = [
        test_admin_login,
        test_admin_dashboard,
        lambda: test_admin_url_detail(test_admin_urls()),
        test_admin_stats,
        test_invalid_token
    ]
    
    passed = 0
    failed = 0
    
    for test in tests:
        try:
            if test():
                passed += 1
            else:
                failed += 1
        except Exception as e:
            print(f"❌ Test error: {e}")
            failed += 1
    
    # Summary
    print(f"\n🏁 Test Summary")
    print("=" * 50)
    print(f"✅ Passed: {passed}")
    print(f"❌ Failed: {failed}")
    print(f"📊 Total Tests: {passed + failed}")
    
    if failed == 0:
        print("🎉 All admin tests passed! The admin interface is working correctly.")
    else:
        print("⚠️  Some tests failed. Please verify application is running and database is accessible.")

if __name__ == "__main__":
    main()
