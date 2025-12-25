package com.gestion.stock.service;

public interface AuditService {
    void logAction(String action, String entityType, String entityId, String userEmail, String details, String ipAddress);
}