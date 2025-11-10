import { ThemeToggle } from "@/components/theme/theme-toggle";
import URLShortener from "@/components/url-shortener";

export default function Home() {
  return (
    <div className="w-screen h-screen">
      <div className="fixed top-0 right-0 p-4">
        <ThemeToggle />
      </div>
      <div className="flex w-full text-5xl font-bold justify-center m-4">
        Url Shortener
      </div>
      <div className="flex flex-col w-full h-full">
        <URLShortener />
      </div>
    </div>
  );
}
