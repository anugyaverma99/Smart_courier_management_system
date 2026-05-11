// user roles
export type Role = 'CUSTOMER' | 'ADMIN';
// all possible delivery statuses
export type DeliveryStatus = 'DRAFT' | 'BOOKED' | 'PICKED_UP' | 'IN_TRANSIT' | 'OUT_FOR_DELIVERY' | 'DELIVERED' | 'DELAYED' | 'FAILED' | 'RETURNED';
// report generation types
export type ReportType = 'DAILY' | 'WEEKLY' | 'MONTHLY';

// logged in user session data
export interface AuthUser { token: string; email: string; fullName: string; role: Role; userId: number; active?: boolean; }
// sender/receiver address
export interface AddressDto { name: string; phone: string; email: string; addressLine: string; city: string; state: string; zipCode: string; country: string; }
// package dimensions and details
export interface PackageDto { description: string; weightKg: number; lengthCm: number; widthCm: number; heightCm: number; serviceType: 'domestic' | 'express' | 'international'; declaredValue: number; }
// delivery order
export interface Delivery { id: number; trackingNumber: string; customerId: string; senderAddress: AddressDto; receiverAddress: AddressDto; packageDetails: PackageDto; status: DeliveryStatus; charge: number; pickupScheduledAt: string; createdAt: string; updatedAt: string; }
// single tracking event in timeline
export interface TrackingEvent { id: number; deliveryId: string; trackingNumber: string; status: DeliveryStatus; location: string; remarks: string; updatedBy: string; eventTime: string; createdAt: string; }
// uploaded document (invoice, label, etc.)
export interface DeliveryDocument { id: number; deliveryId: string; trackingNumber: string; fileName: string; filePath: string; documentType: string; contentType: string; uploadedBy: string; uploadedAt: string; }
// proof of delivery submitted by rider
export interface DeliveryProof { id: number; deliveryId: string; trackingNumber: string; receivedBy: string; proofImagePath?: string; remarks?: string; submittedBy: string; deliveredAt: string; createdAt: string; }
// hub/warehouse details
export interface Hub { id: number; name: string; city: string; state: string; pincode: string; contactNumber: string; active: boolean; createdAt: string; }
// admin monitor view of a delivery
export interface MonitorDelivery { id: number; deliveryId: string; trackingNumber: string; customerName: string; senderCity: string; receiverCity: string; currentStatus: DeliveryStatus; assignedHub: string; lastUpdated: string; liveSenderName?: string; liveReceiverName?: string; latestTrackingStatus?: string; latestTrackingLocation?: string; }
// exception raised on problematic delivery
export interface ExceptionItem { id: number; deliveryId: string; trackingNumber: string; exceptionStatus: DeliveryStatus; resolutionStatus: 'OPEN' | 'RESOLVED'; reason: string; remarks?: string; resolvedBy?: string; raisedAt: string; resolvedAt?: string; }
// generated report summary
export interface ReportItem { id: number; reportType: ReportType; fromDate: string; toDate: string; totalDeliveries: number; deliveredCount: number; failedCount: number; delayedCount: number; returnedCount: number; generatedBy: string; generatedAt: string; liveDeliveryCount: number; totalTrackingEvents: number; }
// managed user in admin panel
export interface ManagedUser { userId: number; fullName: string; email: string; phone?: string; role: Role; active?: boolean; }
// admin dashboard stats
export interface DashboardResponse { totalDeliveries: number; deliveredToday: number; inTransit: number; outForDelivery: number; exceptions: number; activeHubs: number; liveDeliveryCount: number; totalTrackingEvents: number; }

