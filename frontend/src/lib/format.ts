export function formatPrice(price: number): string {
  return new Intl.NumberFormat("zh-CN", {
    style: "currency",
    currency: "CNY",
    maximumFractionDigits: 0
  }).format(price);
}

export function formatMileage(km: number): string {
  if (km >= 10000) {
    return `${(km / 10000).toFixed(1)}万公里`;
  }
  return `${km.toLocaleString()} km`;
}
