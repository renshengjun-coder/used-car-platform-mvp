"use client";

import { useRouter, useSearchParams } from "next/navigation";
import { useState } from "react";
import { apiBase } from "@/lib/api";

const FUEL_TYPES = ["", "GASOLINE", "HYBRID", "EV", "DIESEL"];
const TRANSMISSIONS = ["", "AUTOMATIC", "MANUAL"];

export default function FilterBar() {
  const router = useRouter();
  const params = useSearchParams();

  const [form, setForm] = useState({
    keyword: params.get("keyword") ?? "",
    priceMin: params.get("priceMin") ?? "",
    priceMax: params.get("priceMax") ?? "",
    yearMin: params.get("yearMin") ?? "",
    city: params.get("city") ?? "",
    fuelType: params.get("fuelType") ?? "",
    transmission: params.get("transmission") ?? ""
  });

  function update(key: keyof typeof form, value: string) {
    setForm((f) => ({ ...f, [key]: value }));
  }

  function submit(e: React.FormEvent) {
    e.preventDefault();
    const qs = new URLSearchParams();
    Object.entries(form).forEach(([k, v]) => {
      if (v) qs.set(k, v);
    });
    router.push(`/?${qs.toString()}`);

    fetch(`${apiBase()}/api/v1/behavior/search`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify({ keyword: form.keyword || null, filters: form })
    }).catch(() => {});
  }

  const inputCls =
    "rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-brand focus:outline-none";

  return (
    <form
      onSubmit={submit}
      className="grid grid-cols-2 gap-3 rounded-2xl bg-white p-4 shadow-sm ring-1 ring-gray-200 md:grid-cols-4 lg:grid-cols-7"
    >
      <input
        className={`${inputCls} col-span-2 lg:col-span-2`}
        placeholder="Search make / model / title"
        value={form.keyword}
        onChange={(e) => update("keyword", e.target.value)}
      />
      <input
        className={inputCls}
        placeholder="Min price"
        type="number"
        value={form.priceMin}
        onChange={(e) => update("priceMin", e.target.value)}
      />
      <input
        className={inputCls}
        placeholder="Max price"
        type="number"
        value={form.priceMax}
        onChange={(e) => update("priceMax", e.target.value)}
      />
      <input
        className={inputCls}
        placeholder="Min year"
        type="number"
        value={form.yearMin}
        onChange={(e) => update("yearMin", e.target.value)}
      />
      <input
        className={inputCls}
        placeholder="City"
        value={form.city}
        onChange={(e) => update("city", e.target.value)}
      />
      <select
        className={inputCls}
        value={form.fuelType}
        onChange={(e) => update("fuelType", e.target.value)}
      >
        {FUEL_TYPES.map((f) => (
          <option key={f} value={f}>
            {f || "Any fuel"}
          </option>
        ))}
      </select>
      <select
        className={inputCls}
        value={form.transmission}
        onChange={(e) => update("transmission", e.target.value)}
      >
        {TRANSMISSIONS.map((t) => (
          <option key={t} value={t}>
            {t || "Any gearbox"}
          </option>
        ))}
      </select>
      <button
        type="submit"
        className="rounded-lg bg-brand px-4 py-2 text-sm font-semibold text-white transition hover:bg-brand-dark"
      >
        Search
      </button>
    </form>
  );
}
