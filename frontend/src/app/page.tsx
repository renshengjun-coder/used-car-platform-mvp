import { Suspense } from "react";
import FilterBar from "@/components/FilterBar";
import CarCard from "@/components/CarCard";
import RecommendationStrip from "@/components/RecommendationStrip";
import { searchListings } from "@/lib/api";
import type { SearchFilters } from "@/lib/types";

export const dynamic = "force-dynamic";

type SearchParams = { [key: string]: string | string[] | undefined };

function toFilters(params: SearchParams): SearchFilters {
  const pick = (k: string) => {
    const v = params[k];
    return typeof v === "string" ? v : undefined;
  };
  return {
    keyword: pick("keyword"),
    priceMin: pick("priceMin"),
    priceMax: pick("priceMax"),
    yearMin: pick("yearMin"),
    yearMax: pick("yearMax"),
    mileageMin: pick("mileageMin"),
    mileageMax: pick("mileageMax"),
    city: pick("city"),
    fuelType: pick("fuelType"),
    transmission: pick("transmission"),
    page: pick("page")
  };
}

export default async function HomePage({
  searchParams
}: {
  searchParams: Promise<SearchParams>;
}) {
  const params = await searchParams;
  const filters = toFilters(params);

  let listings;
  let error = false;
  try {
    listings = await searchListings(filters);
  } catch {
    error = true;
  }

  return (
    <div className="space-y-6">
      {/* AC-016: recommendation strip at the TOP of the listing page */}
      <RecommendationStrip context="LISTING_TOP" />

      <Suspense fallback={<div className="h-20" />}>
        <FilterBar />
      </Suspense>

      {error && (
        <div className="rounded-xl bg-red-50 p-4 text-sm text-red-700">
          Search is temporarily unavailable. Please try again.
        </div>
      )}

      {listings && (
        <section>
          <p className="mb-3 text-sm text-gray-500">
            {listings.totalElements} car{listings.totalElements === 1 ? "" : "s"} found
          </p>
          {listings.content.length === 0 ? (
            <div className="rounded-xl bg-white p-8 text-center text-gray-500 ring-1 ring-gray-200">
              No cars match your filters.
            </div>
          ) : (
            <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4">
              {listings.content.map((car) => (
                <CarCard key={car.id} car={car} />
              ))}
            </div>
          )}
        </section>
      )}
    </div>
  );
}
