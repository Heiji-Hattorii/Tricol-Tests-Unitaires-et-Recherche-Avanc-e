package com.gestion.stock.controller;

import com.gestion.stock.dto.response.MouvementStockResponseDTO;
import com.gestion.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("stocks/mouvements")
@RequiredArgsConstructor
public class MouvementStockController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<Page<MouvementStockResponseDTO>> searchMouvements(
            @RequestParam(required = false) Long produitId,
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String numeroLot,
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<MouvementStockResponseDTO> result = stockService.searchMouvements(
                produitId, reference, type, numeroLot, dateDebut, dateFin, page, size
        );
        return ResponseEntity.ok(result);
    }


    @GetMapping("/produit/{id}")
    public ResponseEntity<List<MouvementStockResponseDTO>> historiqueMouvementStockProduit(@PathVariable Long id) {
        return ResponseEntity.ok(stockService.historiqueMouvementStockProduit(id));
    }


    @GetMapping("/all")
    public ResponseEntity<List<MouvementStockResponseDTO>> historiqueMouvement() {
        return ResponseEntity.ok(stockService.historiqueMouvement());
    }
}
