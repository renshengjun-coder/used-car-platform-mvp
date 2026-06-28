---
artifact_id: REQ-004-out-of-scope
artifact_type: requirement
package_id: used-car-mvp
version: v1
status: draft
owner: ""
created_at: 2026-06-28
traces:
  - derives_from: "artifacts/used-car-mvp/01-requirements/PRD.md@v1"
related: []
---

# Out of Scope — used-car-mvp

Explicit exclusions to prevent scope creep toward a full Guazi-class platform.

## Commerce & transactions

- Online payment, deposit, escrow, installment, or loan integration
- Order placement, purchase contract, or ownership transfer workflow
- Trade-in valuation and vehicle exchange

## Communication & leads

- In-app chat, SMS, or WeChat integration
- Phone number masking or virtual numbers
- Lead assignment, CRM, or sales follow-up tools

## Trust & services

- Third-party vehicle inspection reports (e.g. 检测报告)
- Warranty, return policy, or dispute resolution flows
- Delivery/logistics scheduling

## Seller enterprise features

- Dealer organization hierarchy, roles, and permissions beyond single seller account
- Bulk CSV/API inventory import
- VIN decoder integration and automatic spec fill
- Multi-branch inventory management

## Buyer account features (deferred)

- Buyer registration/login (MVP uses session-based anonymous tracking)
- Saved favorites, compare list, or price alerts
- Purchase history

## Platform operations

- Full admin back-office UI (manual DB seeding acceptable for demo)
- Content moderation queue and automated fraud detection
- Audit logging beyond basic application logs

## Geographic & localization

- Multi-country, multi-currency, or multi-language support
- Region-specific compliance (ICP, real-name verification, etc.)

## Advanced discovery

- ML/deep-learning recommendation models
- Map-based search and geo-radius filter
- Natural language search

## Mobile & channels

- iOS/Android native apps
- Mini-program (WeChat/Alipay) clients

## Infrastructure hardening (post-MVP)

- Kubernetes production hardening, multi-AZ HA
- Elasticsearch cluster sizing and index lifecycle production tuning
- CDN and image processing pipeline beyond basic upload/resize
