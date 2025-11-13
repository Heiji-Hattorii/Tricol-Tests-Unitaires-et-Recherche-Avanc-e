package com.gestion.stock.repository;

import com.gestion.stock.entity.MouvementStock;
import com.gestion.stock.enums.TypeMouvement;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MouvementStockSpecification {

    public static Specification<MouvementStock> produitId(Long produitId) {
        return (root, query, cb) ->
                produitId == null ? null :
                        cb.equal(root.get("stock").get("produit").get("id"), produitId);
    }

    public static Specification<MouvementStock> referenceProduit(String reference) {
        return (root, query, cb) ->
                reference == null ? null :
                        cb.equal(root.get("stock").get("produit").get("reference"), reference);
    }

    public static Specification<MouvementStock> type(String type) {
        return (root, query, cb) -> {
            if (type == null) return null;
            try {
                TypeMouvement tm = TypeMouvement.valueOf(type);
                return cb.equal(root.get("typeMouvement"), tm);
            } catch (IllegalArgumentException ex) {
                return null;
            }
        };
    }

    public static Specification<MouvementStock> numeroLot(String numeroLot) {
        return (root, query, cb) ->
                numeroLot == null ? null :
                        cb.equal(root.get("stock").get("numeroLot"), numeroLot);
    }

    public static Specification<MouvementStock> dateBetween(LocalDate dateDebut, LocalDate dateFin) {
        return (root, query, cb) -> {

            if (dateDebut == null && dateFin == null) {
                return cb.conjunction();
            }

            LocalDateTime start = (dateDebut != null) ? dateDebut.atStartOfDay() : null;
            LocalDateTime end = (dateFin != null) ? dateFin.atTime(23, 59, 59) : null;

            if (start != null && end != null) {
                return cb.between(root.get("dateMouvement"), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("dateMouvement"), start);
            } else {
                return cb.lessThanOrEqualTo(root.get("dateMouvement"), end);
            }
        };
    }

}
