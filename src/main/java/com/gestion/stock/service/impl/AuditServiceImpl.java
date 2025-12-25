package com.gestion.stock.service.impl;

import com.gestion.stock.entity.AuditLog;
import com.gestion.stock.repository.AuditLogRepository;
import com.gestion.stock.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void logAction(String action, String entityType, String entityId, String userEmail, String details, String ipAddress) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setUserEmail(userEmail);
        auditLog.setDetails(details);
        auditLog.setTimestamp(LocalDateTime.now());
        
        auditLogRepository.save(auditLog);
    }

    @Override
    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll();
    }
}