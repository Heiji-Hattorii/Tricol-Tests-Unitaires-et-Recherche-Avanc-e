package com.gestion.stock.service;

import com.gestion.stock.entity.AuditLog;
import java.util.List;

public interface AuditService {
    void logAction(String action, String entityType, String entityId, String userEmail, String details, String ipAddress);
    List<AuditLog> getAllAuditLogs();
}