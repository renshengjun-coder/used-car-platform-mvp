import { notFound } from "next/navigation";
import Link from "next/link";
import { getListing } from "@/lib/api";
import { formatMileage, formatPrice } from "@/lib/format";
import ViewTracker from "@/components/ViewTracker";
import RecommendationStrip from "@/components/RecommendationStrip";

export const dynamic = "force-dynamic";

export default async function CarDetailPage({
  params
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const car = await getListing(id);
  if (!car) notFound();

  const specs: [string, string | number | null][] = [
    ["Year", car.year],
    ["Mileage", formatMileage(car.mileage)],
    ["Make", car.make],
    ["Model", car.model],
    ["Fuel", car.fuelType],
    ["Transmission", car.transmission],
    ["City", car.city]
  ];

  return (
    <div className="space-y-6">
      <ViewTracker carId={car.id} />

      <Link href="/" className="text-sm text-gray-500 hover:text-brand">
        ← Back to listings
      </Link>

      <div className="grid gap-6 md:grid-cols-2">
        <div className="space-y-3">
          <div className="overflow-hidden rounded-2xl bg-gray-100">
            {/* eslint-disable-next-line @next/next/no-img-element */}
            <img
              src={car.photoUrls[0] ?? "https://picsum.photos/seed/placeholder/800/600"}
              alt={car.title}
              className="aspect-[4/3] w-full object-cover"
            />
          </div>
          {car.photoUrls.length > 1 && (
            <div className="grid grid-cols-4 gap-2">
              {car.photoUrls.slice(1, 5).map((url, i) => (
                // eslint-disable-next-line @next/next/no-img-element
                <img
                  key={i}
                  src={url}
                  alt={`${car.title} ${i + 2}`}
                  className="aspect-square w-full rounded-lg object-cover"
                />
              ))}
            </div>
          )}
        </div>

        <div className="space-y-4">
          <h1 className="text-2xl font-bold text-gray-900">{car.title}</h1>
          <p className="text-3xl font-extrabold text-brand">{formatPrice(car.price)}</p>
          <dl className="grid grid-cols-2 gap-3 rounded-2xl bg-white p-4 ring-1 ring-gray-200">
            {specs.map(([label, value]) => (
              <div key={label}>
                <dt className="text-xs text-gray-500">{label}</dt>
                <dd className="text-sm font-medium text-gray-900">{value ?? "—"}</dd>
              </div>
            ))}
          </dl>
          {car.description && (
            <div className="rounded-2xl bg-white p-4 ring-1 ring-gray-200">
              <h2 className="mb-2 text-sm font-semibold text-gray-900">Description</h2>
              <p className="whitespace-pre-line text-sm text-gray-700">{car.description}</p>
            </div>
          )}
        </div>
      </div>

      {/* AC-016: "You may also like" recommendation section in the detail page */}
      <RecommendationStrip context="DETAIL" excludeCarId={car.id} />
    </div>
  );
}
