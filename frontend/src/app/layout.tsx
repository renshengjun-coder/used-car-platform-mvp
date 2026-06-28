import type { Metadata } from "next";
import Link from "next/link";
import "./globals.css";

export const metadata: Metadata = {
  title: "UsedCar — Find your next car",
  description: "Used car marketplace MVP: publish, search, and discover recommended cars."
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body className="min-h-screen">
        <header className="sticky top-0 z-10 border-b border-gray-200 bg-white">
          <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-3">
            <Link href="/" className="text-xl font-extrabold text-brand">
              Used<span className="text-gray-900">Car</span>
            </Link>
            <nav className="flex items-center gap-4 text-sm font-medium">
              <Link href="/" className="text-gray-700 hover:text-brand">
                Browse
              </Link>
              <Link
                href="/sell"
                className="rounded-lg bg-brand px-3 py-1.5 text-white hover:bg-brand-dark"
              >
                Sell a car
              </Link>
            </nav>
          </div>
        </header>
        <main className="mx-auto max-w-6xl px-4 py-6">{children}</main>
      </body>
    </html>
  );
}
