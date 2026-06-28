export interface ListingSummary {
  id: number;
  title: string;
  price: number;
  make: string;
  model: string;
  year: number;
  mileage: number;
  city: string;
  thumbnailUrl: string | null;
  publishedAt: string | null;
}

export interface ListingDetail extends ListingSummary {
  sellerId: number;
  fuelType: string | null;
  transmission: string | null;
  description: string | null;
  status: string;
  photoUrls: string[];
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface RecommendationResponse {
  items: ListingSummary[];
  strategy: "PERSONALIZED" | "POPULAR_FALLBACK";
}

export interface SearchFilters {
  keyword?: string;
  priceMin?: string;
  priceMax?: string;
  yearMin?: string;
  yearMax?: string;
  mileageMin?: string;
  mileageMax?: string;
  city?: string;
  fuelType?: string;
  transmission?: string;
  page?: string;
}
