"use client";

import { useEffect, useState } from "react";
import type { RecommendationResponse } from "@/lib/types";
import { apiBase } from "@/lib/api";
import CarCard from "./CarCard";

interface Props {
  context: "LISTING_TOP" | "DETAIL";
  excludeCarId?: number;
  title?: string;
}

export default function RecommendationStrip({ context, excludeCarId, title }: Props) {
  const [data, setData] = useState<RecommendationResponse | null>(null);

  useEffect(() => {
    const params = new URLSearchParams({ context });
    if (excludeCarId) params.set("excludeCarId", String(excludeCarId));
    fetch(`${apiBase()}/api/v1/recommendations?${params.toString()}`, {
      credentials: "include"
    })
      .then((r) => (r.ok ? r.json() : null))
      .then(setData)
      .catch(() => setData(null));
  }, [context, excludeCarId]);

  if (!data || data.items.length === 0) return null;

  const heading =
    title ??
    (context === "DETAIL"
      ? "You may also like"
      : data.strategy === "PERSONALIZED"
        ? "Recommended for you"
        : "Popular right now");

  return (
    <section className="rounded-2xl bg-white p-4 shadow-sm ring-1 ring-gray-200">
      <div className="mb-3 flex items-center justify-between">
        <h2 className="text-base font-bold text-gray-900">{heading}</h2>
        {data.strategy === "POPULAR_FALLBACK" && (
          <span className="rounded-full bg-orange-50 px-2 py-0.5 text-xs text-brand">
            Popular
          </span>
        )}
      </div>
      <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 md:grid-cols-5">
        {data.items.map((car) => (
          <CarCard key={car.id} car={car} />
        ))}
      </div>
    </section>
  );
}
