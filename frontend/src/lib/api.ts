import type {
  ListingDetail,
  ListingSummary,
  PageResponse,
  RecommendationResponse,
  SearchFilters
} from "./types";

const API_BASE =
  process.env.NEXT_PUBLIC_API_BASE ?? process.env.API_BASE ?? "http://localhost:8080";

export function apiBase(): string {
  return API_BASE;
}

function buildQuery(filters: SearchFilters): string {
  const params = new URLSearchParams();
  Object.entries(filters).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      params.set(key, String(value));
    }
  });
  const qs = params.toString();
  return qs ? `?${qs}` : "";
}

async function getJson<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, { cache: "no-store", ...init });
  if (!res.ok) {
    throw new Error(`API ${path} failed: ${res.status}`);
  }
  return res.json() as Promise<T>;
}

export async function searchListings(
  filters: SearchFilters
): Promise<PageResponse<ListingSummary>> {
  return getJson<PageResponse<ListingSummary>>(`/api/v1/listings${buildQuery(filters)}`);
}

export async function getListing(id: string | number): Promise<ListingDetail | null> {
  const res = await fetch(`${API_BASE}/api/v1/listings/${id}`, { cache: "no-store" });
  if (res.status === 404) return null;
  if (!res.ok) throw new Error(`getListing failed: ${res.status}`);
  return res.json() as Promise<ListingDetail>;
}

export async function getRecommendations(
  context: "LISTING_TOP" | "DETAIL",
  excludeCarId?: number
): Promise<RecommendationResponse> {
  const params = new URLSearchParams({ context });
  if (excludeCarId) params.set("excludeCarId", String(excludeCarId));
  return getJson<RecommendationResponse>(`/api/v1/recommendations?${params.toString()}`);
}
