import { ThemeToggle } from "@/components/theme/theme-toggle";

export default function Home() {
  return (
    <div className="w-screen h-screen">
      <div className="flex justify-end p-4">
        <ThemeToggle />
      </div>
    </div>
  );
}
