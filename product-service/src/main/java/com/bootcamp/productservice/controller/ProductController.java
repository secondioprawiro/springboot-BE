package com.bootcamp.productservice.controller;

import com.bootcamp.productservice.dto.response.BaseResponse;
import com.bootcamp.productservice.dto.response.ResCreatePokemonDto;
import com.bootcamp.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/sync")
    public ResponseEntity<BaseResponse<Object>> syncData(){
        productService.syncDataByThirdPartyApi()
                .thenAccept(result -> log.info("Update dari background: {}", result))
                .exceptionally(ex ->{
                    log.error("Update dari background: {}", ex.getMessage());
                    return null;
                });
        return ResponseEntity.ok(BaseResponse.success(null, "Permintaan sedang diproses"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ResCreatePokemonDto>> getProductById(
        @PathVariable String id
    ){
        ResCreatePokemonDto resCreatePokemonDto =productService.getPokemonById(id);
        return ResponseEntity.ok(BaseResponse.success(resCreatePokemonDto, "Get pokemon ID: " + id));
    }
}
