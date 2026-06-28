"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { apiBase } from "@/lib/api";

type Mode = "login" | "register";

export default function SellPage() {
  const router = useRouter();
  const [token, setToken] = useState<string | null>(null);
  const [authMode, setAuthMode] = useState<Mode>("login");
  const [auth, setAuth] = useState({ email: "demo@usedcar.dev", password: "demo1234" });
  const [message, setMessage] = useState<string | null>(null);

  const [form, setForm] = useState({
    title: "",
    price: "",
    make: "",
    model: "",
    year: "",
    mileage: "",
    fuelType: "GASOLINE",
    transmission: "AUTOMATIC",
    city: "",
    description: "",
    photoUrl: ""
  });

  async function submitAuth(e: React.FormEvent) {
    e.preventDefault();
    setMessage(null);
    const res = await fetch(`${apiBase()}/api/v1/auth/${authMode}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(auth)
    });
    const data = await res.json();
    if (!res.ok) {
      setMessage(data?.error?.message ?? "Authentication failed");
      return;
    }
    setToken(data.token);
    setMessage(`Signed in as ${data.email}`);
  }

  async function submitListing(e: React.FormEvent) {
    e.preventDefault();
    setMessage(null);
    if (!token) return;
    const body = {
      title: form.title,
      price: Number(form.price),
      make: form.make,
      model: form.model,
      year: Number(form.year),
      mileage: Number(form.mileage),
      fuelType: form.fuelType,
      transmission: form.transmission,
      city: form.city,
      description: form.description,
      photoUrls: form.photoUrl ? [form.photoUrl] : [],
      status: "PUBLISHED"
    };
    const res = await fetch(`${apiBase()}/api/v1/listings`, {
      method: "POST",
      headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
      body: JSON.stringify(body)
    });
    const data = await res.json();
    if (!res.ok) {
      setMessage(data?.error?.message ?? "Failed to publish listing");
      return;
    }
    router.push(`/cars/${data.id}`);
  }

  const inputCls =
    "w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-brand focus:outline-none";

  if (!token) {
    return (
      <div className="mx-auto max-w-md space-y-4">
        <h1 className="text-xl font-bold">Seller sign in</h1>
        <div className="flex gap-2 text-sm">
          <button
            className={authMode === "login" ? "font-bold text-brand" : "text-gray-500"}
            onClick={() => setAuthMode("login")}
          >
            Login
          </button>
          <span className="text-gray-300">|</span>
          <button
            className={authMode === "register" ? "font-bold text-brand" : "text-gray-500"}
            onClick={() => setAuthMode("register")}
          >
            Register
          </button>
        </div>
        <form onSubmit={submitAuth} className="space-y-3 rounded-2xl bg-white p-5 ring-1 ring-gray-200">
          <input
            className={inputCls}
            placeholder="Email"
            type="email"
            value={auth.email}
            onChange={(e) => setAuth({ ...auth, email: e.target.value })}
          />
          <input
            className={inputCls}
            placeholder="Password (min 8 chars)"
            type="password"
            value={auth.password}
            onChange={(e) => setAuth({ ...auth, password: e.target.value })}
          />
          <button className="w-full rounded-lg bg-brand py-2 font-semibold text-white hover:bg-brand-dark">
            {authMode === "login" ? "Login" : "Create account"}
          </button>
          {message && <p className="text-sm text-gray-600">{message}</p>}
          <p className="text-xs text-gray-400">Demo: demo@usedcar.dev / demo1234</p>
        </form>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-2xl space-y-4">
      <h1 className="text-xl font-bold">Publish a used car</h1>
      <form onSubmit={submitListing} className="grid grid-cols-2 gap-3 rounded-2xl bg-white p-5 ring-1 ring-gray-200">
        <input className={`${inputCls} col-span-2`} placeholder="Title" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} required />
        <input className={inputCls} placeholder="Price (RMB)" type="number" value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} required />
        <input className={inputCls} placeholder="City" value={form.city} onChange={(e) => setForm({ ...form, city: e.target.value })} required />
        <input className={inputCls} placeholder="Make" value={form.make} onChange={(e) => setForm({ ...form, make: e.target.value })} required />
        <input className={inputCls} placeholder="Model" value={form.model} onChange={(e) => setForm({ ...form, model: e.target.value })} required />
        <input className={inputCls} placeholder="Year" type="number" value={form.year} onChange={(e) => setForm({ ...form, year: e.target.value })} required />
        <input className={inputCls} placeholder="Mileage (km)" type="number" value={form.mileage} onChange={(e) => setForm({ ...form, mileage: e.target.value })} required />
        <select className={inputCls} value={form.fuelType} onChange={(e) => setForm({ ...form, fuelType: e.target.value })}>
          {["GASOLINE", "HYBRID", "EV", "DIESEL"].map((f) => <option key={f}>{f}</option>)}
        </select>
        <select className={inputCls} value={form.transmission} onChange={(e) => setForm({ ...form, transmission: e.target.value })}>
          {["AUTOMATIC", "MANUAL"].map((t) => <option key={t}>{t}</option>)}
        </select>
        <input className={`${inputCls} col-span-2`} placeholder="Photo URL (required to publish)" value={form.photoUrl} onChange={(e) => setForm({ ...form, photoUrl: e.target.value })} required />
        <textarea className={`${inputCls} col-span-2`} placeholder="Description" rows={4} value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
        <button className="col-span-2 rounded-lg bg-brand py-2 font-semibold text-white hover:bg-brand-dark">
          Publish listing
        </button>
        {message && <p className="col-span-2 text-sm text-red-600">{message}</p>}
      </form>
    </div>
  );
}
