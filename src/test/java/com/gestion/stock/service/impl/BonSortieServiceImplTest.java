package com.gestion.stock.service.impl;

import com.gestion.stock.dto.request.BonSortieRequestDto;
import com.gestion.stock.entity.*;
import com.gestion.stock.enums.*;
import com.gestion.stock.dto.response.*;
import com.gestion.stock.mapper.BonSortieItemMapper;
import com.gestion.stock.mapper.BonSortieMapper;
import com.gestion.stock.mapper.StockToMouvementMapper;
import com.gestion.stock.repository.*;
import com.gestion.stock.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BonSortieServiceImplTest {

    @InjectMocks
    private BonSortieServiceImpl bonSortieService;

    @Mock private BonSortieRepository bonSortieRepository;
    @Mock private ProduitRepository produitRepository;
    @Mock private MouvementStockRepository mouvementStockRepository;
    @Mock private StockRepository stockRepository;
    @Mock private BonSortieMapper bonSortieMapper;
    @Mock private StockToMouvementMapper stockToMouvementMapper;
    @Mock private BonSortieItemMapper bonSortieItemMapper;

    @Mock private StockService stockService;

    private Produit produit;
    private BonSortie bonSortie;
    private BonSortieItem item;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        produit = new Produit();
        produit.setId(1L);
        produit.setStockActuel(100);

        item = new BonSortieItem();
        item.setProduit(produit);
        item.setQuantite(0);

        bonSortie = new BonSortie();
        bonSortie.setId(1L);
        bonSortie.setStatut(StatutBonSortie.BROUILLON);
        bonSortie.setItems(List.of(item));
    }


    @Test
    void testSortiePartielleUnSeulLot() {
        item.setQuantite(20); // demande 20

        Stock stock1 = new Stock();
        stock1.setNumeroLot("Lot-001");
        stock1.setQuantiteActuel(50);
        stock1.setPrixAchat(20D);
        stock1.setProduit(produit);
        stock1.setDateEntre(LocalDateTime.now().minusDays(5));

        Stock stock2 = new Stock();
        stock2.setNumeroLot("Lot-002");
        stock2.setQuantiteActuel(100);
        stock2.setPrixAchat(22D);
        stock2.setProduit(produit);
        stock2.setDateEntre(LocalDateTime.now().minusDays(5));

        when(bonSortieRepository.findById(1L)).thenReturn(Optional.of(bonSortie));
        when(stockRepository.findAll()).thenReturn(List.of(stock1,stock2));
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));

        when(stockToMouvementMapper.toMouvementSortie(any(Stock.class))).thenAnswer(invocation -> {
            Stock stock = invocation.getArgument(0);

            MouvementStock m = new MouvementStock();
            m.setStock(stock);
            m.setQuantite(item.getQuantite());
            m.setTypeMouvement(TypeMouvement.SORTIE);
            m.setDateMouvement(LocalDateTime.now());

            return m;
        });

        Map<String, Object> result = bonSortieService.updateBonSortieToValider(1L);

        assertEquals(30, stock1.getQuantiteActuel());
        assertEquals("VALIDE", result.get("status"));
    }


    @Test
    void testSortiePlusieursLots() {
        item.setQuantite(50);

        Stock lotA = new Stock();
        lotA.setId(1L);
        lotA.setNumeroLot("Lot-A");
        lotA.setProduit(produit);
        lotA.setQuantiteActuel(30);
        lotA.setPrixAchat(30D);
        lotA.setDateEntre(LocalDateTime.now().minusDays(10));

        Stock lotB = new Stock();
        lotB.setId(2L);
        lotB.setNumeroLot("Lot-B");
        lotB.setProduit(produit);
        lotB.setQuantiteActuel(40);
        lotB.setPrixAchat(40D);
        lotB.setDateEntre(LocalDateTime.now().minusDays(5));

        when(bonSortieRepository.findById(1L)).thenReturn(Optional.of(bonSortie));
        when(stockRepository.findAll()).thenReturn(List.of(lotA, lotB));
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));

        when(stockToMouvementMapper.toMouvementSortie(any(Stock.class))).thenAnswer(invocation -> {
            Stock stock = invocation.getArgument(0);
            MouvementStock mouvement = new MouvementStock();
            mouvement.setStock(stock);
            mouvement.setQuantite(Math.min(item.getQuantite(), stock.getQuantiteActuel()));
            mouvement.setTypeMouvement(TypeMouvement.SORTIE);
            mouvement.setDateMouvement(LocalDateTime.now());
            return mouvement;
        });

        Map<String, Object> response = bonSortieService.updateBonSortieToValider(1L);

        assertEquals(0, lotA.getQuantiteActuel());
        assertEquals(20, lotB.getQuantiteActuel());
        assertEquals("VALIDE", response.get("status"));
    }


    @Test
    void testStockInsuffisant() {
        item.setQuantite(35);

        Stock lotA = new Stock();
        lotA.setId(1L);
        lotA.setNumeroLot("Lot-A");
        lotA.setProduit(produit);
        lotA.setQuantiteActuel(30);
        lotA.setPrixAchat(30D);
        lotA.setDateEntre(LocalDateTime.now().minusDays(10));

        when(bonSortieRepository.findById(1L)).thenReturn(Optional.of(bonSortie));
        when(stockRepository.findAll()).thenReturn(List.of(lotA));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bonSortieService.updateBonSortieToValider(1L)
        );

        assertTrue(ex.getMessage().contains("Insufficient stock"));
    }


    @Test
    void testSortieEpuiseExactementStock() {
        item.setQuantite(50);

        Stock lotA = new Stock();
        lotA.setId(1L);
        lotA.setNumeroLot("Lot-A");
        lotA.setProduit(produit);
        lotA.setQuantiteActuel(20);
        lotA.setPrixAchat(20D);
        lotA.setDateEntre(LocalDateTime.now().minusDays(10));

        Stock lotB = new Stock();
        lotB.setId(2L);
        lotB.setNumeroLot("Lot-B");
        lotB.setProduit(produit);
        lotB.setQuantiteActuel(30);
        lotB.setPrixAchat(30D);
        lotB.setDateEntre(LocalDateTime.now().minusDays(5));

        when(bonSortieRepository.findById(1L)).thenReturn(Optional.of(bonSortie));
        when(stockRepository.findAll()).thenReturn(List.of(lotA, lotB));
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));

        Map<String, Object> res = bonSortieService.updateBonSortieToValider(1L);

        assertEquals(0, lotA.getQuantiteActuel());
        assertEquals(0, lotB.getQuantiteActuel());
        assertEquals("VALIDE", res.get("status"));
    }


    @Test
    void testCreationLotDepuisCommande() {
        Produit produit = new Produit();
        produit.setId(1L);
        produit.setNom("Produit Test");
        produit.setStockActuel(0);

        Commande commande = new Commande();
        commande.setId(1L);

        DetailsCommande details = new DetailsCommande();
        details.setProduit(produit);
        details.setQuantite(50);
        details.setPrix(20D);
        details.setCommande(commande); // <-- Liaison à la commande

        commande.setDetailsCommandes(List.of(details));

        when(stockService.createStockLotsAndMouvement(anyList())).thenAnswer(invocation -> {
            List<DetailsCommande> list = invocation.getArgument(0);
            StockResponseDTO dto = new StockResponseDTO();
            dto.setQuantiteActuel(list.get(0).getQuantite());
            dto.setNomProduit(list.get(0).getProduit().getNom());
            dto.setCommandeId(list.get(0).getCommande() != null ? list.get(0).getCommande().getId() : null);
            return List.of(dto);
        });

        List<StockResponseDTO> stocks = stockService.createStockLotsAndMouvement(commande.getDetailsCommandes());

        assertEquals(1, stocks.size());
        assertEquals(50, stocks.get(0).getQuantiteActuel());
        assertEquals(produit.getNom(), stocks.get(0).getNomProduit());
        assertEquals(commande.getId(), stocks.get(0).getCommandeId());
    }



    @Test
    void testCalculValorisationStockFIFO() {
        Long produitId = 1L;

        StockResponseDTO lot1 = new StockResponseDTO();
        lot1.setQuantiteActuel(10);
        lot1.setPrixAchat(20D);

        StockResponseDTO lot2 = new StockResponseDTO();
        lot2.setQuantiteActuel(5);
        lot2.setPrixAchat(25D);

        when(stockService.stocksForProductSortedFifo(produitId))
                .thenReturn(Map.of("stocks", List.of(lot1, lot2)));

        Map<String, List<StockResponseDTO>> stocksMap = stockService.stocksForProductSortedFifo(produitId);
        List<StockResponseDTO> stocks = stocksMap.get("stocks");

        double valorisation = stocks.stream()
                .mapToDouble(s -> s.getQuantiteActuel() * s.getPrixAchat())
                .sum();

        assertEquals(325D, valorisation, 0.001, "La valorisation du stock selon FIFO est incorrecte");
    }


}
