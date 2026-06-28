import Link from "next/link";
import type { ListingSummary } from "@/lib/types";
import { formatMileage, formatPrice } from "@/lib/format";

export default function CarCard({ car }: { car: ListingSummary }) {
  return (
    <Link
      href={`/cars/${car.id}`}
      className="group block overflow-hidden rounded-xl bg-white shadow-sm ring-1 ring-gray-200 transition hover:shadow-md"
    >
      <div className="aspect-[4/3] w-full overflow-hidden bg-gray-100">
        {/* eslint-disable-next-line @next/next/no-img-element */}
        <img
          src={car.thumbnailUrl ?? "https://picsum.photos/seed/placeholder/640/480"}
          alt={car.title}
          className="h-full w-full object-cover transition group-hover:scale-105"
        />
      </div>
      <div className="space-y-1 p-3">
        <h3 className="line-clamp-1 text-sm font-semibold text-gray-900">{car.title}</h3>
        <p className="text-lg font-bold text-brand">{formatPrice(car.price)}</p>
        <p className="text-xs text-gray-500">
          {car.year} · {formatMileage(car.mileage)} · {car.city}
        </p>
      </div>
    </Link>
  );
}
