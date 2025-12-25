package com.gestion.stock.controller;


import com.gestion.stock.dto.response.MouvementStockResponseDTO;
import com.gestion.stock.dto.response.ProduitResponseDTO;
import com.gestion.stock.dto.response.StockResponseDTO;
import com.gestion.stock.entity.MouvementStock;
import com.gestion.stock.entity.Stock;
import com.gestion.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;


    @GetMapping
    @PreAuthorize("hasAuthority('STOCK_READ')")
    public ResponseEntity<List<StockResponseDTO>> getAllTheStock(){
        return ResponseEntity.ok(stockService.allStock());
    }

    @GetMapping("/produit/{id}")
    @PreAuthorize("hasAuthority('STOCK_READ')")
    public ResponseEntity<Map<String , List<StockResponseDTO>>> getStocksByProductSortedFifo(@PathVariable Long id){
        return ResponseEntity.ok(stockService.stocksForProductSortedFifo(id));
    }

    @GetMapping("/mouvements")
    @PreAuthorize("hasAuthority('MOUVEMENT_READ')")
    public ResponseEntity<List<MouvementStockResponseDTO>> historiqueMouvementStock(){
        return ResponseEntity.ok(stockService.historiqueMouvement());
    }

    @GetMapping("/mouvements/produit/{id}")
    @PreAuthorize("hasAuthority('MOUVEMENT_READ')")
    public ResponseEntity<List<MouvementStockResponseDTO>> historiqueMouvementProduit(@PathVariable Long id){
        return ResponseEntity.ok(stockService.historiqueMouvementStockProduit(id));
    }

    @GetMapping("/alertes")
    @PreAuthorize("hasAuthority('PRODUIT_ALERT_CONFIG')")
    public ResponseEntity<List<ProduitResponseDTO>> produitUnderThreshold(){
        return ResponseEntity.ok(stockService.produitUnderThreshold());
    }

    @GetMapping("/valorisation")
    @PreAuthorize("hasAuthority('STOCK_VALORISATION')")
    public ResponseEntity<String> valorisationStock(){
        return ResponseEntity.ok(stockService.valorisationStock());
    }

}
