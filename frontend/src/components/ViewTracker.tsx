"use client";

import { useEffect } from "react";
import { apiBase } from "@/lib/api";

export default function ViewTracker({ carId }: { carId: number }) {
  useEffect(() => {
    fetch(`${apiBase()}/api/v1/behavior/view`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify({ carId })
    }).catch(() => {});
  }, [carId]);

  return null;
}
