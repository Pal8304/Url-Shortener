"use client";

import ky from "ky";
import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

const apiBaseUrl = process.env.SERVICE_BASE_URL || "http://localhost:8080";

export default function URLShortener() {
  const [longUrl, setLongUrl] = useState("");

  async function handleUrlSubmit(longUrl: string) {
    const apiUrl = `${apiBaseUrl}` + `/api/`;

    const response = await ky
      .post(apiUrl, {
        json: { originalUrl: longUrl },
      })
      .json();

    console.log("Response from API:", response);
  }
  return (
    <div className="flex flex-col w-2/3 h-2/3 justify-center items-center mx-auto rounded-lg shadow-lg border-4">
      <div className="flex flex-row w-full justify-center">
        <Label htmlFor="url-input" className="text-2xl m-2">
          URL Input
        </Label>
        <Input
          type="text"
          placeholder="Enter your long URL here"
          id="url-input"
          className="max-w-96 m-2"
          value={longUrl}
          onChange={(e) => setLongUrl(e.target.value)}
        />
      </div>
      <Button
        variant="outline"
        size="lg"
        className="m-2"
        onClick={() => {
          handleUrlSubmit(longUrl);
        }}
      >
        Generate Short URL
      </Button>
    </div>
  );
}
